package com.example.dummystocks.di

import android.app.Application
import androidx.room.Room
import com.example.dummystocks.data.local.AppDatabase
import com.example.dummystocks.data.remote.StocksApi
import com.example.dummystocks.data.remote.StocksApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideDatabase(
    application: Application,
  ): AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, "app_database")
      .fallbackToDestructiveMigration()
      .build()
  }

  @Provides
  @Singleton
  fun provideStockDao(
    database: AppDatabase,
  ) = database.stockDao()

  @Provides
  @Singleton
  fun provideStocksApi(): StocksApi {
    return StocksApiImpl()
  }
}