package com.example.dummystocks.features.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.dummystocks.domain.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private  val stocksRepository: StocksRepository,
): ViewModel() {
}