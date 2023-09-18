package com.example.dummystocks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dummystocks.data.local.converter.ListTypeConverters
import com.example.dummystocks.data.local.dao.StockDao
import com.example.dummystocks.data.local.entity.WatchlistStockEntity
import com.example.dummystocks.data.local.entity.StockEntity

@Database(
  entities = [StockEntity::class, WatchlistStockEntity::class],
  version = 2,
)
@TypeConverters(ListTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun stockDao(): StockDao
}