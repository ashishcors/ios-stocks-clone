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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.PartiallyExpanded
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.dummystocks.domain.model.News
import com.example.dummystocks.ui.theme.DummyStocksTheme
import com.example.dummystocks.utils.DATE_FORMAT_MMMM_DD
import com.example.dummystocks.utils.format
import com.example.dummystocks.utils.timeAgo
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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

          var expanded by remember { mutableStateOf(false) }
          val primarySheetState = rememberStandardBottomSheetState(
            initialValue = Expanded,
            skipHiddenState = true,
          )

          LaunchedEffect(key1 = primarySheetState.currentValue,  primarySheetState.targetValue){
            if (primarySheetState.currentValue == PartiallyExpanded && primarySheetState.targetValue != Expanded) {
              expanded = true
            } else if (primarySheetState.currentValue == Expanded && primarySheetState.targetValue == PartiallyExpanded) {
              expanded = false
              primarySheetState.expand()
            }
          }

          val coroutineScope = rememberCoroutineScope()
          val stocks by viewModel.myStocks.collectAsState(initial = emptyList())
          val news by viewModel.news.collectAsState(initial = emptyList())

          BottomSheetScaffold(
            scaffoldState = rememberBottomSheetScaffoldState(primarySheetState),
            sheetPeekHeight = 100.dp,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            topBar = {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Column(
                  modifier = Modifier
                    .padding(start = 16.dp),
                ) {
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
                  modifier = Modifier.height(36.dp),
                  onClick = {
                    Toast.makeText(
                      this@MainActivity,
                      "Search pressed. ",
                      Toast.LENGTH_SHORT
                    ).show()
                  }) {
                  Icon(Icons.Default.MoreHoriz, contentDescription = "Search")
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
                  text = if (primarySheetState.currentValue == PartiallyExpanded && primarySheetState.targetValue != Expanded) {
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
              LazyColumn(
                modifier = Modifier
                  .heightIn(min = 0.dp, max = if (expanded) 660.dp else 360.dp)
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
              Timber.d("isScrolling: ${lazyListState.isScrollInProgress}, primarySheetState.currentValue: ${primarySheetState.currentValue}, expanded: $expanded")
              if (lazyListState.isScrollInProgress && primarySheetState.currentValue == Expanded && !expanded) {
                expanded = true
                primarySheetState.partialExpand()
              }
            }

            LazyColumn(
              modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize(),
              state = lazyListState,
            ) {
              items(stocks.size) { index ->
                Box(modifier = Modifier.height(300.dp)) {
                  Greeting(name = "${stocks[index].name}, ${stocks[index].price}")
                }
              }
            }

          }
        }
      }
    }
  }

  @Composable
  private fun DragHandle() {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Top Stories",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
      )
      Text(
        text = "From @ News",
        modifier = Modifier.padding(bottom = 8.dp),
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