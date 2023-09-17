@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dummystocks

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.dummystocks.domain.model.News
import com.example.dummystocks.domain.model.Stock
import com.example.dummystocks.ui.BottomSheetScaffold
import com.example.dummystocks.ui.SheetValue.PartiallyExpanded
import com.example.dummystocks.ui.SheetValue.PartiallyHidden
import com.example.dummystocks.ui.rememberBottomSheetScaffoldState
import com.example.dummystocks.ui.rememberStandardBottomSheetState
import com.example.dummystocks.ui.theme.DummyStocksTheme
import com.example.dummystocks.utils.DATE_FORMAT_MMMM_DD
import com.example.dummystocks.utils.format
import com.example.dummystocks.utils.timeAgo
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      DummyStocksTheme {
        val viewModel = hiltViewModel<MainViewModel>()
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

          val primarySheetState = rememberStandardBottomSheetState(
            initialValue = PartiallyExpanded,
            skipHiddenState = true,
          )

          val stocks by viewModel.myStocks.collectAsState(initial = emptyList())
          val news by viewModel.news.collectAsState(initial = emptyList())
          val configuration = LocalConfiguration.current

          BottomSheetScaffold(
            scaffoldState = rememberBottomSheetScaffoldState(primarySheetState),
            sheetPartialExpandedHeight = 360.dp,
            sheetPartialHiddenHeight = 100.dp,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            topBar = {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(56.dp)
                  .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Column {
                  Text(
                    text = "Stocks",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                  )
                  Text(
                    text = LocalDate.now().format(DATE_FORMAT_MMMM_DD),
                    style = MaterialTheme.typography.titleLarge,
                  )
                }
                IconButton(
                  modifier = Modifier,
                  onClick = {
                  }) {
                  Icon(
                    Icons.Default.MoreHoriz,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                      .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                        shape = CircleShape,
                      )
                      .padding(4.dp),
                  )
                }
              }
            },
            sheetDragHandle = {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
              ) {
                Box(
                  modifier = Modifier
                    .height(4.dp)
                    .width(40.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.50f))
                )
              }
            },
            sheetContent = {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.Start,
              ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = if (primarySheetState.currentValue == PartiallyHidden && primarySheetState.targetValue == PartiallyHidden) {
                    "Business News"
                  } else {
                    "Top Stories"
                  },
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.Bold,
                )
                Text(
                  text = "From @ News",
                  modifier = Modifier.padding(bottom = 8.dp),
                )
              }
              val maxHeight = configuration.screenHeightDp - 158
              LazyColumn(
                modifier = Modifier
                  .heightIn(min = 0.dp, max = maxHeight.dp)
                  .fillMaxSize()
                  .padding(horizontal = 16.dp)
              ) {

                items(news.size) { index ->
                  Column {
                    NewsItemView(news = news[index])
                    Spacer(modifier = Modifier.padding(8.dp))
                  }
                }
              }
            },
            modifier = Modifier.fillMaxSize(),
          ) { scaffoldPadding ->

            val lazyListState = rememberLazyListState()

            LaunchedEffect(lazyListState.isScrollInProgress) {
              if (lazyListState.isScrollInProgress && primarySheetState.currentValue == PartiallyExpanded) {
                primarySheetState.partialHide()
              }
            }

            LazyColumn(
              modifier = Modifier
                .padding(scaffoldPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
              state = lazyListState,
            ) {
              item {
                Column(
                  modifier = Modifier
                    .padding(vertical = 8.dp)
                ) {
                  // search bar
                  Row(
                    modifier = Modifier
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

              items(stocks.size) { index ->
                Column {
                  StockItemView(stock = stocks[index])
                  HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
              }
            }

          }
        }
      }
    }
  }
}

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
        text = stock.id,
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
            if (stock.change > 0) Color.Green
            else Color.Red,
            MaterialTheme.shapes.small
          )
          .padding(vertical = 4.dp, horizontal = 8.dp)
      )
    }
  }
}

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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  DummyStocksTheme {
    Greeting("Android")
  }
}