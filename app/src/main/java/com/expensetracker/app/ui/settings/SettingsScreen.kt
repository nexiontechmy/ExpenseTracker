package com.expensetracker.app.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.Currencies
import com.expensetracker.app.data.ThemeMode
import com.expensetracker.app.ui.AppViewModel

@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()
    val currency by viewModel.currencySymbol.collectAsState()
    val showCurrency by viewModel.showCurrencySymbol.collectAsState()
    val threshold by viewModel.largeAmountThreshold.collectAsState()

    var customCurrencyInput by remember(currency) { mutableStateOf(currency) }
    var showCustomField by remember(currency) { mutableStateOf(Currencies.matchBySymbol(currency) == null) }
    var thresholdInput by remember(threshold) { mutableStateOf(threshold.toInt().toString()) }

    val exportJsonLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { viewModel.exportJson(it); Toast.makeText(context, "Exported to JSON", Toast.LENGTH_SHORT).show() }
    }
    val exportCsvLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { viewModel.exportCsv(it); Toast.makeText(context, "Exported to CSV", Toast.LENGTH_SHORT).show() }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            viewModel.importJson(it, replace = false) { count ->
                Toast.makeText(context, "Imported $count transactions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Column {
                Text("Appearance", style = MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = themeMode == ThemeMode.SYSTEM, onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }, label = { Text("System") })
                    FilterChip(selected = themeMode == ThemeMode.LIGHT, onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }, label = { Text("Light") })
                    FilterChip(selected = themeMode == ThemeMode.DARK, onClick = { viewModel.setThemeMode(ThemeMode.DARK) }, label = { Text("Dark") })
                }
            }
        }
        item { Divider() }
        item {
            Column {
                Text("Currency", style = MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Show currency symbol", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Turn off to show plain numbers only",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = showCurrency, onCheckedChange = { viewModel.setShowCurrencySymbol(it) })
                }
                if (showCurrency) {
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Currencies.presets) { option ->
                            FilterChip(
                                selected = !showCustomField && currency == option.symbol,
                                onClick = { showCustomField = false; viewModel.setCurrencySymbol(option.symbol) },
                                label = { Text("${option.symbol} ${option.code}") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = showCustomField,
                                onClick = { showCustomField = true },
                                label = { Text("Custom") }
                            )
                        }
                    }
                    if (showCustomField) {
                        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
                        OutlinedTextField(
                            value = customCurrencyInput,
                            onValueChange = { customCurrencyInput = it; if (it.isNotBlank()) viewModel.setCurrencySymbol(it) },
                            label = { Text("Custom currency symbol") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        item { Divider() }
        item {
            Column {
                Text("Large amount threshold", style = MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
                OutlinedTextField(
                    value = thresholdInput,
                    onValueChange = { text ->
                        thresholdInput = text.filter { it.isDigit() }
                        thresholdInput.toDoubleOrNull()?.let { viewModel.setLargeAmountThreshold(it) }
                    },
                    label = { Text("Flag expenses above this amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        item { Divider() }
        item {
            Column {
                Text("Backup", style = MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { exportJsonLauncher.launch("expense_tracker_backup.json") }) {
                        Text("Export JSON")
                    }
                    OutlinedButton(onClick = { exportCsvLauncher.launch("expense_tracker_export.csv") }) {
                        Text("Export CSV")
                    }
                }
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
                OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json")) }) {
                    Text("Import from JSON")
                }
            }
        }
        item { Divider() }
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Google Sheets Sync", style = MaterialTheme.typography.titleMedium)
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 4.dp))
                    Text(
                        "Coming soon — sync your transactions to a Google Sheet automatically.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
                    Button(onClick = {}, enabled = false) {
                        Text("Connect Google Sheets")
                    }
                }
            }
        }
    }
}
