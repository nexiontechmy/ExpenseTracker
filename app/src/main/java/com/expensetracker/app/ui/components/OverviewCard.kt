package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expensetracker.app.ui.theme.ExpenseRed
import com.expensetracker.app.ui.theme.IncomeGreen

@Composable
fun OverviewCard(
    currencySymbol: String,
    spending: Double,
    income: Double,
    modifier: Modifier = Modifier
) {
    val balance = income - spending
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Net Balance",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                formatAmount(currencySymbol, balance),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OverviewColumn(
                    label = "Spending",
                    value = formatAmount(currencySymbol, spending),
                    dotColor = ExpenseRed,
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(
                    modifier = Modifier.height(36.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                )
                OverviewColumn(
                    label = "Income",
                    value = formatAmount(currencySymbol, income),
                    dotColor = IncomeGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OverviewColumn(label: String, value: String, dotColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                "  $label",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold
        )
    }
}
