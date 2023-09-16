package com.example.dummystocks.data.remote.model

data class StockResponse(
  val id: String,
  val name: String,
  val price: Double,
  val change: Double,
  val chart: List<Double>,
)