package com.example.dummystocks.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_entity")
data class NewsEntity(
  @PrimaryKey @ColumnInfo("id")
  val id: String,

  @ColumnInfo("title")
  val title: String,

  @ColumnInfo("url")
  val url: String,

  @ColumnInfo("image_url")
  val imageUrl: String,

  @ColumnInfo("published_at")
  val publishedAt: String,

  @Embedded(prefix = "source_")
  val source: PublisherEntity,

  @ColumnInfo("authors")
  val authors: List<String>,
)

data class PublisherEntity(
  @ColumnInfo("id")
  val id: String,

  @ColumnInfo("name")
  val name: String,

  @ColumnInfo("icon_url")
  val iconUrl: String,
)
