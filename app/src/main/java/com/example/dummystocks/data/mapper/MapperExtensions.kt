package com.example.dummystocks.data.mapper

import com.example.dummystocks.data.local.entity.StockEntity
import com.example.dummystocks.data.local.entity.StockWithWatchlistInfo
import com.example.dummystocks.data.remote.model.StockResponse
import com.example.dummystocks.domain.model.Stock

fun StockWithWatchlistInfo.toDomain(): Stock = Stock(
  id = stock.id,
  name = stock.name,
  price = stock.price,
  change = stock.change,
  chart = stock.chart,
  isInWatchlist = isInWatchlist,
)


fun StockResponse.toEntity(): StockEntity = StockEntity(
  id = id,
  name = name,
  price = price,
  change = change,
  chart = chart,
)