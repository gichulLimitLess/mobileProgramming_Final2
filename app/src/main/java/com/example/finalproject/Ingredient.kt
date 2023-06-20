package com.example.finalproject

import java.io.Serializable

data class Ingredient(
    var id:Int, var Iname:String="", var Quantity:Int, var BuyYear:Int, var BuyMonth:Int,
    var BuyDay:Int, var EndYear:Int, var EndMonth:Int, var EndDay:Int, var Unit:String
)

