package com.expensetracker.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.TransactionEntity
import com.expensetracker.app.ui.AppViewModel
import com.expensetracker.app.ui.components.AddEditTransactionSheet
import com.expensetracker.app.ui.components.MonthSelector
import com.expensetracker.app.ui.components.OverviewCard
import com.expensetracker.app.ui.components.TopCategoriesCard
import com.expensetracker.app.ui.components.TransactionListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: AppViewModel) {
    val month by viewModel.selectedMonth.collectAsState()
    val transactions by viewModel.monthTransactions.collectAsState()
    val expenseTotal by viewModel.monthExpenseTotal.collectAsState()
    val incomeTotal by viewModel.monthIncomeTotal.collectAsState()
    val currency by viewModel.displayCurrency.collectAsState()
    val threshold by viewModel.largeAmountThreshold.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    var editing: TransactionEntity? by remember { mutableStateOf(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text(" Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                MonthSelector(month, onPrevious = viewModel::goToPreviousMonth, onNext = viewModel::goToNextMonth)
            }
            item {
                OverviewCard(
                    currencySymbol = currency,
                    spending = expenseTotal,
                    income = incomeTotal,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            if (categoryTotals.isNotEmpty()) {
                item {
                    TopCategoriesCard(
                        totals = categoryTotals,
                        currencySymbol = currency,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            item {
                Text(
                    "Daily activity",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 2.dp)
                )
            }
            if (transactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        Text("No transactions this month yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(transactions, key = { it.id }) { t ->
                    TransactionListItem(
                        transaction = t,
                        currencySymbol = currency,
                        isLargeAmount = t.amount >= threshold,
                        onClick = { editing = t }
                    )
                }
            }
        }
    }

    if (showAddSheet) {
        AddEditTransactionSheet(
            existing = null,
            onDismiss = { showAddSheet = false },
            onSave = { amount, type, categoryId, payer, dateMillis, note ->
                viewModel.addTransaction(amount, type, categoryId, payer, dateMillis, note)
                showAddSheet = false
            }
        )
    }

    editing?.let { t ->
        AddEditTransactionSheet(
            existing = t,
            onDismiss = { editing = null },
            onSave = { amount, type, categoryId, payer, dateMillis, note ->
                viewModel.updateTransaction(t.copy(amount = amount, type = type, categoryId = categoryId, payer = payer, dateMillis = dateMillis, note = note))
                editing = null
            },
            onDelete = {
                viewModel.deleteTransaction(t)
                editing = null
            }
        )
    }
}
