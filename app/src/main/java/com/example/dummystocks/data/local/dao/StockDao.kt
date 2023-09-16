package com.example.dummystocks.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.dummystocks.data.local.entity.FavoriteStockEntity
import com.example.dummystocks.data.local.entity.StockEntity
import com.example.dummystocks.data.local.entity.StockWithFavorite
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
  @Transaction
  @Query("SELECT * FROM stock_entity")
  fun getFavoriteStocks(): Flow<List<StockWithFavorite>>

  @Query("SELECT * FROM favorite_stock_entity")
  fun getFavouriteStockIds(): Flow<List<FavoriteStockEntity>>

  // Insert or update the stock without changing the favourite status
  @Upsert
  suspend fun insertAll(stocks: List<StockEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(favorite: FavoriteStockEntity)

  @Query("DELETE FROM favorite_stock_entity WHERE id = :id")
  suspend fun deleteF(id: String)
}