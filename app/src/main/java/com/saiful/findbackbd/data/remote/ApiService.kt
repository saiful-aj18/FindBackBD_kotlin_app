package com.saiful.findbackbd.data.remote
import retrofit2.http.GET
import retrofit2.http.Query
interface ApiService{@GET("/v1/forecast") suspend fun forecast(@Query("latitude")lat:Double,@Query("longitude")lon:Double,@Query("current")current:String="temperature_2m"):Map<String,Any>}
