package com.example.finalproject

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.finalproject.databinding.ActivityRecipeDetailBinding
import java.net.HttpURLConnection
import java.net.URL

class recipeDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecipeDetailBinding

    //테스트용 데이터 (추후 넘겨받은 데이터와 자신이 가지고 있는 재료의 계산을 통해 데이터 가공할 예정)
    var needed_List: ArrayList<NeededIngredientsData> = arrayListOf(
        NeededIngredientsData("당근", 5),
        NeededIngredientsData("오이", 4),
        NeededIngredientsData("강낭콩", 7),
        NeededIngredientsData("불닭소스", 200),
        NeededIngredientsData("마요네즈", 100),
        NeededIngredientsData("케첩", 3),
        NeededIngredientsData("양파", 10),
        NeededIngredientsData("북어채", 7)
    )

    //테스트용 데이터 2
    lateinit var testProcess: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getDataFromRecipeFragmentAndSet()
        //앱 상단에 기본적으로 표시되는 작업 표시줄을 가려주기 위한 구문
        supportActionBar?.hide()
    }

    fun getDataFromRecipeFragmentAndSet()
    {
        val intent = intent
        val containerLayout = binding.containerOfRecipeProcess
        val containerLayout_neededIngredientsList = binding.containerOfNeededIngredients

        //getSerizableExtra가 Deprecated 되어서, 이를 해결하기 위한 방법 (RecipeFragment로부터 intent로 데이터를 넘겨 받는 작업)
        @Suppress("DEPRECATION")
        val receivedRecipeData = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra("recipeData", recipeData::class.java)
        else
            intent.getSerializableExtra("recipeData") as recipeData

        //for문을 돌리면서 넘겨 받은 필요한 재료(Ingredient) 정보 저장
        if (receivedRecipeData != null) {
            for(needed_List in receivedRecipeData.recipe_stuff)
            {
                val textView = TextView(this)
                textView.text = needed_List
                textView.setTextColor(Color.BLACK)
                textView.textSize = 18F
                containerLayout_neededIngredientsList.addView(textView)
            }
        }

        var count = 0
        //for문을 돌리면서 넘겨 받은 과정(Process) text 정보 저장
        if (receivedRecipeData != null) {
            for(processString in receivedRecipeData.recipe_process)
            {
                //textView 추가
                val textView = TextView(this)
                textView.text = processString
                textView.setTextColor(Color.BLACK)
                textView.textSize = 18F
                containerLayout.addView(textView)

                //textView 밑에 imageView(과정 정보 관련 사진) 추가
                val imageView = ImageView(this)
                val imageLayoutParams = LinearLayout.LayoutParams(130,110)
                imageLayoutParams.gravity = Gravity.LEFT
                Glide.with(this).load(receivedRecipeData.recipeProcess_pictures_url[count]).into(imageView)
                containerLayout.addView(imageView)
            }
        }


        //과정 정보의 개수에 따라 난이도를 나눌 것이다 (여기선 3개 이하 초급, 3~6개 사이 중급, 7개 이상 고급)
        if (receivedRecipeData != null) {
            if (receivedRecipeData.recipe_process.count() < 3)
                binding.difficulty.text = "초급"
            else if (receivedRecipeData.recipe_process.count() in 3..6)
            {
                binding.difficulty.text = "중급"
            }
            else if (receivedRecipeData.recipe_process.count() >= 7)
            {
                binding.difficulty.text = "고급"
            }
        }

        //불러온 url을 가지고 완성 이미지를 뿌려줄 것이다 (Glide library 활용)
        var url = receivedRecipeData!!.recipe_CompletePicture_url

        Glide.with(this)
            .load(url) //불러올 이미지 url
            .into(binding.recipeCompleteImage)

        //레시피 이름을 전달 받은 이름으로 바꿔준다
        binding.RecipeNameInRecipeDetail.text = receivedRecipeData!!.recipe_name
        binding.recipeFixedTextInRecipeDetail.text = receivedRecipeData!!.recipe_name

        //뒤로 가기 버튼을 눌렀을 때
        binding.backBtn.setOnClickListener {
            //현재 activity를 닫고, 스택에 존재하는 이전 activity를 가져온다
            finish()
        }


    }


}