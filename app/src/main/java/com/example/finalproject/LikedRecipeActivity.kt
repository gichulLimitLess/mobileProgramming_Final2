package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.ActivityLikedRecipeBinding
import com.example.finalproject.databinding.ActivityRecipeByMeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LikedRecipeActivity : AppCompatActivity() {
    lateinit var binding: ActivityLikedRecipeBinding

    lateinit var recipeListDB: RecipeDatabase
    lateinit var recipe_Adapter: LikedRecipeAdapter

    private val Ingredient_DBHelper: IngredientDBHelper by lazy { IngredientDBHelper(this) }


    //receivedMyRecipeSet은 DB에 저장되어 있는 모든 레시피의 정보들을 저장할 것이다
    //receviedMyIngredient는 DB에 저장되어 있는 사용자가 입력해 놓은 재료들의 정보를 저장할 것임
    var receivedMyRecipeSet = ArrayList<recipeData>()
    var receivedIngredientSet = ArrayList<Ingredient>()


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLikedRecipeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //DB를 가져와 사용할 것이다
        recipeListDB = RecipeDatabase.getDatabase(this)

        //test용 dummy data
        var list = listOf(recipeData("레시피2", "null", listOf("0"), listOf("0"), listOf("0"), listOf(0), listOf("0"), 1, 0))
        //임시 정보 삽입
        CoroutineScope(Dispatchers.IO).launch {
            recipeListDB.recipeData_DAO().updateRecipe(list)
        }
        initLayout()
        //앱 상단에 기본적으로 표시되는 작업 표시줄을 가려주기 위한 구문
        supportActionBar?.hide()
    }
    private fun setRecyclerView()
    {
        //recyclerView 어댑터와 layoutManager를 생성하고 세팅한다
        //requireContext() 함수를 통해 현재 Fragment의 context 정보를 가져온다
        binding.wholeRecipeRecyclerViewInLikedRecipe.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)


        CoroutineScope(Dispatchers.IO).launch {
            getMyRecipes()
        }

        recipe_Adapter = LikedRecipeAdapter(receivedMyRecipeSet, receivedIngredientSet)




        //RecyclerView에게 우리가 정의한 adapter를 이용하여 화면에 띄우라고 알려주는 구문
        binding.wholeRecipeRecyclerViewInLikedRecipe.adapter = recipe_Adapter

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.wholeRecipeRecyclerViewInLikedRecipe)
    }

    private fun initLayout()
    {
        setRecyclerView()
        binding.backBtnInLikedRecipe.setOnClickListener {
            finish()
        }
    }

    //모든 재료의 값들을 가져 오는 함수
    fun getAllIngredients()
    {
        receivedIngredientSet.clear()
        receivedIngredientSet.addAll(Ingredient_DBHelper.getAllIngredients())

        //가져온 재료 리스트가 비어있는 경우 (DB가 비어있는 경우)
        if(receivedIngredientSet.isEmpty())
        {
            CoroutineScope(Dispatchers.Main).launch{
                Toast.makeText(this@LikedRecipeActivity, "재료 리스트가 비어 있습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //즐겨찾기의 레시피들을 가져온다
    fun getMyRecipes()
    {
        receivedMyRecipeSet = recipeListDB.recipeData_DAO().findLikedRecipe() as ArrayList<recipeData>
        getAllIngredients() //사용자의 재료 정보들을 IngredientDB에서 가져 온다
        recipe_Adapter.recipe_items = receivedMyRecipeSet

        //데이터가 유효할 때만
        if(receivedMyRecipeSet.isNotEmpty())
        {
            //레시피를 정렬 한다
            val flag = sorting_Recipes()
            recipe_Adapter.ingredients = receivedIngredientSet

            //정렬하지 못했다면
            if(!flag)
            {
                CoroutineScope(Dispatchers.Main).launch{
                    Toast.makeText(this@LikedRecipeActivity, "재료가 들어가 있지 않아 정렬되지 않습니다", Toast.LENGTH_SHORT).show()
                }
            }

            //IO에서 Main으로 잠시 switch 해서 notifyDataSetChanged()를 호출해야 한다
            CoroutineScope(Dispatchers.Main).launch{
                recipe_Adapter.notifyDataSetChanged()
                binding.searchingInLikedRecipe.visibility = View.VISIBLE
                binding.whenLikedRecipeEmpty.visibility = View.GONE
                binding.wholeRecipeRecyclerViewInLikedRecipe.visibility = View.VISIBLE
                binding.addRecipeWhenVisible.visibility = View.VISIBLE
            }
        }
        else //데이터가 유효하지 않다면
        {
            //IO에서 Main으로 잠시 switch 해서 notifyDataSetChanged()를 호출해야 한다
            CoroutineScope(Dispatchers.Main).launch{
                binding.searchingInLikedRecipe.visibility = View.GONE
                binding.whenLikedRecipeEmpty.visibility = View.VISIBLE
                binding.wholeRecipeRecyclerViewInLikedRecipe.visibility = View.GONE
                binding.addRecipeWhenVisible.visibility = View.GONE
                recipe_Adapter.recipe_items.clear()
                recipe_Adapter.notifyDataSetChanged()
            }
        }
    }

    //finish()를 통해 다시 왔을 경우 이것부터 호출된다
    override fun onRestart() {
        super.onRestart()
        initLayout()
    }

    //레시피를 정렬하는 함수
    fun sorting_Recipes(): Boolean
    {
        val list_Length = receivedMyRecipeSet.count()
        var needed_IngredientTypes =
            List(list_Length) {0}.toMutableList() //기준치를 넘지 못하는 Ingredient 종류
        var needed_IngredientCount =
            List(list_Length) {0}.toMutableList() //기준치를 충족하기 위해 더 필요한 Ingredient의 수량

        //DB에서 가져온 재료가 비어 있다면 정렬할 수 없으므로, false를 반환하고 정렬을 종료한다
        if(receivedIngredientSet.isEmpty())
            return false

        //1단계: 레시피에서 필요로 하는 기준치를 넘는 재료가 몇 개 있는지부터 계산
        //2단계: 레시피에서 필요로 하는 기준치를 넘는 재료의 갯수가 동일하면, 만족시 키기 위한 남은 총 수량이 적은 것부터 우선순위
        var recipe_sortingNumber = 0
        for(recipe in receivedMyRecipeSet)
        {
            for(userIngredient in receivedIngredientSet)
            {
                var i = 0
                //특정한 한 레시피의 갯수(또는 g)와 재료명들을 돌려가 보면서 확인한다
                for(recipe_stuff in recipe.recipe_stuff)
                {
                    //해당 재료도 가지고 있고, 기준치도 충족하는 경우
                    if(recipe_stuff == userIngredient.Iname && recipe.recipe_stuff_count[i] <= userIngredient.Quantity)
                    {
                        i++
                        continue
                    }
                    //해당 재료를 가지고는 있지만, 기준치를 충족하지 못하는 경우
                    else if(recipe_stuff == userIngredient.Iname && recipe.recipe_stuff_count[i] > userIngredient.Quantity)
                    {
                        needed_IngredientCount[recipe_sortingNumber] += recipe.recipe_stuff_count[i] - userIngredient.Quantity
                        needed_IngredientTypes[recipe_sortingNumber]++
                        i++
                    }
                    //해당 재료 자체를 가지고 있지 않은 경우
                    else if(recipe_stuff != userIngredient.Iname)
                    {
                        needed_IngredientCount[recipe_sortingNumber] += recipe.recipe_stuff_count[i]
                        needed_IngredientTypes[recipe_sortingNumber]++
                        i++
                    }
                }
            }
            recipe_sortingNumber++
        }

        //위에서 구한 수치를 이용하여 receivedRecipeSet 버블 정렬 실시
        for (pass in 0 until list_Length - 1) {
            var swapped = false

            for (j in 1 until list_Length - pass) {

                //필요한 재료의 종류가 뒤의 레시피가 더 적어서 뒤의 레시피가 더 우선순위를 갖는 경우
                if (needed_IngredientTypes[j] < needed_IngredientTypes[j - 1]) {
                    val temp = receivedMyRecipeSet[j]
                    receivedMyRecipeSet[j] = receivedMyRecipeSet[j - 1]
                    receivedMyRecipeSet[j - 1] = temp

                    swapped = true
                }
                //필요한 재료의 종류는 같지만, 기준치를 충족하기 위한 총량도 더 적은 경우
                else if (needed_IngredientTypes[j] == needed_IngredientTypes[j - 1] &&
                    needed_IngredientCount[j] < needed_IngredientCount[j - 1])
                {
                    val temp = receivedMyRecipeSet[j]
                    receivedMyRecipeSet[j] = receivedMyRecipeSet[j - 1]
                    receivedMyRecipeSet[j - 1] = temp

                    swapped = true
                }
                //필요한 재료의 종류가 앞에 레시피가 더 많으면 정렬을 바꿔줄 필요가 없으니 건드리지 않는다
            }

            //swap을 실시할 게 없다는 것은, 정렬이 완료 되었다는 것이므로, 버블 정렬을 멈춘다
            if (!swapped) {
                break
            }
        }

        //여기까지 왔으면 정상적으로 sorting에 성공한 것이므로, true를 반환하고 함수를 종료한다
        return true
    }

    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false // 드래그 앤 드롭을 사용하지 않을 경우 false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val item = recipe_Adapter.getItemAtPosition(position)
            val list = listOf(item)
            CoroutineScope(Dispatchers.IO).launch {
                item.likedRecipe = 0
                recipeListDB.recipeData_DAO().updateRecipe(list)
                getMyRecipes()
            }
        }
    }
}