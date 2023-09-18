package com.example.dummystocks.features.home.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.dummystocks.domain.model.News
import com.example.dummystocks.utils.timeAgo

@Composable
fun NewsItemView(news: News) {
  val context = LocalContext.current
  Card {
    Column {
      Row(
        modifier = Modifier.padding(16.dp)
      ) {
        Column(
          modifier = Modifier
            .weight(0.6f)
        ) {
          AsyncImage(
            model = news.source.iconUrl,
            contentDescription = news.source.name,
            modifier = Modifier
              .height(14.dp)
              .padding(bottom = 2.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
          )
          Text(
            text = news.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
          )
        }
        Spacer(modifier = Modifier.width(16.dp))
        AsyncImage(
          model = news.imageUrl,
          contentDescription = "News Image",
          modifier = Modifier
            .weight(0.4f)
            .aspectRatio(1.0f)
            .clip(MaterialTheme.shapes.medium),
          contentScale = ContentScale.Crop,
        )
      }
      HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = "${news.publishedAt.timeAgo()} • ${news.author}",
          modifier = Modifier.padding(8.dp),
          style = MaterialTheme.typography.bodyMedium
        )
        IconButton(
          modifier = Modifier.height(36.dp),
          onClick = {
            Toast.makeText(context, "More options pressed. ", Toast.LENGTH_SHORT).show()
          }) {
          Icon(Icons.Default.MoreHoriz, contentDescription = "More Options")
        }
      }
    }
  }
}