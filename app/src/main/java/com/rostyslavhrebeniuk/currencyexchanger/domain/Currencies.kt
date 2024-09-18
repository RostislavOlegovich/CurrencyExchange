package com.rostyslavhrebeniuk.currencyexchanger.domain

data class Currencies(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
