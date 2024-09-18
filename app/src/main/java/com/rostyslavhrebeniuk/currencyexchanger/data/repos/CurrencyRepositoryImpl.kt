package com.rostyslavhrebeniuk.currencyexchanger.data.repos

import com.rostyslavhrebeniuk.currencyexchanger.data.api.ApiCurrency
import com.rostyslavhrebeniuk.currencyexchanger.domain.entity.Currencies
import com.rostyslavhrebeniuk.currencyexchanger.domain.CurrencyRepository
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
