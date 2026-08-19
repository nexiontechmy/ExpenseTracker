package com.expensetracker.app.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.Categories
import com.expensetracker.app.data.Payer
import com.expensetracker.app.data.TransactionEntity
import com.expensetracker.app.ui.AppViewModel
import com.expensetracker.app.ui.components.AddEditTransactionSheet
import com.expensetracker.app.ui.components.TransactionListItem
import com.expensetracker.app.ui.components.formatMonth
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: AppViewModel) {
    val allTransactions by viewModel.allTransactions.collectAsState()
    val currency by viewModel.displayCurrency.collectAsState()
    val threshold by viewModel.largeAmountThreshold.collectAsState()

    var payerFilter: Payer? by remember { mutableStateOf(null) }
    var categoryFilter: String? by remember { mutableStateOf(null) }
    var showAddSheet by remember { mutableStateOf(false) }
    var editing: TransactionEntity? by remember { mutableStateOf(null) }

    val zone = ZoneId.systemDefault()
    val filtered = allTransactions
        .filter { payerFilter == null || it.payer == payerFilter }
        .filter { categoryFilter == null || it.categoryId == categoryFilter }
    val grouped = filtered
        .sortedWith(compareByDescending<TransactionEntity> { it.dateMillis }.thenByDescending { it.id })
        .groupBy { YearMonth.from(Instant.ofEpochMilli(it.dateMillis).atZone(zone).toLocalDate()) }

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
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("History", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 12.dp))
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                    FilterChip(selected = payerFilter == null, onClick = { payerFilter = null }, label = { Text("All") })
                    Payer.values().forEach { p ->
                        FilterChip(selected = payerFilter == p, onClick = { payerFilter = if (payerFilter == p) null else p }, label = { Text(p.label) })
                    }
                }
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    item {
                        FilterChip(selected = categoryFilter == null, onClick = { categoryFilter = null }, label = { Text("All categories") })
                    }
                    items(Categories.expenseCategories) { cat ->
                        FilterChip(
                            selected = categoryFilter == cat.id,
                            onClick = { categoryFilter = if (categoryFilter == cat.id) null else cat.id },
                            label = { Text(cat.label) }
                        )
                    }
                }
            }
            if (filtered.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                        Text("No transactions found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                grouped.forEach { (month, txns) ->
                    item(key = "header_$month") {
                        Text(
                            formatMonth(month),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                        )
                    }
                    items(txns, key = { it.id }) { t ->
                        TransactionListItem(
                            transaction = t,
                            currencySymbol = currency,
                            isLargeAmount = t.amount >= threshold,
                            onClick = { editing = t },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
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
