package com.example.project_g08.network

import com.example.project_g08.model.MyResObj
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

//    @GET("?stateCode={stateCode}&api_key={apiey}")
//    suspend fun getPark(@Path("stateCode","apiKey") stateCode:String, apiKey:String): Response<MyResObj>

    @GET("api/v1/parks/")
    suspend fun getPark(
        @Query("stateCode") stateCode: String?,
        @Query("api_key") apiKey: String?
    ): Response<MyResObj>


}
