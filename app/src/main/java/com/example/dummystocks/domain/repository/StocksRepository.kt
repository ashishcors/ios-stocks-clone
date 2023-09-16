package com.example.dummystocks.domain.repository

import com.example.dummystocks.domain.model.News
import com.example.dummystocks.domain.model.SafeResult
import com.example.dummystocks.domain.model.Stock
import kotlinx.coroutines.flow.Flow

interface StocksRepository {

  fun getFavouriteStocks(): Flow<List<Stock>>

  fun getNews(): Flow<List<News>>
}