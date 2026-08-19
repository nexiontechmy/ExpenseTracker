package com.expensetracker.app.ui.components

import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

fun formatAmount(symbol: String, amount: Double): String {
    val separator = if (symbol.length > 1) " " else ""
    return "$symbol$separator${"%,.2f".format(amount)}"
}

fun formatMonth(month: YearMonth): String {
    return "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}"
}

fun formatMonthShort(month: YearMonth): String {
    return "${month.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} '${month.year.toString().takeLast(2)}"
}
