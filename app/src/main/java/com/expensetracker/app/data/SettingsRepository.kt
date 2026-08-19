package com.expensetracker.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class SettingsRepository(private val context: Context) {
    private val THEME_KEY = stringPreferencesKey("theme_mode")
    private val THRESHOLD_KEY = doublePreferencesKey("large_amount_threshold")
    private val CURRENCY_KEY = stringPreferencesKey("currency_symbol")
    private val SHOW_CURRENCY_KEY = booleanPreferencesKey("show_currency_symbol")

    val themeMode = context.dataStore.data.map { prefs ->
        prefs[THEME_KEY]?.let { ThemeMode.valueOf(it) } ?: ThemeMode.SYSTEM
    }

    val largeAmountThreshold = context.dataStore.data.map { prefs ->
        prefs[THRESHOLD_KEY] ?: 500.0
    }

    val currencySymbol = context.dataStore.data.map { prefs ->
        prefs[CURRENCY_KEY] ?: "RM"
    }

    val showCurrencySymbol = context.dataStore.data.map { prefs ->
        prefs[SHOW_CURRENCY_KEY] ?: true
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[THEME_KEY] = mode.name }
    }

    suspend fun setLargeAmountThreshold(value: Double) {
        context.dataStore.edit { it[THRESHOLD_KEY] = value }
    }

    suspend fun setCurrencySymbol(symbol: String) {
        context.dataStore.edit { it[CURRENCY_KEY] = symbol }
    }

    suspend fun setShowCurrencySymbol(show: Boolean) {
        context.dataStore.edit { it[SHOW_CURRENCY_KEY] = show }
    }
}
