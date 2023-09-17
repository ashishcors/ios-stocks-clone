package com.example.dummystocks.data.remote

import com.example.dummystocks.data.remote.model.StockResponse
import com.example.dummystocks.domain.model.News
import com.example.dummystocks.domain.model.Publisher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

interface StocksApi {

  suspend fun getSuggestedStocks(): List<StockResponse>

  fun getStockUpdates(ids: List<String>): Flow<List<StockResponse>>

  suspend fun findStocks(query: String): List<StockResponse>

  fun getNews(): Flow<List<News>>
}

class StocksApiImpl : StocksApi {

  private val dummyStocks = listOf(
    StockResponse(
      "Dow Jones",
      "Dow Jones Industrial Average",
      35456.42,
      16.43,
      listOf(35250.1, 35400.2, 35555.3, 35330.4, 35660.5, 35420.6, 35500.7, 35390.8, 35610.9, 35520.0),
    ),
    StockResponse(
      "S&P 500",
      "Standard & Poor's 500",
      4561.00,
      0.43,
      listOf(4540.0, 4572.1, 4555.5, 4580.0, 4566.7, 4534.8, 4590.2, 4550.0, 4575.3, 4542.9),
    ),
    StockResponse(
      "AAPL",
      "Apple Inc.",
      155.6,
      1.2,
      listOf(152.0, 153.5, 154.0, 155.0, 156.0, 155.5, 155.3, 154.9, 155.4, 155.6)
    ),
    StockResponse(
      "GOOGL",
      "Alphabet Inc.",
      2845.3,
      -2.1,
      listOf(2860.0, 2855.0, 2853.0, 2847.0, 2846.0, 2845.0, 2844.0, 2843.0, 2846.0, 2845.3)
    ),
    StockResponse(
      "TSLA",
      "Tesla Inc.",
      812.0,
      0.5,
      listOf(810.0, 811.0, 812.0, 813.0, 814.0, 813.0, 812.0, 811.0, 810.0, 812.0)
    ),
    StockResponse(
      "MSFT",
      "Microsoft Corporation",
      305.0,
      -0.2,
      listOf(306.0, 305.0, 304.0, 303.0, 302.0, 301.0, 300.0, 299.0, 298.0, 297.0)
    ),
    StockResponse(
      "FB",
      "Facebook Inc.",
      356.0,
      0.1,
      listOf(355.0, 356.0, 357.0, 358.0, 359.0, 358.0, 357.0, 356.0, 355.0, 356.0)
    ),
    StockResponse(
      "AMZN",
      "Amazon.com Inc.",
      3567.0,
      0.3,
      listOf(3566.0, 3567.0, 3568.0, 3569.0, 3570.0, 3569.0, 3568.0, 3567.0, 3566.0, 3567.0)
    ),
    StockResponse(
      "NFLX",
      "Netflix Inc.",
      600.0,
      0.0,
      listOf(599.0, 600.0, 601.0, 602.0, 603.0, 602.0, 601.0, 600.0, 599.0, 600.0)
    ),
    StockResponse(
      "DIS",
      "The Walt Disney Company",
      176.0,
      0.0,
      listOf(175.0, 176.0, 177.0, 178.0, 179.0, 178.0, 177.0, 176.0, 175.0, 176.0)
    ),
    StockResponse(
      "PYPL",
      "PayPal Holdings Inc.",
      270.0,
      0.0,
      listOf(269.0, 270.0, 271.0, 272.0, 273.0, 272.0, 271.0, 270.0, 269.0, 270.0)
    ),
    StockResponse(
      "SPOT",
      "Spotify Technology S.A.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "UBER",
      "Uber Technologies Inc.",
      45.0,
      0.0,
      listOf(44.0, 45.0, 46.0, 47.0, 48.0, 47.0, 46.0, 45.0, 44.0, 45.0)
    ),
    StockResponse(
      "LYFT",
      "Lyft Inc.",
      50.0,
      0.0,
      listOf(49.0, 50.0, 51.0, 52.0, 53.0, 52.0, 51.0, 50.0, 49.0, 50.0)
    ),
    StockResponse(
      "SQ",
      "Square Inc.",
      250.0,
      0.0,
      listOf(249.0, 250.0, 251.0, 252.0, 253.0, 252.0, 251.0, 250.0, 249.0, 250.0)
    ),
    StockResponse(
      "PINS",
      "Pinterest Inc.",
      60.0,
      0.0,
      listOf(59.0, 60.0, 61.0, 62.0, 63.0, 62.0, 61.0, 60.0, 59.0, 60.0)
    ),
    StockResponse(
      "SNAP",
      "Snap Inc.",
      70.0,
      0.0,
      listOf(69.0, 70.0, 71.0, 72.0, 73.0, 72.0, 71.0, 70.0, 69.0, 70.0)
    ),
    StockResponse(
      "TWTR",
      "Twitter Inc.",
      60.0,
      0.0,
      listOf(59.0, 60.0, 61.0, 62.0, 63.0, 62.0, 61.0, 60.0, 59.0, 60.0)
    ),
    StockResponse(
      "SNOW",
      "Snowflake Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "ZM",
      "Zoom Video Communications Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "DOCU",
      "DocuSign Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "ROKU",
      "Roku Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "Z",
      "Zillow Group Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "SHOP",
      "Shopify Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "CRWD",
      "CrowdStrike Holdings Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "NET",
      "Cloudflare Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "OKTA",
      "Okta Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "DDOG",
      "Datadog Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "FSLY",
      "Fastly Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "TWLO",
      "Twilio Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    ),
    StockResponse(
      "ETSY",
      "Etsy Inc.",
      300.0,
      0.0,
      listOf(299.0, 300.0, 301.0, 302.0, 303.0, 302.0, 301.0, 300.0, 299.0, 300.0)
    )
  )

  private val theTelegraph = Publisher(
    id = "the-telegraph",
    name = "The Telegraph",
    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/48/The_Telegraph_logo.svg/320px-The_Telegraph_logo.svg.png"
  )

  private val newYorkTimes = Publisher(
    id = "new-york-times",
    name = "New York Times",
    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/58/NewYorkTimes.svg/320px-NewYorkTimes.svg.png"
  )

  private val bbcNews = Publisher(
    id = "bbc-news",
    name = "BBC News",
    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7d/BBC_America.svg/320px-BBC_America.svg.png"
  )

  private val dummyNews = listOf(
    News(
      id = "1",
      title = "Apple Announces New iPhone",
      url = "https://apple.com/news/iphone-13",
      imageUrl = "https://images.unsplash.com/photo-1606041011872-596597976b25?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YXBwbGUlMjBpcGhvbmV8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
      publishedAt = LocalDateTime.now(),
      source = theTelegraph,
      author = "Robert William",
    ),
    News(
      id = "2",
      title = "Alphabet's Revenue Surpasses Expectations",
      url = "https://alphabet.com/news/revenue-q3",
      imageUrl = "https://images.unsplash.com/reserve/uZYSV4nuQeyq64azfVIn_15130980706_64134efc6e_o.jpg?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YWxwaGFiZXR8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
      publishedAt = LocalDateTime.now().minusHours(1),
      source = newYorkTimes,
      author = "John Doe",
    ),
    News(
      id = "3",
      title = "Tesla's New Model 3",
      url = "https://tesla.com/news/model-3",
      imageUrl = "https://images.unsplash.com/photo-1562178235-7ba56b202338?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8bW9kZWwlMjAzfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
      publishedAt = LocalDateTime.now().minusHours(2),
      source = bbcNews,
      author = "Robert William",
    ),
    News(
      id = "4",
      title = "Microsoft's New Surface Laptop",

      url = "https://microsoft.com/news/surface-laptop",
      imageUrl = "https://images.unsplash.com/photo-1621570071349-aa0859110f9b?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8c3VyZmFjZSUyMGxhcHRvcHxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
      publishedAt = LocalDateTime.now().minusDays(1),
      source = theTelegraph,
      author = "John Doe",
    ),
    News(
      id = "5",
      title = "Facebook's New Oculus Quest",
      url = "https://facebook.com/news/oculus-quest",
      imageUrl = "https://images.unsplash.com/photo-1622979135225-d2ba269cf1ac?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8T2N1bHVzfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
      publishedAt = LocalDateTime.now().minusDays(4),
      source = newYorkTimes,
      author = "Robert William",
    ),
    News(
      id = "6",
      title = "Amazon's New Echo",
      url = "https://amazon.com/news/echo",
      imageUrl = "https://images.unsplash.com/photo-1543512214-318c7553f230?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NXx8ZWNobyUyMGRvdHxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
      publishedAt = LocalDateTime.now().minusDays(5),
      source = bbcNews,
      author = "John Doe",
    ),
    News(
      id = "7",
      title = "Netflix's New Show",

      url = "https://netflix.com/news/show",
      imageUrl = "https://images.unsplash.com/photo-1522869635100-9f4c5e86aa37?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8bmV0ZmxpeHxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
      publishedAt = LocalDateTime.now().minusDays(6),
      source = theTelegraph,
      author = "Robert William",
    ),
    News(
      id = "8",
      title = "Disney's New Movie",
      url = "https://disney.com/news/movie",
      imageUrl = "https://images.unsplash.com/photo-1528041119984-da3a9f8d04d1?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8ZGlzbmV5fGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
      publishedAt = LocalDateTime.now().minusDays(7),
      source = newYorkTimes,
      author = "John Doe",
    ),
    News(
      id = "9",
      title = "PayPal's New Payment System",
      url = "https://paypal.com/news/payment-system",
      imageUrl = "https://images.unsplash.com/photo-1648091854674-59abf26bbf39?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8cGF5cGFsfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
      publishedAt = LocalDateTime.now().minusDays(8),
      source = bbcNews,
      author = "Robert William",
    ),
    News(
      id = "10",
      title = "Spotify's New Music",
      url = "https://spotify.com/news/music",
      imageUrl = "https://images.unsplash.com/photo-1527150122806-f682d2fd8b09?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8c3BvdGlmeXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
      publishedAt = LocalDateTime.now().minusDays(9),
      source = theTelegraph,
      author = "John Doe",
    ),
  )

  override suspend fun getSuggestedStocks(): List<StockResponse> {
    return dummyStocks.take(8)
  }

  override fun getStockUpdates(ids: List<String>): Flow<List<StockResponse>> = flow {
    emit(dummyStocks.filter { it.id in ids })
    while (true) {
      delay(1000)
      emit(dummyStocks.filter { it.id in ids }
        .map {
          val change = it.change + (-10..10).random()
          it.copy(
            price = it.price + change,
            change = change,
            chart = it.chart.drop(1) + (it.price + change)
          )
        })
    }
  }

  override suspend fun findStocks(query: String): List<StockResponse> {
    return dummyStocks.filter {
      it.name.contains(query, ignoreCase = true)
          || it.id.contains(query, ignoreCase = true)
    }
  }

  override fun getNews(): Flow<List<News>> = flow {
    emit(dummyNews)
  }

}