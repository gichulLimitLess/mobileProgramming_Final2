package com.example.finalproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(
    entities = [recipeData::class],
    version = 3
)

@TypeConverters(convertingToSave::class)

abstract class RecipeDatabase: RoomDatabase(){
    abstract fun recipeData_DAO():recipeDataDAO

    companion object {
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase{
            val tempInstance = INSTANCE

            //기존에 이미 DB가 생성 되어있는 경우
            if(tempInstance != null)
            {
                return tempInstance
            }

            //여기로 넘어 왔으면 DB가 생성이 안 된 것이므로, 생성해준다
            val instance = Room.databaseBuilder(
                context,
                RecipeDatabase::class.java,
                "RecipeListDataBase" //DB 명칭이 될 것이다
            ).fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance
            return instance
        }
    }
}