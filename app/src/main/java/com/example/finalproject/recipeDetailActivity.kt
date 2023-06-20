package com.example.finalproject

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.finalproject.databinding.ActivityRecipeDetailBinding
import com.google.android.material.internal.ViewUtils.dpToPx
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class recipeDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecipeDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getDataFromRecipeFragmentAndSet()
        //뒤로 가기 버튼을 눌렀을 때
        binding.backBtn.setOnClickListener {
            //현재 activity를 닫고, 스택에 존재하는 이전 activity를 가져온다
            finish()
        }
        //앱 상단에 기본적으로 표시되는 작업 표시줄을 가려주기 위한 구문
        supportActionBar?.hide()
    }

    //dp에서 Px로 바꾸는 과정이 필요하니, 이에 관한 함수를 따로 선언한다
    private fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }

    //Data를 나만의 레시피에서 가져와서 setting 한다
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
                //textView 위에 imageView(과정 정보 관련 사진) 추가
                val imageView = ImageView(this)
                imageView.id = View.generateViewId() //고유한 id를 지정하여 저장한다

                val processImageView_height = dpToPx(150)
                val imageLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT
                    ,processImageView_height)
                imageView.layoutParams = imageLayoutParams

                // Adjust scale type to fit the image within the ImageView
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER

                //내가 만든 레시피라면.. 권한이 허용되지 않았으면 이미지를 띄울 수 없는 상황일 것이다
                if(receivedRecipeData.isMineRecipe == 1)
                {
                    //READ_EXTERNAL_STORAGE 권한이 애초에 허용이 되지 않았다면, imageView를 추가할 필요가 없다
                    if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //왜 출력이 안될까? (Permission 문제 같은데 왜 이럴까?)
                        val absolutePath = receivedRecipeData.recipeProcess_pictures_url[count]

                        Log.d("URICorrect?",absolutePath)

                        //이미지 파일 절대 경로를 이용하여 Glide에 적용한다
                        Glide.with(this@recipeDetailActivity)
                            .load(File(absolutePath))
                            .centerCrop()
                            .into(imageView)

                        containerLayout.addView(imageView)
                    }
                }
                else //기존의 레시피라면.. 원래대로 시행하면 된다
                {
                    Glide.with(this).load(receivedRecipeData.recipeProcess_pictures_url[count]).into(imageView)
                    containerLayout.addView(imageView)
                }

                //textView 추가
                val textView = TextView(this)
                textView.text = processString
                textView.setTextColor(Color.BLACK)
                textView.textSize = 18F
                containerLayout.addView(textView)


                count++
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

        //내가 만든 레시피라면.. 권한이 허용되지 않았으면 이미지를 띄울 수 없는 상황일 것이다
        if (receivedRecipeData != null) {
            if(receivedRecipeData.isMineRecipe == 1)
            {
                //READ_EXTERNAL_STORAGE 권한이 애초에 허용이 되지 않았다면, imageView를 추가할 필요가 없다
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    //이미지 파일 절대 경로를 이용하여 Glide에 적용한다
                    Glide.with(this).load(url).into(binding.recipeCompleteImage)
                }
            }
            else //기존의 레시피라면.. 원래대로 시행하면 된다
            {
                Glide.with(this).load(url).into(binding.recipeCompleteImage)
            }
        }

        //레시피 이름을 전달 받은 이름으로 바꿔준다
        binding.RecipeNameInRecipeDetail.text = receivedRecipeData!!.recipe_name
        binding.recipeFixedTextInRecipeDetail.text = receivedRecipeData!!.recipe_name

    }

}