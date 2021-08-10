package com.example.vino.vinodao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.vino.model.Vineyard
import com.example.vino.network.VineyardManagerUser

@Dao
interface VineyardDao {

    @Query("SELECT * FROM Vineyard v INNER JOIN UserVineyardCrossRef r ON v.vineyardId = r.vineyardId INNER JOIN User u ON r.userId = u.userId WHERE u.userId = :userId")
    suspend fun getVineyardsForUserId(userId: Int): List<Vineyard>

    @Query("SELECT * FROM Vineyard WHERE vineyardId = :vineyardId")
    suspend fun getVineyardForVineyardId(vineyardId: Int): Vineyard

    @Query("SELECT COUNT(*) FROM VINEYARD WHERE sprayed = 1")
    suspend fun getNumberOfVineyardsSprayed(): Int

    @Query("SELECT * FROM VINEYARD WHERE sprayed = 1")
    suspend fun getVineyardsSprayed(): List<Vineyard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vineyard: Vineyard)

    @Update
    suspend fun update(vineyard: Vineyard)

    @Delete
    suspend fun delete(vineyard: Vineyard)

    @Query("DELETE FROM Vineyard")
    suspend fun deleteAll()
}