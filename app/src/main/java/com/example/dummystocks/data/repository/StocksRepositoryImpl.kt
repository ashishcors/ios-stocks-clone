package com.example.dummystocks.data.repository

import com.example.dummystocks.data.local.dao.NewsDao
import com.example.dummystocks.data.local.dao.StockDao
import com.example.dummystocks.data.local.entity.StockWithFavorite
import com.example.dummystocks.data.mapper.toDomain
import com.example.dummystocks.data.mapper.toEntity
import com.example.dummystocks.data.remote.StocksApi
import com.example.dummystocks.data.remote.model.StockResponse
import com.example.dummystocks.domain.model.News
import com.example.dummystocks.domain.model.Stock
import com.example.dummystocks.domain.repository.StocksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class StocksRepositoryImpl @Inject constructor(
  private val stockDao: StockDao,
  private val newsDao: NewsDao,
  private val stocksApi: StocksApi,
) : StocksRepository {

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun getFavouriteStocks(): Flow<List<Stock>> {
    return callbackFlow {
      // Listen for changes to the favourite stocks
      val remoteSyncJob = launch {
        stockDao.getFavouriteStockIds()
          .flatMapLatest { list ->
            val ids = if (list.isNotEmpty()) {
              list.map { it.id }
            } else {
              stocksApi.getSuggestedStocks().map { it.id }
            }
            stocksApi.getStockUpdates(ids)
          }
          .collect { updatedStocks ->
            // Update the database with the new stock data
            stockDao.insertAll(updatedStocks.map(StockResponse::toEntity))
          }
      }

      // Listen for changes to the stock data
      val localDataJob = launch {
        stockDao.getFavoriteStocks()
          .collect {
            trySend(it.map(StockWithFavorite::toDomain))
          }
      }

      awaitClose {
        remoteSyncJob.cancel()
        localDataJob.cancel()
      }
    }
  }

  override fun getNews(): Flow<List<News>> {
    return stocksApi.getNews()
  }
}
