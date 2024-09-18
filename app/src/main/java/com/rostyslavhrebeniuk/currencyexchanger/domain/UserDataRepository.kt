package com.rostyslavhrebeniuk.currencyexchanger.domain

import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    fun getBalances(): Flow<Map<String, Double>>

    suspend fun updateBalances(balances: Map<String, Double>)

    fun getFreeConversions(): Flow<Int>

    suspend fun updateFreeConversions()
}
