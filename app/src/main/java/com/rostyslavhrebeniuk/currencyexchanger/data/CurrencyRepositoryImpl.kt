package com.rostyslavhrebeniuk.currencyexchanger.data

import com.rostyslavhrebeniuk.currencyexchanger.api.ApiCurrency
import com.rostyslavhrebeniuk.currencyexchanger.domain.Currencies
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val apiCurrency: ApiCurrency
) : CurrencyRepository {

    override suspend fun getCurrencies(): Currencies? {
        val response = apiCurrency.getCurrencies()
        return if (response.isSuccessful && response.body() != null) {
            response.body()
        } else {
            null
        }
    }
}
