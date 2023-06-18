package com.example.finalproject

import androidx.room.TypeConverter

//sqlite 자체에서는 복잡한 형의 자료들을 저장할 수 없으니, 형 변환을 해야 한다 (따로 클래스 선언이 필요합니다)
class convertingToSave {
    @TypeConverter
    fun fromListToString(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromStringToList(string: String): List<String> {
        return string.split(",")
    }

    @TypeConverter
    fun fromIntListToString(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromStringToIntList(string: String): List<Int> {
        return string.split(",").map { it.toInt() }
    }

}