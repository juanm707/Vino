package com.example.vino.vinodao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.vino.database.UserVineyardCrossRef

@Dao
interface UserVineyardCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userVineyardCrossRef: UserVineyardCrossRef)
}