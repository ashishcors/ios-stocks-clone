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

@Entity(tableName = "watchlist_stock_entity")
data class WatchlistStockEntity(
  @PrimaryKey @ColumnInfo("id")
  val id: String,
)

data class StockWithWatchlistInfo(
  @Embedded
  val stock: StockEntity,

  @Relation(
    parentColumn = "id",
    entityColumn = "id",
    entity = WatchlistStockEntity::class,
  )
  val watchlistEntry: WatchlistStockEntity?,
) {
  @Ignore
  val isInWatchlist = watchlistEntry != null
}
