package com.example.vino.vinodao

import androidx.room.*
import com.example.vino.model.Block
import com.example.vino.model.BlockWithCoordinates
import com.example.vino.model.Todo
import com.example.vino.model.Vineyard
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {

    @Query("SELECT * FROM Block WHERE vineyardId = :vineyardId")
    suspend fun getBlocksForVineyardId(vineyardId: Int): List<BlockWithCoordinates>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: Block)

    @Update
    suspend fun update(block: Block)

    @Delete
    suspend fun delete(block: Block)

    @Query("DELETE FROM Block")
    suspend fun deleteAll()
}