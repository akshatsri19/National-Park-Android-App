package com.example.project_g08.model

import java.io.Serializable

data class Address(
    val postalCode:String,
    val city:String,
    val stateCode:String,
    val line1:String,
    val line2:String,
    val line3:String,
    val type:String ): Serializable
