package com.example.vino.vinodao

import androidx.room.*
import com.example.vino.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {

    @Transaction
    @Query("SELECT * FROM Block WHERE vineyardId = :vineyardId")
    suspend fun getBlocksForVineyardId(vineyardId: Int): List<BlockWithCoordinates>
    
    @Query("SELECT name, blockId FROM Block WHERE vineyardId = :vineyardId")
    suspend fun getBlockInfoForLWPReading(vineyardId: Int): List<BlockNameIdTuple>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: Block)

    @Update
    suspend fun update(block: Block)

    @Delete
    suspend fun delete(block: Block)

    @Query("DELETE FROM Block")
    suspend fun deleteAll()
}