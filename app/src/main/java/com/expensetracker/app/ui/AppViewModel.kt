package com.expensetracker.app.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.ExpenseApplication
import com.expensetracker.app.data.CategoryInfo
import com.expensetracker.app.data.Categories
import com.expensetracker.app.data.Payer
import com.expensetracker.app.data.ThemeMode
import com.expensetracker.app.data.TransactionEntity
import com.expensetracker.app.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

data class CategoryTotal(val category: CategoryInfo, val total: Double)
data class MonthTrend(val month: YearMonth, val expense: Double, val income: Double)

class AppViewModel(private val app: ExpenseApplication) : ViewModel() {
    private val zone = ZoneId.systemDefault()

    val themeMode: StateFlow<ThemeMode> = app.settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val currencySymbol: StateFlow<String> = app.settingsRepository.currencySymbol
        .stateIn(viewModelScope, SharingStarted.Eagerly, "RM")

    val showCurrencySymbol: StateFlow<Boolean> = app.settingsRepository.showCurrencySymbol
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val displayCurrency: StateFlow<String> = combine(currencySymbol, showCurrencySymbol) { symbol, show ->
        if (show) symbol else ""
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "RM")

    val largeAmountThreshold: StateFlow<Double> = app.settingsRepository.largeAmountThreshold
        .stateIn(viewModelScope, SharingStarted.Eagerly, 500.0)

    val allTransactions: StateFlow<List<TransactionEntity>> = app.repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth

    val monthTransactions: StateFlow<List<TransactionEntity>> =
        combine(allTransactions, _selectedMonth) { list, month ->
            list.filter { monthOf(it.dateMillis) == month }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val monthExpenseTotal: StateFlow<Double> = monthTransactions
        .map { list -> list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val monthIncomeTotal: StateFlow<Double> = monthTransactions
        .map { list -> list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val categoryTotals: StateFlow<List<CategoryTotal>> = monthTransactions
        .map { list ->
            Categories.expenseCategories.mapNotNull { cat ->
                val sum = list.filter { it.type == TransactionType.EXPENSE && it.categoryId == cat.id }
                    .sumOf { it.amount }
                if (sum > 0) CategoryTotal(cat, sum) else null
            }.sortedByDescending { it.total }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val payerTotals: StateFlow<Map<Payer, Double>> = monthTransactions
        .map { list ->
            Payer.values().associateWith { payer ->
                list.filter { it.type == TransactionType.EXPENSE && it.payer == payer }.sumOf { it.amount }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val trend6Months: StateFlow<List<MonthTrend>> = combine(allTransactions, _selectedMonth) { list, month ->
        (5 downTo 0).map { offset ->
            val m = month.minusMonths(offset.toLong())
            val monthList = list.filter { monthOf(it.dateMillis) == m }
            MonthTrend(
                month = m,
                expense = monthList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount },
                income = monthList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private fun monthOf(dateMillis: Long): YearMonth =
        YearMonth.from(Instant.ofEpochMilli(dateMillis).atZone(zone).toLocalDate())

    fun goToPreviousMonth() { _selectedMonth.value = _selectedMonth.value.minusMonths(1) }
    fun goToNextMonth() { _selectedMonth.value = _selectedMonth.value.plusMonths(1) }
    fun goToMonth(month: YearMonth) { _selectedMonth.value = month }

    fun addTransaction(
        amount: Double,
        type: TransactionType,
        categoryId: String,
        payer: Payer,
        dateMillis: Long,
        note: String
    ) {
        viewModelScope.launch {
            app.repository.add(
                TransactionEntity(
                    amount = amount, type = type, categoryId = categoryId,
                    payer = payer, dateMillis = dateMillis, note = note
                )
            )
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch { app.repository.update(transaction) }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch { app.repository.delete(transaction) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { app.settingsRepository.setThemeMode(mode) }
    }

    fun setCurrencySymbol(symbol: String) {
        viewModelScope.launch { app.settingsRepository.setCurrencySymbol(symbol) }
    }

    fun setShowCurrencySymbol(show: Boolean) {
        viewModelScope.launch { app.settingsRepository.setShowCurrencySymbol(show) }
    }

    fun setLargeAmountThreshold(value: Double) {
        viewModelScope.launch { app.settingsRepository.setLargeAmountThreshold(value) }
    }

    fun exportJson(uri: Uri) {
        viewModelScope.launch { app.exportImportManager.exportJson(uri, allTransactions.value) }
    }

    fun exportCsv(uri: Uri) {
        viewModelScope.launch { app.exportImportManager.exportCsv(uri, allTransactions.value) }
    }

    fun importJson(uri: Uri, replace: Boolean, onDone: (Int) -> Unit) {
        viewModelScope.launch {
            val imported = app.exportImportManager.importJson(uri)
            if (replace) app.repository.replaceAll(imported) else app.repository.importMerge(imported)
            onDone(imported.size)
        }
    }
}
