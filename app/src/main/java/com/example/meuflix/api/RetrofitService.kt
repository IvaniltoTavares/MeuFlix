package com.example.meuflix.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService {
    companion object{

        const val KEY_YOUTUBE = "AIzaSyC5JTIv0Pi_SLSlumaDc6ogxUzgKKF8n5c"

        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "f5fd9c7f0b2df27175b903923267cd74"
        const val BASE_URL_IMAGE = "https://image.tmdb.org/t/p/"

        private val okkHttpClient = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor())
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okkHttpClient)
            .build()

        val filmeAPI :FilmeAPI =  retrofit.create(FilmeAPI::class.java)
    }
}