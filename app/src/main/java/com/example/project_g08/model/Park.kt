package com.example.project_g08.model

import java.io.Serializable

data class Park(
    val fullName:String,
    val description:String,
    val url:String,
    val latitude:Double,
    val longitude:Double,
    val addresses:List<Address>,
    val images:List<Images> ) : Serializable
