package com.rostyslavhrebeniuk.currencyexchanger.domain

import com.rostyslavhrebeniuk.currencyexchanger.domain.entity.Currencies

interface CurrencyRepository {

    suspend fun getCurrencies(): Currencies?
}
