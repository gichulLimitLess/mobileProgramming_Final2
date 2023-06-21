package com.example.finalproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.room.Room
import com.opencsv.CSVReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class StartActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long = 2000
    lateinit var recipeListDB: RecipeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //상단의 액션 바를 숨기기 위한 구문
        supportActionBar?.hide()
        readCSVAndStoreInDatabase(this)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)

    }

    //CSV 읽어와서 데이터베이스에 저장하는 함수
    fun readCSVAndStoreInDatabase(context: Context) {
        val csvFile = context.resources.openRawResource(R.raw.recipelist) //CSV 파일을 읽어들일 준비를 한다

        val reader = BufferedReader(InputStreamReader(csvFile))
        val csvReader = CSVReader(reader)

        //SQlite 데이터 베이스를 가져온다
        recipeListDB = RecipeDatabase.getDatabase(this)

        var counter = 0

        //오류 생기면 바로 잡아낼 수 있게 잡아낸다
        try {
            //첫번째는 넘긴다
            csvReader.skip(1)

            var nextLine: Array<String>?
            while (csvReader.readNext().also { nextLine = it } != null) {

                // Process each row of the CSV file
                var recipeName = nextLine?.get(0) ?: ""
                var recipeCompletePicture_url = nextLine?.get(1) ?: ""
                var recipeProcess_pictures_url = nextLine?.get(2) ?: ""

                val cleanedString = recipeProcess_pictures_url.trim().removeSurrounding("[", "]")
                val recipeProcess_pictures_url_toList = cleanedString.split(",").map { it.trim().replace("'", "") }


                var recipe_process = nextLine?.get(3) ?: ""

                val cleanedString2 = recipe_process.trim().removeSurrounding("[", "]")
                val modifiedLine = cleanedString2
                    .replace("'", "")
                    .replace(Regex("[0-9]+\\."), "")
                val recipeProcess_toList = modifiedLine.split("., ").map { it.trim() }
                var list = mutableListOf<String>()

                for(process in recipeProcess_toList)
                {
                    val modifiedLine2 = process.replace(",","")
                    list.add(modifiedLine2)
                }

                var recipe_stuff = nextLine?.get(4) ?: ""

                val cleanedString3 = recipe_stuff.trim().removeSurrounding("[", "]")
                val values0 = cleanedString3.split(",").map { it.trim().replace("'", "") }

                var recipe_stuff_count = nextLine?.get(5) ?: ""

                val cleanedString4 = recipe_stuff_count.trim().removeSurrounding("[", "]")
                val values = cleanedString4.split(",").map { it.trim() }

                //따옴표 쳐내기
                val recipeStuffCount_toListIntVer = values.map {
                    val cleanedValue = it.trim().removeSurrounding("'")
                    cleanedValue.toIntOrNull() ?: 0
                }

                var recipe_StuffCountUnit = nextLine?.get(6) ?: ""

                val cleanedString5 = recipe_StuffCountUnit.trim().removeSurrounding("[", "]")
                val values2 = cleanedString5.split(",").map { it.trim().replace("'", "") }

                // Create an instance of your recipeData class and set the values
                val recipeData = recipeData(recipeName, recipeCompletePicture_url, recipeProcess_pictures_url_toList,
                    list, values0, recipeStuffCount_toListIntVer, values2)


                //DB에 삽입
                CoroutineScope(Dispatchers.IO).launch {
                    recipeListDB.recipeData_DAO().insertRecipe(recipeData)
                }

            }
        } catch (e: Exception) {
            Log.e("CSV Import", "Error reading CSV file: ${e.message}")
        } finally {
            // Close the CSV reader
            csvReader.close()
        }
    }
}