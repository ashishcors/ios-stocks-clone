package com.example.dummystocks.features.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dummystocks.domain.model.Stock

@Composable
fun StockItemView(stock: Stock) {
  Row(
    modifier = Modifier.padding(
      vertical = 4.dp
    )
  ) {
    Column(
      modifier = Modifier
        .weight(.5f)
        .padding(vertical = 4.dp)
    ) {
      Text(
        text = stock.tickerSymbol,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
      )
      Text(
        text = stock.name,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
      )
    }

    Box(
      modifier = Modifier
        .weight(.2f)
    ) {
      // Chart goes here
    }
    Column(
      modifier = Modifier.weight(.3f),
      horizontalAlignment = Alignment.End,
    ) {
      Text(
        text = String.format("%.2f", stock.price),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
      )
      Text(
        text = String.format("%+.2f", stock.change),
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        modifier = Modifier
          .width(80.dp)
          .background(
            if (stock.isPositive) Color.Green else Color.Red,
            MaterialTheme.shapes.small,
          )
          .padding(vertical = 4.dp, horizontal = 8.dp)
      )
    }
  }
}