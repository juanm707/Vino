package com.example.vino.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.vino.database.UserVineyardCrossRef
import com.example.vino.model.*
import com.example.vino.network.VineyardManagerUser
import com.example.vino.network.VinoApiService
import com.example.vino.network.VinoWeatherService
import com.example.vino.network.WeatherBasic
import com.example.vino.vinodao.*
import kotlinx.coroutines.flow.Flow

class VinoRepository(private val todoDao: TodoDao,
                     private val blockDao: BlockDao,
                     private val coordinateDao: CoordinateDao,
                     private val lwpReadingDao: LWPReadingDao,
                     private val userDao: UserDao,
                     private val vineyardDao: VineyardDao,
                     private val userVineyardCrossRefDao: UserVineyardCrossRefDao,
                     private val vinoWeatherService: VinoWeatherService,
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
    suspend fun insert(vineyard: Vineyard) {
        return vineyardDao.insert(vineyard)
    }

    @WorkerThread
    suspend fun insert(coordinate: Coordinate) {
        return coordinateDao.insert(coordinate)
    }

    @WorkerThread
    suspend fun insert(lwpReading: LWPReading) {
        return lwpReadingDao.insert(lwpReading)
    }

    @WorkerThread
    suspend fun insert(userVineyardCrossRef: UserVineyardCrossRef) {
        return userVineyardCrossRefDao.insert(userVineyardCrossRef)
    }

    @WorkerThread
    suspend fun insert(user: VineyardManagerUser) {
        return userDao.insert(user)
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
        return userDao.getUser()
    }

    suspend fun getVineyards(userId: Int): List<Vineyard> {
        return vineyardDao.getVineyardsForUserId(userId)
    }

    suspend fun getVineyard(vineyardId: Int): Vineyard {
        return vineyardDao.getVineyardForVineyardId(vineyardId)
    }

    suspend fun refreshVineyards(userId: Int) {
        vinoApiService.getVineyards().forEach { vineyard ->
            insert(vineyard)
            insert(UserVineyardCrossRef(userId, vineyard.vineyardId))
        }

    }

    suspend fun getNumberOfVineyardsSprayed(): Int {
        return vineyardDao.getNumberOfVineyardsSprayed()
    }

    suspend fun getVineyardsSprayed(): List<String> {
        return vineyardDao.getVineyardsSprayed()
    }

    // This method to make api request for user
    suspend fun refreshUser() {
        insert(vinoApiService.getUser())
    }

    suspend fun refreshTodos() {
        vinoApiService.getTodos().forEach { todo ->
            insert(todo)
        }
    }

    suspend fun refreshBlocks() { // TODO: would take vineyard id
        vinoApiService.getBlocks().forEach { block ->
            insert(Block(block.blockId, block.vineyardId, block.name, block.variety, block.acres,
                block.vines, block.rootstock, block.clone, block.yearPlanted, block.rowSpacing, block.vineSpacing))
            block.coordinates.forEach {  coordinate ->
                insert(Coordinate(coordinate.id, block.blockId, coordinate.latitude, coordinate.longitude))
            }
        }
    }

    suspend fun refreshLWPReadings() {
        vinoApiService.getLWPReadings().forEach { lwpReading ->
            insert(lwpReading)
        }
    }

    suspend fun getBlockInfoForLWPReading(vineyardId: Int): List<BlockNameIdTuple> {
        return blockDao.getBlockInfoForLWPReading(vineyardId)
    }

    suspend fun getBlocksForVineyardId(vineyardId: Int): List<BlockWithCoordinates> {
        return blockDao.getBlocksForVineyardId(vineyardId)
    }

    suspend fun getLWPReadingsForBlockId(blockId: Int): List<LWPReading> {
        return lwpReadingDao.getLWPReadingsForBlockId(blockId)
    }

    suspend fun getDailyWeather(latitude: Double, longitude: Double): WeatherBasic {
        return vinoWeatherService.getDailyWeather(latitude, longitude)
    }

    suspend fun getAdvancedWeather(latitude: Double, longitude: Double): WeatherBasic {
        return vinoWeatherService.getAdvanceWeather(latitude, longitude)
    }
}