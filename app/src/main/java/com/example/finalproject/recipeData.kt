package com.example.finalproject

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "recipeListTable")
//recipe 정보에 대한 Data 정보 (likedRecipe 기본 값은 false(0))
data class recipeData(
    @PrimaryKey var recipe_name: String,
    @ColumnInfo(name = "recipe_completePicture_url") var recipe_CompletePicture_url: String,
    @ColumnInfo(name = "recipe_processPictures_url") var recipeProcess_pictures_url: List<String>,
    @ColumnInfo(name = "recipe_Process") var recipe_process: List<String>,
    @ColumnInfo(name = "recipe_Stuff") var recipe_stuff: List<String>,
    @ColumnInfo(name = "recipe_StuffCount") var recipe_stuff_count: List<Int>,
    @ColumnInfo(name = "recipe_StuffCountUnit") var recipe_StuffCountUnit: List<String>,
    @ColumnInfo(name = "favorite_Recipe") var likedRecipe: Int = 0,
    @ColumnInfo(name = "isMine") var isMineRecipe: Int = 0):Serializable
