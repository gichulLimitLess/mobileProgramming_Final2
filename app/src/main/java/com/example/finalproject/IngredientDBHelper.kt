package com.example.finalproject

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

class IngredientDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "IngredientDatabase.db"
        object IngredientContract {
            object IngredientEntry : BaseColumns {
                const val TABLE_NAME = "ingredients"
                const val COLUMN_NAME = "name"
                const val COLUMN_QUANTITY = "quantity"
                const val COLUMN_BUY_YEAR = "buyYear"
                const val COLUMN_BUY_MONTH = "buyMonth"
                const val COLUMN_BUY_DAY = "buyDay"
                const val COLUMN_END_YEAR = "endYear"
                const val COLUMN_END_MONTH = "endMonth"
                const val COLUMN_END_DAY = "endDay"
                const val COLUMN_UNIT = "unit"
                const val _ID = BaseColumns._ID
            }
        }
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${IngredientContract.IngredientEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${IngredientContract.IngredientEntry.COLUMN_NAME} TEXT," +
                    "${IngredientContract.IngredientEntry.COLUMN_QUANTITY} INTEGER," +
                    "${IngredientContract.IngredientEntry.COLUMN_BUY_YEAR} INTEGER," +
                    "${IngredientContract.IngredientEntry.COLUMN_BUY_MONTH} INTEGER," +
                    "${IngredientContract.IngredientEntry.COLUMN_BUY_DAY} INTEGER," +
                    "${IngredientContract.IngredientEntry.COLUMN_END_YEAR} INTEGER," +
                    "${IngredientContract.IngredientEntry.COLUMN_END_MONTH} INTEGER," +
                    "${IngredientContract.IngredientEntry.COLUMN_END_DAY} INTEGER," +
                    "${IngredientContract.IngredientEntry.COLUMN_UNIT} INTEGER)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${IngredientContract.IngredientEntry.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    fun insertIngredient(ingredient: Ingredient) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(IngredientContract.IngredientEntry.COLUMN_NAME, ingredient.Iname)
            put(IngredientContract.IngredientEntry.COLUMN_QUANTITY, ingredient.Quantity)
            put(IngredientContract.IngredientEntry.COLUMN_BUY_YEAR, ingredient.BuyYear)
            put(IngredientContract.IngredientEntry.COLUMN_BUY_MONTH, ingredient.BuyMonth)
            put(IngredientContract.IngredientEntry.COLUMN_BUY_DAY, ingredient.BuyDay)
            put(IngredientContract.IngredientEntry.COLUMN_END_YEAR, ingredient.EndYear)
            put(IngredientContract.IngredientEntry.COLUMN_END_MONTH, ingredient.EndMonth)
            put(IngredientContract.IngredientEntry.COLUMN_END_DAY, ingredient.EndDay)
            put(IngredientContract.IngredientEntry.COLUMN_UNIT, ingredient.Unit)
        }
        db.insert(IngredientContract.IngredientEntry.TABLE_NAME, null, values)
    }
    fun getAllIngredients(): ArrayList<Ingredient> {
        val db = this.readableDatabase
        val projection = arrayOf(
            IngredientContract.IngredientEntry.COLUMN_NAME,
            IngredientContract.IngredientEntry.COLUMN_QUANTITY,
            IngredientContract.IngredientEntry.COLUMN_BUY_YEAR,
            IngredientContract.IngredientEntry.COLUMN_BUY_MONTH,
            IngredientContract.IngredientEntry.COLUMN_BUY_DAY,
            IngredientContract.IngredientEntry.COLUMN_END_YEAR,
            IngredientContract.IngredientEntry.COLUMN_END_MONTH,
            IngredientContract.IngredientEntry.COLUMN_END_DAY,
            IngredientContract.IngredientEntry.COLUMN_UNIT,
            IngredientContract.IngredientEntry._ID
        )
        val cursor = db.query(
            IngredientContract.IngredientEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val ingredients = ArrayList<Ingredient>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(IngredientContract.IngredientEntry._ID))
                val name = getString(getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_NAME))
                val quantity = getInt(getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_QUANTITY))
                val buyYear = getInt(getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_BUY_YEAR))
                val buyMonth = getInt(getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_BUY_MONTH))
                val buyDay = getInt(getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_BUY_DAY))
                val endYear = getInt(getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_END_YEAR))
                val endMonth = getInt(getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_END_MONTH))
                val endDay = getInt(getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_END_DAY))
                val unit = getString(getColumnIndexOrThrow(IngredientContract.IngredientEntry.COLUMN_UNIT))
                ingredients.add(Ingredient(id, name, quantity, buyYear, buyMonth, buyDay, endYear, endMonth, endDay, unit))
            }
        }
        return ingredients
    }
    fun clearAllIngredients() {
        val db = this.writableDatabase
        db.delete(IngredientContract.IngredientEntry.TABLE_NAME, null, null)
    }
    fun deleteIngredient(id: Int) {
        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} LIKE ?"
        val selectionArgs = arrayOf(id.toString())
        db.delete(IngredientContract.IngredientEntry.TABLE_NAME, selection, selectionArgs)
    }
    fun updateIngredient(ingredient: Ingredient) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(IngredientContract.IngredientEntry.COLUMN_NAME, ingredient.Iname)
            put(IngredientContract.IngredientEntry.COLUMN_QUANTITY, ingredient.Quantity)
            put(IngredientContract.IngredientEntry.COLUMN_BUY_YEAR, ingredient.BuyYear)
            put(IngredientContract.IngredientEntry.COLUMN_BUY_MONTH, ingredient.BuyMonth)
            put(IngredientContract.IngredientEntry.COLUMN_BUY_DAY, ingredient.BuyDay)
            put(IngredientContract.IngredientEntry.COLUMN_END_YEAR, ingredient.EndYear)
            put(IngredientContract.IngredientEntry.COLUMN_END_MONTH, ingredient.EndMonth)
            put(IngredientContract.IngredientEntry.COLUMN_END_DAY, ingredient.EndDay)
            put(IngredientContract.IngredientEntry.COLUMN_UNIT, ingredient.Unit)
        }
        val selection = "${IngredientContract.IngredientEntry._ID} = ?"
        val selectionArgs = arrayOf(ingredient.id.toString())
        db.update(
            IngredientContract.IngredientEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
    }
}