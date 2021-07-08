package com.example.vino.vinodao

import androidx.room.*
import com.example.vino.model.LWPReading

@Dao
interface LWPReadingDao {

    @Transaction
    @Query("SELECT * FROM LWPReading WHERE blockId = :blockId")
    suspend fun getLWPReadingsForBlockId(blockId: Int): List<LWPReading>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lwpReading: LWPReading)

    @Update
    suspend fun update(lwpReading: LWPReading)

    @Delete
    suspend fun delete(lwpReading: LWPReading)

    @Query("DELETE FROM LWPReading")
    suspend fun deleteAll()

}