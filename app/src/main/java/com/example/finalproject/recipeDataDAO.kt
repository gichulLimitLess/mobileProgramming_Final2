package com.example.finalproject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface recipeDataDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRecipe(recipeData: recipeData)

    @Delete
    fun deleteRecipe(recipeData: recipeData)

    @Update
    fun updateRecipe(recipeData: List<recipeData>)

    @Query("Select * from recipeListTable")
    fun getAllRecord(): List<recipeData>

    @Query("Select * from recipeListTable where recipe_name = :name")
    fun findRecipe(name: String): recipeData

    @Query("Select * from recipeListTable where recipe_name like :name")
    fun findSimilarRecipe(name: String): List<recipeData>

    @Query("Select * from recipeListTable where isMine = 1")
    fun findMyRecipe(): List<recipeData>

    @Query("Select * from recipeListTable where favorite_Recipe = 1")
    fun findLikedRecipe(): List<recipeData>
}