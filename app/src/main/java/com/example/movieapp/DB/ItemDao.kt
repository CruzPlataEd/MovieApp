package com.example.movieapp.DB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: Item)
    @Update
    suspend fun updateItem(item:Item)
    @Delete
    suspend fun deleteItem(item: Item)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCartelera(item: Cartelera)
    @Update
    suspend fun updateCartelera(item:Cartelera)
    @Delete
    suspend fun deleteCartelera(item: Cartelera)

    @Query("SELECT * from peliculas ORDER BY page ASC")
    fun getItems(): Flow<List<Item>>

    @Query("SELECT * from cartelera ORDER BY page ASC")
    fun getCantidad(): Flow<List<Cartelera>>
}