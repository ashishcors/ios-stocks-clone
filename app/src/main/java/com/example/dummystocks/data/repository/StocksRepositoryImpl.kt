package com.example.dummystocks.data.repository

import com.example.dummystocks.data.local.dao.StockDao
import com.example.dummystocks.data.local.entity.StockWithWatchlistInfo
import com.example.dummystocks.data.local.entity.WatchlistStockEntity
import com.example.dummystocks.data.mapper.toDomain
import com.example.dummystocks.data.mapper.toEntity
import com.example.dummystocks.data.remote.StocksApi
import com.example.dummystocks.data.remote.model.StockResponse
import com.example.dummystocks.domain.model.News
import com.example.dummystocks.domain.model.SafeResult
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
  private val stocksApi: StocksApi,
) : StocksRepository {

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun getWatchlistStocks(): Flow<List<Stock>> {
    return callbackFlow {
      // Listen for changes to the watchlist stocks
      val remoteSyncJob = launch {
        stockDao.getWatchlistStockIdsFlow()
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
            stockDao.insertStocks(updatedStocks.map(StockResponse::toEntity))
          }
      }

      // Listen for changes to the stock data
      val localDataJob = launch {
        stockDao.getWatchlistStocksFlow()
          .collect {
            trySend(it.map(StockWithWatchlistInfo::toDomain))
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

  override suspend fun findStocks(query: String): SafeResult<List<Stock>> {
    return try {
      val currentWatchList = stockDao.getWatchlistStockIds()
      val result = stocksApi.findStocks(query)
        .map {
          Stock(
            id = it.id,
            name = it.name,
            price = it.price,
            change = it.change,
            chart = it.chart,
            isInWatchlist = currentWatchList.any { watchlistStock -> watchlistStock.id == it.id },
          )
        }
      SafeResult.Success(result)
    } catch (e: Exception) {
      SafeResult.Failure(e)
    }
  }

  override suspend fun updateStockWatchlistStatus(
    stock: Stock,
    isInWatchlist: Boolean
  ): SafeResult<Unit> {
    return try {
      if (isInWatchlist) {
        stockDao.insertWatchlistEntry(WatchlistStockEntity(stock.id))
      } else {
        stockDao.deleteWatchEntry(stock.id)
      }
      SafeResult.Success(Unit)
    } catch (e: Exception) {
      SafeResult.Failure(e)
    }
  }
}
