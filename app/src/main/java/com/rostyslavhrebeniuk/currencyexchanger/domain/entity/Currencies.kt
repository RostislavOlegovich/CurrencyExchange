package com.rostyslavhrebeniuk.currencyexchanger.domain.entity

data class Currencies(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
