package com.example.dummystocks.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.dummystocks.data.local.entity.StockEntity
import com.example.dummystocks.data.local.entity.StockWithWatchlistInfo
import com.example.dummystocks.data.local.entity.WatchlistStockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
  @Transaction
  @Query("SELECT * FROM stock_entity")
  fun getWatchlistStocksFlow(): Flow<List<StockWithWatchlistInfo>>

  @Query("SELECT * FROM watchlist_stock_entity")
  fun getWatchlistStockIdsFlow(): Flow<List<WatchlistStockEntity>>

  @Query("SELECT * FROM watchlist_stock_entity")
  suspend fun getWatchlistStockIds(): List<WatchlistStockEntity>

  @Upsert
  suspend fun insertStocks(stocks: List<StockEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWatchlistEntry(item: WatchlistStockEntity)

  @Query("DELETE FROM watchlist_stock_entity WHERE id = :id")
  suspend fun deleteWatchEntry(id: String)
}