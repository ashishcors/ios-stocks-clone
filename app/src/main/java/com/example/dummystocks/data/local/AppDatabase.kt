package com.example.dummystocks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dummystocks.data.local.converter.ListTypeConverters
import com.example.dummystocks.data.local.dao.NewsDao
import com.example.dummystocks.data.local.dao.StockDao
import com.example.dummystocks.data.local.entity.FavoriteStockEntity
import com.example.dummystocks.data.local.entity.NewsEntity
import com.example.dummystocks.data.local.entity.StockEntity

@Database(
  entities = [StockEntity::class, FavoriteStockEntity::class, NewsEntity::class],
  version = 1,
)
@TypeConverters(ListTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun stockDao(): StockDao

  abstract fun newsDao(): NewsDao
}