package com.expensetracker.app

import android.app.Application
import com.expensetracker.app.data.AppDatabase
import com.expensetracker.app.data.ExportImportManager
import com.expensetracker.app.data.SettingsRepository
import com.expensetracker.app.data.SheetsSyncManager
import com.expensetracker.app.repository.TransactionRepository

class ExpenseApplication : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { TransactionRepository(database.transactionDao()) }
    val settingsRepository by lazy { SettingsRepository(this) }
    val exportImportManager by lazy { ExportImportManager(this) }
    val sheetsSyncManager by lazy { SheetsSyncManager() }
}
