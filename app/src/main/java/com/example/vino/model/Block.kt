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

@Entity(tableName = "Coordinate", primaryKeys = ["blockParentId", "latitude", "longitude"])
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

@Entity(tableName = "LWPReading", primaryKeys = ["lwpReadingId", "blockId"])
class LWPReading(
    val lwpReadingId: Int,
    val blockId: Int,
    val timestamp: Long,
    val barPressureData: Float
)

class BlockNameIdTuple {
    @ColumnInfo(name = "blockId")
    var blockId: Int = 0

    @ColumnInfo(name = "name")
    var blockName: String = ""
}
