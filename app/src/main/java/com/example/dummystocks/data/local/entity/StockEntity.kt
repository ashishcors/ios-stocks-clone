package com.example.dummystocks.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "stock_entity")
data class StockEntity(
  @PrimaryKey @ColumnInfo("id")
  val id: String,

  @ColumnInfo("name")
  val name: String,

  @ColumnInfo("price")
  val price: Double,

  @ColumnInfo("change")
  val change: Double,

  @ColumnInfo("chart")
  val chart: List<Double>,
)

@Entity(tableName = "favorite_stock_entity")
data class FavoriteStockEntity(
  @PrimaryKey @ColumnInfo("id")
  val id: String,
)

data class StockWithFavorite(
  @Embedded
  val stock: StockEntity,

  @Relation(
    parentColumn = "id",
    entityColumn = "id",
    entity = FavoriteStockEntity::class,
  )
  val favourite: FavoriteStockEntity?,
) {
  @Ignore
  val isFavourite = favourite != null
}
