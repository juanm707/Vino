package com.example.vino.vinodao

import androidx.room.*
import com.example.vino.model.Block
import com.example.vino.model.Coordinate

@Dao
interface CoordinateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coordinate: Coordinate)

    @Update
    suspend fun update(coordinate: Coordinate)

    @Delete
    suspend fun delete(coordinate: Coordinate)

    @Query("DELETE FROM Coordinate")
    suspend fun deleteAll()
}