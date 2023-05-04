package com.example.movieapp.API

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface MovieAPI {
    @Headers("Accept: application/json")
    @GET
    suspend fun fetchMovie(@Url url: String): TopMovieItem
}