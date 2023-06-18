package com.example.finalproject

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import com.example.finalproject.databinding.ActivityAddMyRecipeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text

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

        //추가 버튼을 눌렀을 때
        binding.addMyRecipeBtn.setOnClickListener {
            val flag = onAddButtonClicked()
            //채워질 editText가 다 채워져 있다면
            if (flag) {
                onAddButtonClicked()
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
        //아래 2개의 view들을 묶어서 저장할 LinearLayout을 선언
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
            val editText3 = viewLayout.getChildAt(1) as EditText

            if (editText3.text.isEmpty())
                continue

            processValues.add(editText3.text.toString())
            isCanAdded++
        }
        //과정 정보가 하나라도 입력 되있지 않다면
        if (isCanAdded == 0) {
            return false
        }

        //나머지 정보들 추가
        val recipeName = binding.inputRecipeName.text.toString()
        val url1 = "없음"
        val url2 = arrayListOf<String>("없음", "없음")
        val favoriteRecipe = 0
        val isMine = 1

        inputMyRecipeData = recipeData(recipeName, url1, url2, processValues, myRecipeIngredients,
            myRecipeIngredients_Count, spinnerValues, favoriteRecipe, isMine
        )

        //DB에 삽입
        CoroutineScope(Dispatchers.IO).launch {
            recipeListDB.recipeData_DAO().insertRecipe(inputMyRecipeData)
        }

        return true
    }
}