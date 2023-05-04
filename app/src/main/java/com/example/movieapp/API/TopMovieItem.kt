package com.example.movieapp.API

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopMovieItem(
    @Json(name="page")
    val page: Int,
    @Json(name = "results")
    val results: ArrayList<MovieItem>
)
