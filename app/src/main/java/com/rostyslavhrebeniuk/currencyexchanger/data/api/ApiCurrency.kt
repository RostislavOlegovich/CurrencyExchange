package com.rostyslavhrebeniuk.currencyexchanger.data.api

import com.rostyslavhrebeniuk.currencyexchanger.domain.entity.Currencies
import retrofit2.Response
import retrofit2.http.GET

interface ApiCurrency {

    @GET("api/currency-exchange-rates")
    suspend fun getCurrencies() : Response<Currencies>
}