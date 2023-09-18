package com.example.dummystocks.domain.repository

import com.example.dummystocks.domain.model.News
import com.example.dummystocks.domain.model.SafeResult
import com.example.dummystocks.domain.model.Stock
import kotlinx.coroutines.flow.Flow

interface StocksRepository {

  fun getWatchlistStocks(): Flow<List<Stock>>

  fun getNews(): Flow<List<News>>

  suspend fun findStocks(query: String): SafeResult<List<Stock>>

  suspend fun updateStockWatchlistStatus(stock: Stock, isInWatchlist: Boolean): SafeResult<Unit>
}