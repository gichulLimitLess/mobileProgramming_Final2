package com.example.finalproject

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.example.finalproject.databinding.ActivityAddMyRecipeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.File

class AddMyRecipeActivity : AppCompatActivity() {

    private lateinit var recipeStuffContainer: LinearLayout
    private lateinit var recipeProcessContainer: LinearLayout

    //각각의 요소들은 최대 100개까지만 만들 수 있도록 제한해야 할 것이다 (offset이 각 100이므로)
    private var editTextViewCount_Top_Name = 0
    private var editTextViewCount_Top_Count = 0
    private var spinnerViewCount = 100
    private var editTextViewCount_Bottom = 200
    private var ingredientSet_Count = 1000
    private var processSet_Count = 2000

    private val processImageViewMap: MutableMap<Int, Int> = mutableMapOf() //ImageView의 ID와 position 값들을 저장하기 위해 선언한 Map

    lateinit var binding: ActivityAddMyRecipeBinding
    lateinit var recipeListDB: RecipeDatabase
    lateinit var inputMyRecipeData: recipeData

    var processInCount = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddMyRecipeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initLayout()
        //앱 상단에 기본적으로 표시되는 작업 표시줄을 가려주기 위한 구문
        supportActionBar?.hide()

    }

    //레이아웃 초기화
    private fun initLayout() {
        recipeStuffContainer = binding.AddNeededIngrdientContainer
        recipeProcessContainer = binding.containerOfRecipeProcessInAddMyRecipe

        //DB 가져와서 사용할 것이다
        recipeListDB = RecipeDatabase.getDatabase(this)

        //뒤로 가기 버튼을 눌렀을 때
        binding.backBtn.setOnClickListener {
            //현재 activity를 닫고, 스택에 존재하는 이전 activity를 가져온다
            finish()
        }

        //재료 추가하기 버튼을 눌렀을 때
        binding.addIngrdientInMyRecipe.setOnClickListener {
            onIngredientAddButtonClicked()
        }

        //과정 정보 추가하기 버튼을 눌렀을 때
        binding.addProcessInMyRecipe.setOnClickListener {
            onProcessAddButtonClicked()
        }

        //재료 대표 이미지를 추가하려고 할 때
        binding.recipeCompletedImageInAddMyRecipe.setOnClickListener {
            launchGallery_ForRepresentativeImage()
        }

        //추가 버튼을 눌렀을 때
        binding.addMyRecipeBtn.setOnClickListener {
            val flag = onAddButtonClicked()
            //채워질 editText가 다 채워져 있다면
            if (flag) {
                //현재 activity를 닫고, 스택에 존재하는 이전 activity를 가져온다
                finish()
            }
            else //채워야 할 게 남아 있다면
            {
                Toast.makeText(
                    this@AddMyRecipeActivity,
                    "최소한 영역별로 1개 이상씩 빈칸을 채워주세요",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //과정 추가 행을 동적으로 추가하는 버튼을 눌렀을 때 시도하는 동작
    fun onProcessAddButtonClicked() {
        processSet_Count++

        val viewLayout = createViewLayout_inProcess()

        //createViewLayout()에서 만든 viewLayout을 Container에 저장한다
        recipeProcessContainer.addView(viewLayout)

    }

    //재료 추가 행을 동적으로 추가하는 버튼을 눌렀을 때 시도하는 동작
    fun onIngredientAddButtonClicked() {
        ingredientSet_Count++

        val viewLayout = createViewLayout_inIngredient()

        //createViewLayout()에서 만든 viewLayout을 Container에 저장한다
        recipeStuffContainer.addView(viewLayout)
    }

    private fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }

    private fun createViewLayout_inIngredient(): LinearLayout {

        //아래 3개의 view들을 묶어서 저장할 LinearLayout을 선언
        val viewLayout = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val marginInDpHorizontal = dpToPx(20)
        val marginInDpTop = dpToPx(10)
        layoutParams.setMargins(marginInDpHorizontal, marginInDpTop, marginInDpHorizontal, 0)
        viewLayout.layoutParams = layoutParams
        viewLayout.orientation = LinearLayout.HORIZONTAL

        //재료명을 입력하는 EditText를 추가한다
        val editText = EditText(this)
        editText.id = editTextViewCount_Top_Name++
        editText.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            5f //weight 값으로 5를 준다
        )
        editText.gravity = Gravity.CENTER
        editText.hint = "재료명을 입력해주세요"
        editText.textSize = 15f
        viewLayout.addView(editText)

        //수량을 입력하는 EditText를 추가한다
        val editText2 = EditText(this)
        editText2.id = editTextViewCount_Top_Count++
        editText2.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            2f //weight 값으로 2를 준다
        )
        editText2.gravity = Gravity.CENTER
        editText2.hint = "수량"
        editText2.textSize = 18f
        viewLayout.addView(editText2)

        //수량의 단위를 입력하는 Spinner를 추가한다
        val spinner = Spinner(this)
        spinner.id = spinnerViewCount++
        spinner.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            3f //weight 값으로 3을 준다
        )
        spinner.gravity = Gravity.CENTER_VERTICAL
        val spinnerValues = resources.getStringArray(R.array.count_unitArray)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerValues)
        spinner.adapter = spinnerAdapter
        viewLayout.addView(spinner)

        //만들어낸 viewLayout을 돌려준다
        return viewLayout

    }

    private fun createViewLayout_inProcess(): LinearLayout {
        //아래 3개의 view들을 묶어서 저장할 LinearLayout을 선언
        val viewLayout = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val marginInDpHorizontal = dpToPx(16)
        val marginInDpTop = dpToPx(6)
        layoutParams.setMargins(marginInDpHorizontal, marginInDpTop, marginInDpHorizontal, 0)
        viewLayout.layoutParams = layoutParams
        viewLayout.orientation = LinearLayout.VERTICAL

        //과정 정보 번호를 추가한다
        val fixedText = TextView(this)
        fixedText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        fixedText.text = processInCount.toString() + "번"
        fixedText.textSize = 15f
        processInCount++
        viewLayout.addView(fixedText)

        //과정 별 사진을 추가할 수 있도록 한다
        val processImageView = ImageView(this)
        processImageView.id = View.generateViewId() //고유한 id를 지정하여 저장한다

        val processImageView_height = dpToPx(150)
        val layoutParams2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, processImageView_height
        )

        val marginInDpHorizontal2 = dpToPx(10)
        val marginInDpVertical2 = dpToPx(6)
        layoutParams2.setMargins(marginInDpHorizontal2, marginInDpVertical2, marginInDpHorizontal2, marginInDpVertical2)
        processImageView.layoutParams = layoutParams2

        processImageView.setImageResource(R.drawable.noneofdata_recipe_data) //기본 이미지 입력
        processImageView.tag = "" //순서대로 URI를 기록하기 위한 tag 정보.. 초기화

        //processImageView의 id와 그의 position 값을 저장한다
        processImageViewMap[processImageView.id] = processImageViewMap.size

        //image 클릭에 대한 이벤트 처리할 것임
        processImageView.setOnClickListener {

            //해당 id에 저장되어 있는 position 값을 이용해서
            val position = processImageViewMap[processImageView.id]

            //position 값이 null이 아니면
            if (position != null) {
                handleImageViewClick(position)
            }
        }
        viewLayout.addView(processImageView)


        //과정 입력란을 추가한다
        val editText = EditText(this)
        editText.id = editTextViewCount_Bottom++

        val InputBox_height = dpToPx(100)
        editText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, InputBox_height
        )
        editText.gravity = Gravity.CENTER
        editText.hint = "레시피 조리 방법을 입력해 주세요"
        editText.textSize = 15f
        editText.setBackgroundResource(R.drawable.edittext_border)
        viewLayout.addView(editText)

        //만들어낸 viewLayout을 돌려준다
        return viewLayout

    }

    //동적으로 생성되는 imageView에 따른 위치 정보를 저장하기 위해 position 값이 필요하다
    private var selectedImagePosition: Int = -1

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, launch the gallery
            launchGallery()
        } else {
            // Permission denied, handle accordingly (e.g., show a message)
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    //갤러리를 실행시키기 위한 launchGallery
    private fun launchGallery() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"

        try {
            galleryLauncher.launch(galleryIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "갤러리 앱이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    //대표 이미지에서의 갤러리 실행을 위한 launchGallery 함수
    private fun launchGallery_ForRepresentativeImage() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"

        try {
            galleryLauncher_forFixedImageView.launch(galleryIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "갤러리 앱이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }

    //URI로부터 절대 경로를 받아오기 위한 함수
    fun getAbsolutePathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    //과정 정보들에 사진 추가하는 것을 위한 galleryLauncher
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { selectedImageUri ->
                val position = selectedImagePosition

                if (position == -1) {
                    Toast.makeText(this, "해당 imageView의 position 정보를 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                val imageViewId = processImageViewMap.filterValues { it == position }.keys.firstOrNull()
                val imageView = imageViewId?.let { findViewById<ImageView>(it) }

                if (imageView != null) {
                    val absolutePath = getAbsolutePathFromUri(this, selectedImageUri)
                    if (absolutePath != null) {
                        //이미지 파일 절대 경로를 이용하여 Glide에 적용한다
                        Glide.with(this@AddMyRecipeActivity)
                            .load(File(absolutePath))
                            .placeholder(android.R.color.transparent) // Use a transparent placeholder
                            .centerCrop()
                            .into(imageView)

                        //해당 절대 경로를 imageView의 tag 값으로 집어 넣는다 (DB에 순서대로 저장하기 위해서)
                        imageView.tag = absolutePath
                    } else {
                        Toast.makeText(this, "파일 절대 경로를 가져오는 데에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }

                //이미지 절대경로를 잘 불러 오는지 확인한다
                if (imageView != null) {
                    Toast.makeText(this, imageView.tag.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //고정된 imageView(맨 위의 대표 사진)에 대한 galleryLauncher
    private val galleryLauncher_forFixedImageView = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { selectedImageUri ->
                val imageView = binding.recipeCompletedImageInAddMyRecipe

                val absolutePath = getAbsolutePathFromUri(this, selectedImageUri)
                if (absolutePath != null) {
                    // Apply to Glide using the absolute path of the image file
                    Glide.with(this@AddMyRecipeActivity)
                        .load(File(absolutePath))
                        .placeholder(android.R.color.transparent) // Use a transparent placeholder
                        .centerCrop()
                        .into(imageView)

                    // Insert the absolute path as the tag value of imageView (to save in DB in order)
                    imageView.tag = absolutePath
                } else {
                    Toast.makeText(this, "Failed to get file absolute path", Toast.LENGTH_SHORT).show()
                }

                // Check if the absolute path of the image is loaded correctly
                Toast.makeText(this, imageView.tag.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }



    //동적으로 생성되는 ImageView를 Click 했을 때의 이벤트 처리
    private fun handleImageViewClick(position: Int) {

        //position 값을 함수 외부의 변수에 저장하여 galleryLauncher에서도 사용할 수 있게 한다
        selectedImagePosition = position

        // Check permissions before launching the gallery
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            launchGallery()
        } else {
            // Request permissions
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    //과정 정보들에 대한 이미지 URI들을 String으로 변환 하여 저장할 것이다
    var processImagesURL = mutableListOf<String>()
    fun onAddButtonClicked(): Boolean {
        var myRecipeIngredients = mutableListOf<String>()
        var myRecipeIngredients_Count = mutableListOf<Int>()
        var spinnerValues = mutableListOf<String>()
        var processValues = mutableListOf<String>()

        var isCanAdded = 0

        //재료 정보들 가져온다
        for (i in 0 until recipeStuffContainer.childCount) {
            val viewLayout = recipeStuffContainer.getChildAt(i) as LinearLayout
            val editText = viewLayout.getChildAt(0) as EditText
            val editText2 = viewLayout.getChildAt(1) as EditText
            val spinner = viewLayout.getChildAt(2) as Spinner

            if (editText.text.isEmpty() || editText2.text.isEmpty())
                continue

            myRecipeIngredients.add(editText.text.toString())
            myRecipeIngredients_Count.add(editText2.text.toString().toInt())
            spinnerValues.add(spinner.selectedItem.toString())
            isCanAdded++
        }

        //재료 정보가 하나라도 입력 되있지 않다면
        if (isCanAdded == 0) {
            return false
        }
        isCanAdded = 0

        //과정 정보들 가져온다
        for (i in 0 until recipeProcessContainer.childCount) {
            val viewLayout = recipeProcessContainer.getChildAt(i) as LinearLayout
            val processImageView = viewLayout.getChildAt(1) as ImageView
            val editText3 = viewLayout.getChildAt(2) as EditText

            if (editText3.text.isEmpty())
                continue

            processValues.add(editText3.text.toString())
            isCanAdded++

            //imageView의 태그 정보를 가져와 해당 uri가 null 값이 아니면, processImagesURL에 저장한다
            val imageUri = processImageView.tag.toString()
            if(imageUri != null)
            {
                processImagesURL.add(imageUri)
            }
        }
        //과정 정보가 하나라도 입력 되있지 않다면
        if (isCanAdded == 0) {
            return false
        }


        //나머지 정보들 추가
        val recipeName = binding.inputRecipeName.text.toString()
        val url1 = binding.recipeCompletedImageInAddMyRecipe.tag.toString()
        val recipeProcessPicturesURI = processImagesURL
        val favoriteRecipe = 0
        val isMine = 1

        inputMyRecipeData = recipeData(recipeName, url1, recipeProcessPicturesURI, processValues, myRecipeIngredients,
            myRecipeIngredients_Count, spinnerValues, favoriteRecipe, isMine
        )

        //DB에 삽입
        CoroutineScope(Dispatchers.IO).launch {
            recipeListDB.recipeData_DAO().insertRecipe(inputMyRecipeData)
        }

        return true
    }
}