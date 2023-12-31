package com.example.dummystocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dummystocks.features.home.HomeScreen
import com.example.dummystocks.features.search.SearchScreen
import com.example.dummystocks.ui.theme.DummyStocksTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()
      DummyStocksTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          NavHost(navController = navController, startDestination = "home") {
            composable("home") {
              HomeScreen {
                navController.navigate("search")
              }
            }
            // Note: looking at iOS implementation, it might be using same screen for search and
            // home we can do that, or we can try using the SharedElement transition to get the same
            // effect in compose.
            composable("search") { SearchScreen() }
          }
        }
      }
    }
  }
}