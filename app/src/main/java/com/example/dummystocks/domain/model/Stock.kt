package com.example.dummystocks.domain.model

data class Stock(
  val id: String,
  val name: String,
  val price: Double,
  val change: Double,
  val chart: List<Double>,
  val isInWatchlist: Boolean = false,
) {
  val tickerSymbol = id
  val isPositive = change >= 0
}