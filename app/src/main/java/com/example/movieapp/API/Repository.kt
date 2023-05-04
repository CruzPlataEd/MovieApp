package com.example.movieapp.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Repository {
    val baseUrl = "https://api.themoviedb.org/3/movie/"
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}