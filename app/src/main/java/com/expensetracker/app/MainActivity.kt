package com.expensetracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.expensetracker.app.ui.AppViewModel
import com.expensetracker.app.ui.AppViewModelFactory
import com.expensetracker.app.ui.navigation.AppNavGraph
import com.expensetracker.app.ui.theme.ExpenseTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as ExpenseApplication

        setContent {
            val viewModel: AppViewModel = viewModel(factory = AppViewModelFactory(app))
            val themeMode by viewModel.themeMode.collectAsState()

            ExpenseTrackerTheme(themeMode = themeMode) {
                AppNavGraph(viewModel)
            }
        }
    }
}
