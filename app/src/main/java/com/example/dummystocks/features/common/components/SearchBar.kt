package com.example.dummystocks.features.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
  modifier: Modifier = Modifier,
) {
  Column(modifier = Modifier) {
    // search bar
    Row(
      modifier = modifier
        .fillMaxWidth()
        .background(
          MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
          shape = MaterialTheme.shapes.medium,
        )
        .padding(vertical = 8.dp, horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,

      ) {
      Icon(
        Icons.Default.Search,
        contentDescription = null,
      )
      Text(
        text = "Search",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.weight(1f),
      )

      Icon(
        Icons.Default.Mic,
        contentDescription = null,
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
    Row {
      Text(
        text = "My Symbols",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
      )
      Icon(
        Icons.Default.UnfoldMore, contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
      )
    }
  }
}