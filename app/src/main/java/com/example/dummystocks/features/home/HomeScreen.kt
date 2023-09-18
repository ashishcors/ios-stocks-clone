package com.example.dummystocks.features.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dummystocks.features.common.components.SearchBar
import com.example.dummystocks.features.common.components.StockItemView
import com.example.dummystocks.features.home.components.NewsItemView
import com.example.dummystocks.ui.BottomSheetScaffold
import com.example.dummystocks.ui.SheetValue.PartiallyExpanded
import com.example.dummystocks.ui.SheetValue.PartiallyHidden
import com.example.dummystocks.ui.rememberBottomSheetScaffoldState
import com.example.dummystocks.ui.rememberStandardBottomSheetState
import com.example.dummystocks.utils.DATE_FORMAT_MMMM_DD
import com.example.dummystocks.utils.format
import java.time.LocalDate

@Composable
fun HomeScreen(
  viewModel: HomeViewModel = hiltViewModel(),
  onNavigateToSearch: () -> Unit,
) {
  val primarySheetState = rememberStandardBottomSheetState(
    initialValue = PartiallyExpanded,
    skipHiddenState = true,
  )

  val stocks by viewModel.myStocks.collectAsState(initial = emptyList())
  val news by viewModel.news.collectAsState(initial = emptyList())
  val configuration = LocalConfiguration.current

  var searchMode by remember { mutableStateOf(false) }

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
        SearchBar(modifier = Modifier
          .padding(vertical = 8.dp)
          .clickable {
            onNavigateToSearch()
          })
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