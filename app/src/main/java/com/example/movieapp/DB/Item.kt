package com.example.movieapp.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "peliculas")
data class Item(
    @ColumnInfo(name = "page")
    val page: Int,
    @ColumnInfo(name = "poster_path")
    val poster_path: String,
    @ColumnInfo(name = "overview")
    val overview: String,
    @PrimaryKey
    val title: String,
    @ColumnInfo(name = "backdrop_path")
    val backdrop_path: String,
    @ColumnInfo(name = "vote_average")
    val vote_average: Float
)

@Entity(tableName = "cartelera")
data class Cartelera(
    @ColumnInfo(name = "poster_path")
    val poster_path: String,
    @ColumnInfo(name = "overview")
    val overview: String,
    @PrimaryKey
    val title: String,
    @ColumnInfo(name = "backdrop_path")
    val backdrop_path: String,
    @ColumnInfo(name = "vote_average")
    val vote_average: Float
)
