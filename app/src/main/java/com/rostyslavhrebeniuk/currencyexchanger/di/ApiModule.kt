
package com.rostyslavhrebeniuk.currencyexchanger.di

import com.rostyslavhrebeniuk.currencyexchanger.data.api.ApiCurrency
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    fun providesBaseUrl() : String = "https://developers.paysera.com/tasks/"

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl : String) : Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    @Provides
    @Singleton
    fun provideMainService(retrofit : Retrofit) : ApiCurrency = retrofit.create(ApiCurrency::class.java)
}
