package com.example.dummystocks.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.dummystocks.data.local.entity.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

  @Query("SELECT * FROM news_entity ORDER BY published_at DESC")
  fun getNews(): Flow<List<NewsEntity>>

  @Upsert
  suspend fun insertAll(news: List<NewsEntity>)
}