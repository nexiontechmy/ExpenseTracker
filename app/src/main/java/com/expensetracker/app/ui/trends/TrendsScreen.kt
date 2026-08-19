package com.expensetracker.app.ui.trends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.expensetracker.app.ui.AppViewModel
import com.expensetracker.app.ui.components.BarEntry
import com.expensetracker.app.ui.components.CategoryPieChart
import com.expensetracker.app.ui.components.DualBarChart
import com.expensetracker.app.ui.components.StatCard
import com.expensetracker.app.ui.components.formatAmount
import com.expensetracker.app.ui.components.formatMonthShort
import com.expensetracker.app.ui.theme.ExpenseRed
import com.expensetracker.app.ui.theme.IncomeGreen

@Composable
fun TrendsScreen(viewModel: AppViewModel) {
    val trend by viewModel.trend6Months.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()
    val payerTotals by viewModel.payerTotals.collectAsState()
    val currency by viewModel.displayCurrency.collectAsState()

    val avgExpense = if (trend.isNotEmpty()) trend.map { it.expense }.average() else 0.0
    val bestSavingMonth = trend.maxByOrNull { it.income - it.expense }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text("Spending Trends", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Last 6 months: expense vs income", style = MaterialTheme.typography.titleMedium)
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
                    DualBarChart(
                        entries = trend.map { BarEntry(formatMonthShort(it.month), it.expense, it.income) },
                        color1 = ExpenseRed,
                        color2 = IncomeGreen
                    )
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Avg monthly spend",
                    value = formatAmount(currency, avgExpense),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Best saving month",
                    value = bestSavingMonth?.let { formatMonthShort(it.month) } ?: "—",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Text("By category (this month)", style = MaterialTheme.typography.titleMedium)
        }
        item {
            if (categoryTotals.isEmpty()) {
                Text("No expense data for this month yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                CategoryPieChart(totals = categoryTotals, currencySymbol = currency, modifier = Modifier.fillMaxWidth())
            }
        }
        item {
            Text("By payer (this month)", style = MaterialTheme.typography.titleMedium)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                payerTotals.forEach { (payer, amount) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(payer.label)
                        Text(formatAmount(currency, amount))
                    }
                }
            }
        }
    }
}
