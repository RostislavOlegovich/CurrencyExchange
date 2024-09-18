package com.rostyslavhrebeniuk.currencyexchanger.data.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

suspend fun DataStore<Preferences>.putString(key: String, value: String) {
    edit {
        it[stringPreferencesKey(name = key)] = value
    }
}

fun DataStore<Preferences>.getString(
    key: String,
    defaultValue: String = "",
): Flow<String> =
    data.map {
        it[stringPreferencesKey(name = key)] ?: defaultValue
    }

suspend fun DataStore<Preferences>.putInt(key: String, value: Int) {
    edit {
        it[intPreferencesKey(name = key)] = value
    }
}

fun DataStore<Preferences>.getInt(
    key: String,
    defaultValue: Int = 0,
): Flow<Int> =
    data.map {
        it[intPreferencesKey(name = key)] ?: defaultValue
    }

