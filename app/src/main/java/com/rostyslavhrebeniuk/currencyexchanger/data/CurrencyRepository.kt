package com.rostyslavhrebeniuk.currencyexchanger.data

import com.rostyslavhrebeniuk.currencyexchanger.domain.Currencies

interface CurrencyRepository {

    suspend fun getCurrencies(): Currencies?
}
