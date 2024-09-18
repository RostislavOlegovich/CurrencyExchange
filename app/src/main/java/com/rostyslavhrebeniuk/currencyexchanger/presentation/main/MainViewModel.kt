package com.rostyslavhrebeniuk.currencyexchanger.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rostyslavhrebeniuk.currencyexchanger.domain.CurrencyRepository
import com.rostyslavhrebeniuk.currencyexchanger.domain.UserDataRepository
import com.rostyslavhrebeniuk.currencyexchanger.di.CurrencyRepositoryAnnotation
import com.rostyslavhrebeniuk.currencyexchanger.di.DefaultDispatcher
import com.rostyslavhrebeniuk.currencyexchanger.di.UserDataRepositoryAnnotation
import com.rostyslavhrebeniuk.currencyexchanger.domain.entity.ConversionResult
import com.rostyslavhrebeniuk.currencyexchanger.domain.entity.Currencies
import com.rostyslavhrebeniuk.currencyexchanger.presentation.utils.roundTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @CurrencyRepositoryAnnotation private val currencyRepository: CurrencyRepository,
    @UserDataRepositoryAnnotation private val userDataRepository: UserDataRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    companion object {
        private const val INITIAL_BALANCE = 1000.0
        private const val FEE = 0.007

        private const val EMPTY_BALANCE = 0.0
        private const val FREE_FEE = 0.0
        private const val UPDATE_DELAY: Long = 5000L
    }

    val freeConversions: StateFlow<Int> = userDataRepository.getFreeConversions()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _exchangeResult = MutableStateFlow<Double?>(null)
    val exchangeResult: StateFlow<Double?> = _exchangeResult.asStateFlow()

    val submitEnabled: StateFlow<Boolean> = combine(_error, _exchangeResult, ::Pair)
        .map { (error, exchangeResult) ->
            error.isNullOrEmpty() && exchangeResult != null
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _sellCurrency = MutableStateFlow<String?>(null)
    private val _receiveCurrency = MutableStateFlow<String?>(null)
    private val _amount = MutableStateFlow(EMPTY_BALANCE)

    private val sellCurrencyBalance = MutableStateFlow("" to EMPTY_BALANCE)
    private val receiveCurrencyBalance = MutableStateFlow("" to EMPTY_BALANCE)

    private val _currencies = MutableStateFlow<Currencies?>(null)

    val currencyRates: StateFlow<Map<String, Double>> =
        combine(_currencies.filterNotNull(), _sellCurrency, ::Pair)
            .map { (currencies, sellCurrency) ->
                currencies.rates.toMutableMap().apply {
                    remove(sellCurrency)
                }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val userBalance: StateFlow<Map<String, Double>> = userDataRepository.getBalances()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val sellCurrencies: StateFlow<Set<String>> = _currencies.filterNotNull().map { setOf(it.base) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    private val _conversionResult = MutableStateFlow<ConversionResult?>(null)
    val conversionResult: StateFlow<ConversionResult?> = _conversionResult.asStateFlow()

    private val commission = MutableStateFlow("")

    init {
        viewModelScope.launch(defaultDispatcher) {
            while (true) {
                _currencies.value = currencyRepository.getCurrencies()
                delay(UPDATE_DELAY)
            }
        }

        viewModelScope.launch(defaultDispatcher) {
            combine(
                userBalance,
                _currencies.filterNotNull(),
                ::Pair
            ).collect { (userBalance, currencies) ->
                if (userBalance.isEmpty()) {
                    userDataRepository.updateBalances(mapOf(currencies.base to INITIAL_BALANCE))
                }
            }
        }

        viewModelScope.launch(defaultDispatcher) {
            sellCurrencies.collect {
                _sellCurrency.value = it.firstOrNull()
            }
        }

        viewModelScope.launch(defaultDispatcher) {
            _currencies.filterNotNull().collect {
                _receiveCurrency.value = it.rates.keys.firstOrNull()
            }
        }

        viewModelScope.launch(defaultDispatcher) {
            combine(
                _amount,
                _sellCurrency.filterNotNull(),
                _receiveCurrency.filterNotNull(),
                ::Triple
            ).distinctUntilChanged()
                .collect { (amount, sellCurrency, receiveCurrency) ->
                    convertCurrency(amount, sellCurrency, receiveCurrency)
                }
        }
    }

    private fun convertCurrency(amount: Double, sellCurrency: String, receiveCurrency: String) {
        viewModelScope.launch(defaultDispatcher) {
            if (amount > EMPTY_BALANCE) {
                val rate = currencyRates.value[receiveCurrency]

                if (rate != null) {
                    val convertedAmount = (amount * rate).roundTo()

                    val fee = calculateFee(amount).roundTo()
                    commission.value = fee.toString()

                    val newSellCurrencyBalance = ((userBalance.value[sellCurrency]
                        ?: EMPTY_BALANCE) - amount - fee).roundTo()

                    if (newSellCurrencyBalance >= EMPTY_BALANCE) {
                        sellCurrencyBalance.value = sellCurrency to newSellCurrencyBalance
                        receiveCurrencyBalance.value =
                            receiveCurrency to (userBalance.value[receiveCurrency]
                                ?: EMPTY_BALANCE) + convertedAmount

                        _exchangeResult.value = convertedAmount
                        _error.value = null
                    } else {
                        _error.value = "Insufficient balance"
                    }

                } else {
                    _error.value = "Rate not available"
                }
            } else {
                _exchangeResult.value = amount
            }
        }
    }

    private fun calculateFee(amount: Double): Double {
        return if (freeConversions.value <= 0) {
            amount * FEE
        } else {
            FREE_FEE
        }
    }

    fun setAmount(amount: String) {
        _amount.value = if (amount.isEmpty()) {
            EMPTY_BALANCE
        } else {
            amount.toDouble().roundTo()
        }
    }

    fun submitExchange() {
        viewModelScope.launch(defaultDispatcher) {
            userDataRepository.updateFreeConversions()

            val balances = userBalance.value.toMutableMap().apply {
                putAll(listOf(sellCurrencyBalance.value, receiveCurrencyBalance.value))
            }

            userDataRepository.updateBalances(balances)

            _conversionResult.value = ConversionResult(
                sellValue = "${sellCurrencyBalance.value.first} ${_amount.value}",
                receiveValue = "${receiveCurrencyBalance.value.first} ${_exchangeResult.value}",
                commissionValue = commission.value
            )

            _exchangeResult.value = null
        }
    }

    fun selectSellCurrency(sellCurrency: String) {
        _sellCurrency.value = sellCurrency
    }

    fun selectReceiveCurrency(receiveCurrency: String) {
        _receiveCurrency.value = receiveCurrency
    }
}
