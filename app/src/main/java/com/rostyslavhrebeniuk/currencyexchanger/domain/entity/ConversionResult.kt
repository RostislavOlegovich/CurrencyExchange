package com.rostyslavhrebeniuk.currencyexchanger.domain.entity

data class ConversionResult(
    val sellValue: String,
    val receiveValue: String,
    val commissionValue: String
)
