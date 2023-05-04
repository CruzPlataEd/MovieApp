package com.example.movieapp

import android.app.Application
import com.example.movieapp.DB.ItemRoomDatabase

class InventoryApplication: Application() {
    val database by lazy{ ItemRoomDatabase.getDatabase(this) }
}