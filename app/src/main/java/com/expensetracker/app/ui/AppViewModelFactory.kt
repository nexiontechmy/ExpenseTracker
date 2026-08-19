package com.expensetracker.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.expensetracker.app.ExpenseApplication

class AppViewModelFactory(private val app: ExpenseApplication) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
