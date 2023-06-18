package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.ActivityMainBinding
import com.example.finalproject.databinding.ActivityRecipeByMeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeByMeActivity : AppCompatActivity() {

    lateinit var binding: ActivityRecipeByMeBinding

    lateinit var recipeListDB: RecipeDatabase
    lateinit var recipe_Adapter: recipeAdapter

    private val Ingredient_DBHelper: IngredientDBHelper by lazy { IngredientDBHelper(this) }


    //receivedMyRecipeSet은 DB에 저장되어 있는 모든 레시피의 정보들을 저장할 것이다
    //receviedMyIngredient는 DB에 저장되어 있는 사용자가 입력해 놓은 재료들의 정보를 저장할 것임
    var receivedMyRecipeSet = ArrayList<recipeData>()
    var receivedIngredientSet = ArrayList<Ingredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRecipeByMeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //DB를 가져와 사용할 것이다
        recipeListDB = RecipeDatabase.getDatabase(this)

        initLayout()
        //앱 상단에 기본적으로 표시되는 작업 표시줄을 가려주기 위한 구문
        supportActionBar?.hide()
    }

    private fun setRecyclerView()
    {
        //recyclerView 어댑터와 layoutManager를 생성하고 세팅한다
        //requireContext() 함수를 통해 현재 Fragment의 context 정보를 가져온다
        binding.wholeRecipeRecyclerViewInMyRecipe.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)


        CoroutineScope(Dispatchers.IO).launch {
            getMyRecipes()
        }

        recipe_Adapter = recipeAdapter(receivedMyRecipeSet, receivedIngredientSet)

        //재료 추가 버튼을 누를 경우 나만의 레시피를 추가하는 화면으로 넘어간다
        binding.addRecipe.setOnClickListener {
            val intent = Intent(this@RecipeByMeActivity, AddMyRecipeActivity::class.java)
            startActivity(intent)
        }

        //나만의 레시피가 있는 경우 띄워지는 추가 버튼을 따로 처리해준다
        binding.addRecipeWhenVisible.setOnClickListener {
            val intent = Intent(this@RecipeByMeActivity, AddMyRecipeActivity::class.java)
            startActivity(intent)
        }

        //RecyclerView에게 우리가 정의한 adapter를 이용하여 화면에 띄우라고 알려주는 구문
        binding.wholeRecipeRecyclerViewInMyRecipe.adapter = recipe_Adapter
    }

    private fun initLayout()
    {
        setRecyclerView()
        binding.backBtnInMyRecipe.setOnClickListener {
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
                Toast.makeText(this@RecipeByMeActivity, "재료 리스트가 비어 있습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //나만의 레시피들을 가져온다
    fun getMyRecipes()
    {
        receivedMyRecipeSet = recipeListDB.recipeData_DAO().findMyRecipe() as ArrayList<recipeData>
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
                    Toast.makeText(this@RecipeByMeActivity, "재료가 들어가 있지 않아 정렬되지 않습니다", Toast.LENGTH_SHORT).show()
                }
            }

            //IO에서 Main으로 잠시 switch 해서 notifyDataSetChanged()를 호출해야 한다
            CoroutineScope(Dispatchers.Main).launch{
                recipe_Adapter.notifyDataSetChanged()
            }
            binding.searchingInMyRecipe.visibility = View.VISIBLE
            binding.whenMyRecipeEmpty.visibility = View.GONE
            binding.wholeRecipeRecyclerViewInMyRecipe.visibility = View.VISIBLE
            binding.addRecipeWhenVisible.visibility = View.VISIBLE
        }
        else //데이터가 유효하지 않다면
        {
            binding.searchingInMyRecipe.visibility = View.GONE
            binding.whenMyRecipeEmpty.visibility = View.VISIBLE
            binding.wholeRecipeRecyclerViewInMyRecipe.visibility = View.GONE
            binding.addRecipeWhenVisible.visibility = View.GONE
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
}