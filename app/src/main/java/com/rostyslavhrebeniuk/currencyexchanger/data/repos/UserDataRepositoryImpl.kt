package com.rostyslavhrebeniuk.currencyexchanger.data.repos

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rostyslavhrebeniuk.currencyexchanger.di.UserDataStore
import com.rostyslavhrebeniuk.currencyexchanger.domain.UserDataRepository
import com.rostyslavhrebeniuk.currencyexchanger.data.utils.getInt
import com.rostyslavhrebeniuk.currencyexchanger.data.utils.getString
import com.rostyslavhrebeniuk.currencyexchanger.data.utils.putInt
import com.rostyslavhrebeniuk.currencyexchanger.data.utils.putString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    @UserDataStore private val dataStore: DataStore<Preferences>
) : UserDataRepository {

    private val gson = Gson()

    companion object {
        const val USER_DATA_STORE = "USER_DATA_STORE"

        private const val BALANCE_DATA_STORE = "BALANCE_DATA_STORE"
        private const val FREE_CONVERSIONS_DATA_STORE = "FREE_CONVERSIONS_DATA_STORE"
        private const val INITIAL_FREE_CONVERSIONS = 5
        private const val ZERO_CONVERSIONS = 0
    }

    override fun getBalances(): Flow<Map<String, Double>> {
        return dataStore.getString(BALANCE_DATA_STORE).map { balancesString ->
            if (balancesString.isEmpty()) {
                emptyMap()
            } else {
                gson.fromJson(balancesString, object : TypeToken<Map<String, Double>>() {}.type)
            }
        }
    }


    override suspend fun updateBalances(balances: Map<String, Double>) {
        dataStore.putString(BALANCE_DATA_STORE, gson.toJson(balances))
    }

    override fun getFreeConversions(): Flow<Int> {
        return dataStore.getInt(FREE_CONVERSIONS_DATA_STORE, INITIAL_FREE_CONVERSIONS)
    }

    override suspend fun updateFreeConversions() {
        val freeConversions = getFreeConversions().first()
        if (freeConversions != ZERO_CONVERSIONS) {
            dataStore.putInt(FREE_CONVERSIONS_DATA_STORE, freeConversions - 1)
        }
    }
}