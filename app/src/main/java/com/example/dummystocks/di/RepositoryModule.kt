package com.example.dummystocks.di

import com.example.dummystocks.data.repository.StocksRepositoryImpl
import com.example.dummystocks.domain.repository.StocksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindStocksRepository(
    stocksRepository: StocksRepositoryImpl,
  ): StocksRepository
}