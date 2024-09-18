
package com.rostyslavhrebeniuk.currencyexchanger.di

import com.rostyslavhrebeniuk.currencyexchanger.domain.CurrencyRepository
import com.rostyslavhrebeniuk.currencyexchanger.data.repos.CurrencyRepositoryImpl
import com.rostyslavhrebeniuk.currencyexchanger.domain.UserDataRepository
import com.rostyslavhrebeniuk.currencyexchanger.data.repos.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    @CurrencyRepositoryAnnotation
    fun provideCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository

    @Binds
    @Singleton
    @UserDataRepositoryAnnotation
    fun provideUserDataRepository(impl: UserDataRepositoryImpl): UserDataRepository
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CurrencyRepositoryAnnotation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserDataRepositoryAnnotation
