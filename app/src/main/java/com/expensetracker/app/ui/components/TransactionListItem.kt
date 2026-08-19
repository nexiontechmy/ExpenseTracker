package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.Categories
import com.expensetracker.app.data.TransactionEntity
import com.expensetracker.app.data.TransactionType
import com.expensetracker.app.ui.theme.ExpenseRed
import com.expensetracker.app.ui.theme.IncomeGreen
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFmt = DateTimeFormatter.ofPattern("dd MMM")

@Composable
fun TransactionListItem(
    transaction: TransactionEntity,
    currencySymbol: String,
    isLargeAmount: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = Categories.byId(transaction.categoryId)
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.label,
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(category.color)
                    .padding(9.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (transaction.type == TransactionType.INCOME) "Income" else category.label,
                    style = MaterialTheme.typography.bodyLarge
                )
                val subtitle = buildString {
                    append(Instant.ofEpochMilli(transaction.dateMillis).atZone(ZoneId.systemDefault()).format(dateFmt))
                    if (transaction.type == TransactionType.EXPENSE) append(" · ${transaction.payer.label}")
                    if (transaction.note.isNotBlank()) append(" · ${transaction.note}")
                }
                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isLargeAmount) {
                Icon(
                    Icons.Filled.PriorityHigh,
                    contentDescription = "Large amount",
                    tint = ExpenseRed,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                (if (transaction.type == TransactionType.INCOME) "+" else "-") + formatAmount(currencySymbol, transaction.amount),
                style = MaterialTheme.typography.bodyLarge,
                color = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
            )
        }
    }
}
