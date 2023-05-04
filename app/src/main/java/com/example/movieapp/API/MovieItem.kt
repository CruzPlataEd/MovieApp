package com.example.movieapp.API

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieItem(
    @Json(name = "poster_path")
    val poster_path: String,
    @Json(name = "overview")
    val overview: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "backdrop_path")
    val backdrop_path: String,
    @Json(name = "vote_average")
    val vote_average: Number
)
