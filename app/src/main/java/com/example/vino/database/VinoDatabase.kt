package com.example.vino.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vino.network.Todo
import com.example.vino.vinodao.TodoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Todo::class], version = 1, exportSchema = false)
public abstract class VinoDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: VinoDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): VinoDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VinoDatabase::class.java,
                    "vino_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}