package com.example.dummystocks.features.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
  Scaffold {
    Text(
      text = "Search Screen",
      modifier = Modifier
        .padding(it)
        .fillMaxSize(),
      textAlign = TextAlign.Center,
    )
  }
}