package com.example.vino.model

import androidx.room.*

@Entity(tableName = "Block")
class Block(
    @PrimaryKey val blockId: Int,
    val vineyardId: Int,
    val name: String,
    val variety: String,
    val acres: Float,
    val vines: Int,
    val rootstock: String,
    val clone: String,
    val yearPlanted: Int,
    val rowSpacing: Float,
    val vineSpacing: Float
)

@Entity(tableName = "Coordinate", primaryKeys = ["coordinateId", "blockParentId", "latitude", "longitude"])
class Coordinate(
    val coordinateId: Int,
    val blockParentId: Int,
    val latitude: Double,
    val longitude: Double
)

class BlockWithCoordinates(
    @Embedded val block: Block,
    @Relation(
        parentColumn = "blockId",
        entityColumn = "blockParentId"
    )
    val coordinates: List<Coordinate>
)
