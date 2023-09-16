package com.example.dummystocks.domain.model

import java.time.LocalDateTime

data class News(
  val id: String,
  val title: String,
  val url: String,
  val imageUrl: String,
  val publishedAt: LocalDateTime,
  val source: Publisher,
  // This probably should be authors
  val author: String,
)

data class Publisher(
  val id: String,
  val name: String,
  val iconUrl: String,
)
