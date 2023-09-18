package com.example.dummystocks.features.home

import androidx.lifecycle.ViewModel
import com.example.dummystocks.domain.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val stocksRepository: StocksRepository,
) : ViewModel() {

  val myStocks = stocksRepository.getWatchlistStocks()

  val news = stocksRepository.getNews()
}