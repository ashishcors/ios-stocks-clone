package com.example.dummystocks

import androidx.lifecycle.ViewModel
import com.example.dummystocks.domain.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val stocksRepository: StocksRepository,
) : ViewModel() {

  val myStocks = stocksRepository.getFavouriteStocks()

  val news = stocksRepository.getNews()

}