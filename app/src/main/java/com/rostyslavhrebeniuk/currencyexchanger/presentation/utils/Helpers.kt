package com.rostyslavhrebeniuk.currencyexchanger.presentation.utils

import android.text.TextUtils
import kotlin.math.pow
import kotlin.math.round

fun Double.roundTo(decimals: Int = 3): Double {
    val multiplier = 10.0.pow(decimals.toDouble())
    return round(this * multiplier) / multiplier
}

fun CharSequence.isDoubleOrDigit(): Boolean {
    return this.toString().toDoubleOrNull() != null || TextUtils.isDigitsOnly(this)
}
