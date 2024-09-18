package com.rostyslavhrebeniuk.currencyexchanger.data

import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    fun getBalances(): Flow<Map<String, Double>>

    suspend fun updateBalances(balances: Map<String, Double>)

    fun getFreeConversions(): Flow<Int>

    suspend fun updateFreeConversions()
}
