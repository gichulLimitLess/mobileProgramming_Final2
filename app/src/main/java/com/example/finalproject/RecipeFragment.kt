package com.example.finalproject

import android.content.Intent
import android.media.CamcorderProfile.getAll
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.insert
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.FragmentRecipeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.NullPointerException

class RecipeFragment : Fragment() {

    //임시 테스트용 Data

    lateinit var recipe_Adapter: recipeAdapter

    //DB 관련 객체와 배열
    lateinit var recipeListDB: RecipeDatabase
    private val Ingredient_DBHelper: IngredientDBHelper by lazy { IngredientDBHelper(requireContext()) }

    var receivedRecipeSet = ArrayList<recipeData>()
    var receivedIngredientSet = ArrayList<Ingredient>()


    //ViewBinding 기법을 사용하기 위해 binding 객체 하나 생성
    lateinit var binding: FragmentRecipeBinding


    //Coroutine 객체를 사용할 것임
    val scope = CoroutineScope(Dispatchers.IO)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecipeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //레아아웃 초기화
        initWholeFragment()

    }

    override fun onResume() {
        super.onResume()

        //다시 돌아왔으니 DB를 다시 가져와야 한다
        // Now that we're back, we need to get the DB again
        CoroutineScope(Dispatchers.IO).launch {
            getAllRecipes()
        }
    }

    //리사이클러뷰를 포함한 총체적인 Fragment 레이아웃 초기화
    private fun initWholeFragment()
    {
        setupRecyclerViewInRecipeFragment()

        //검색 버튼을 눌렀을 때
        binding.recipeSearchBtn.setOnClickListener {
            var recipeName = binding.recipeSearch.text.toString()

            //검색한 문자열 중 일부라도 포함하고 있으면 검색 결과에 노출될 것이다
            recipeName = "%$recipeName%"

            CoroutineScope(Dispatchers.IO).launch{
                findSimilarRecipe(recipeName)
                //insertRecipe()
            }
        }
    }

    //어댑터 생성하고 세팅하는 함수
    private fun setupRecyclerViewInRecipeFragment()
    {
        //recyclerView 어댑터와 layoutManager를 생성하고 세팅한다
        //requireContext() 함수를 통해 현재 Fragment의 context 정보를 가져온다
        binding.wholeRecipeRecyclerView.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)

        recipe_Adapter = recipeAdapter(receivedRecipeSet, receivedIngredientSet)

        //DB를 가져와 사용할 것이다
        recipeListDB = RecipeDatabase.getDatabase(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            getAllRecipes()
        }

        recipe_Adapter.itemClickListener = object: recipeAdapter.OnItemClickListener {
            override fun OnItemClick(item: recipeData, position: Int) {
                //레시피 전체 정보를 띄우는 Activity로 넘어가도록 한다
                navigateToAnotherActivity(item, position)
            }
        }
        recipe_Adapter.itemClickListener2 = object: recipeAdapter.OnItemClickListener2{
            override fun OnItemClick2(item: recipeData) {
                if(item.likedRecipe == 0){
                    item.likedRecipe = 1
                }else{
                    item.likedRecipe = 0
                }
                CoroutineScope(Dispatchers.IO).launch {
                    recipeListDB.recipeData_DAO().updateRecipe(listOf(item))
                    getAllRecipes()
                }
            }
        }

        //RecyclerView에게 우리가 정의한 adapter를 이용하여 화면에 띄우라고 알려주는 구문
        binding.wholeRecipeRecyclerView.adapter = recipe_Adapter

    }

    //레시피 전체 정보를 띄우는 Activity로 넘어가도록 하는 함수
    private fun navigateToAnotherActivity(item: recipeData, position: Int) {
        val intent = Intent(requireContext(), recipeDetailActivity::class.java)
        val message = receivedRecipeSet[position]

        Log.d("whydoesit",message.recipe_process[0])

        intent.putExtra("recipeData", message)
        startActivity(intent)
    }


    //레시피가 저장되어 있는 DB에서 정보들을 가져올 것이다 (아래 메소드 만들 것이다)
    fun getAllRecipes() {
        receivedRecipeSet = recipeListDB.recipeData_DAO().getAllRecord() as ArrayList<recipeData>
        getAllIngredients() //사용자의 재료 정보들을 IngredientDB에서 가져 온다
        recipe_Adapter.recipe_items = receivedRecipeSet

        //데이터가 유효할 때만
        if(receivedRecipeSet.isNotEmpty())
        {
            //레시피를 정렬 한다
            val flag = sorting_Recipes()
            recipe_Adapter.ingredients = receivedIngredientSet

            //IO에서 Main으로 잠시 switch 해서 notifyDataSetChanged()를 호출해야 한다
            CoroutineScope(Dispatchers.Main).launch{
                recipe_Adapter.notifyDataSetChanged()
            }
        }
    }

    fun findSimilarRecipe(recipeName: String)
    {
        receivedRecipeSet = recipeListDB.recipeData_DAO().findSimilarRecipe(recipeName) as ArrayList<recipeData>
        recipe_Adapter.recipe_items = receivedRecipeSet
        CoroutineScope(Dispatchers.Main).launch{
            recipe_Adapter.notifyDataSetChanged()
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

        }
    }

    //레시피를 정렬하는 함수
    fun sorting_Recipes(): Boolean
    {
        val list_Length = receivedRecipeSet.count()
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
        for(recipe in receivedRecipeSet)
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
                    val temp = receivedRecipeSet[j]
                    receivedRecipeSet[j] = receivedRecipeSet[j - 1]
                    receivedRecipeSet[j - 1] = temp

                    swapped = true
                }
                //필요한 재료의 종류는 같지만, 기준치를 충족하기 위한 총량도 더 적은 경우
                else if (needed_IngredientTypes[j] == needed_IngredientTypes[j - 1] &&
                    needed_IngredientCount[j] < needed_IngredientCount[j - 1])
                {
                    val temp = receivedRecipeSet[j]
                    receivedRecipeSet[j] = receivedRecipeSet[j - 1]
                    receivedRecipeSet[j - 1] = temp

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