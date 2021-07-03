package com.example.vino.repository

import androidx.annotation.WorkerThread
import com.example.vino.model.Block
import com.example.vino.model.BlockWithCoordinates
import com.example.vino.model.Coordinate
import com.example.vino.model.Todo
import com.example.vino.network.VineyardManagerUser
import com.example.vino.network.VinoApiService
import com.example.vino.vinodao.BlockDao
import com.example.vino.vinodao.CoordinateDao
import com.example.vino.vinodao.TodoDao
import kotlinx.coroutines.flow.Flow

class VinoRepository(private val todoDao: TodoDao,
                     private val blockDao: BlockDao,
                     private val coordinateDao: CoordinateDao,
                     private val vinoApiService: VinoApiService) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val completeTodos: Flow<List<Todo>> = todoDao.getCompleteTodos()
    val inCompleteTodos: Flow<List<Todo>> = todoDao.getIncompleteTodos()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(todo: Todo) {
        return todoDao.insert(todo)
    }

    @WorkerThread
    suspend fun insert(block: Block) {
        return blockDao.insert(block)
    }

    @WorkerThread
    suspend fun insert(coordinate: Coordinate) {
        return coordinateDao.insert(coordinate)
    }

    @WorkerThread
    suspend fun update(todo: Todo) {
        return todoDao.update(todo)
    }

    @WorkerThread
    suspend fun delete(todo: Todo) {
        return todoDao.delete(todo)
    }

    // This method to get from database
    suspend fun getUser(): VineyardManagerUser {
        return vinoApiService.getUser()
    }

    // This method to make api request for user
    suspend fun refreshUser(): VineyardManagerUser {
        return vinoApiService.getUser()
    }

    suspend fun refreshTodos() {
        vinoApiService.getTodos().forEach { todo ->
            insert(todo)
        }
    }

    suspend fun refreshBlocks() { // TODO: would take vineyard id
        vinoApiService.getBlocks().forEach { block ->
            insert(Block(block.id, block.vineyardId, block.name, block.variety, block.acres,
                block.vines, block.rootstock, block.clone, block.yearPlanted, block.rowSpacing, block.vineSpacing))
            block.coordinates.forEach {  coordinate ->
                insert(Coordinate(coordinate.id, block.id, coordinate.latitude, coordinate.longitude))
            }
        }
    }

    suspend fun getBlocksForVineyardId(vineyardId: Int): List<BlockWithCoordinates> {
        return blockDao.getBlocksForVineyardId(vineyardId)
    }
}