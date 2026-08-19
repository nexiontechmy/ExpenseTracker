package com.expensetracker.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.Categories
import com.expensetracker.app.data.Payer
import com.expensetracker.app.data.TransactionEntity
import com.expensetracker.app.data.TransactionType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionSheet(
    existing: TransactionEntity?,
    onDismiss: () -> Unit,
    onSave: (amount: Double, type: TransactionType, categoryId: String, payer: Payer, dateMillis: Long, note: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amountText by remember { mutableStateOf(existing?.amount?.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() } ?: "") }
    var type by remember { mutableStateOf(existing?.type ?: TransactionType.EXPENSE) }
    var categoryId by remember { mutableStateOf(existing?.categoryId ?: Categories.expenseCategories.first().id) }
    var payer by remember { mutableStateOf(existing?.payer ?: Payer.ME) }
    var note by remember { mutableStateOf(existing?.note ?: "") }
    var dateMillis by remember { mutableStateOf(existing?.dateMillis ?: System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                if (existing == null) "Add Transaction" else "Edit Transaction",
                style = MaterialTheme.typography.titleLarge
            )
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = type == TransactionType.EXPENSE,
                    onClick = { type = TransactionType.EXPENSE },
                    label = { Text("Expense") }
                )
                FilterChip(
                    selected = type == TransactionType.INCOME,
                    onClick = { type = TransactionType.INCOME; categoryId = Categories.INCOME.id },
                    label = { Text("Income") }
                )
            }

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' }; showError = false },
                label = { Text("Amount") },
                isError = showError,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))

            if (type == TransactionType.EXPENSE) {
                Text("Category", style = MaterialTheme.typography.labelMedium)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Categories.expenseCategories) { cat ->
                        FilterChip(
                            selected = categoryId == cat.id,
                            onClick = { categoryId = cat.id },
                            label = { Text(cat.label) }
                        )
                    }
                }

                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
                Text("Paid by", style = MaterialTheme.typography.labelMedium)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Payer.values().forEach { p ->
                        FilterChip(
                            selected = payer == p,
                            onClick = { payer = p },
                            label = { Text(p.label) }
                        )
                    }
                }
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
            }

            AssistChip(
                onClick = { showDatePicker = true },
                label = {
                    Text(
                        Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    )
                }
            )

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onDelete != null) {
                    OutlinedButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Text(" Delete")
                    }
                }
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount == null || amount <= 0.0) {
                            showError = true
                        } else {
                            onSave(amount, type, if (type == TransactionType.INCOME) Categories.INCOME.id else categoryId, payer, dateMillis, note)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { dateMillis = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}
