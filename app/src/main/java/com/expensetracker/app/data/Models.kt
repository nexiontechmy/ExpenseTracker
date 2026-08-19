package com.expensetracker.app.data

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class TransactionType { EXPENSE, INCOME }

enum class Payer(val label: String) {
    ME("Me"),
    SPOUSE("Wife"),
    SPLIT("Split")
}

data class CategoryInfo(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val color: Color
)

object Categories {
    val BILLS = CategoryInfo("bills", "Bills & Utilities", Icons.Filled.Receipt, Color(0xFF5C6BC0))
    val FOOD = CategoryInfo("food", "Food & Drinks", Icons.Filled.Restaurant, Color(0xFFFF7043))
    val GROCERY = CategoryInfo("grocery", "Grocery", Icons.Filled.ShoppingCart, Color(0xFF66BB6A))
    val HEALTH = CategoryInfo("health", "Health", Icons.Filled.LocalHospital, Color(0xFFEF5350))
    val PERSONAL_CARE = CategoryInfo("personal_care", "Personal Care", Icons.Filled.Spa, Color(0xFFAB47BC))
    val PETROL = CategoryInfo("petrol", "Petrol", Icons.Filled.LocalGasStation, Color(0xFFFFA726))
    val SAVING = CategoryInfo("saving", "Saving", Icons.Filled.Savings, Color(0xFF26A69A))
    val SHOPEE = CategoryInfo("shopee", "Shopee Payment", Icons.Filled.ShoppingBag, Color(0xFFFF5252))
    val LOANS = CategoryInfo("loans", "Loans", Icons.Filled.AccountBalance, Color(0xFF8D6E63))
    val OTHERS = CategoryInfo("others", "Others", Icons.Filled.MoreHoriz, Color(0xFF78909C))
    val INCOME = CategoryInfo("income", "Income", Icons.Filled.TrendingUp, Color(0xFF43A047))

    val expenseCategories = listOf(
        BILLS, FOOD, GROCERY, HEALTH, PERSONAL_CARE, PETROL, SAVING, SHOPEE, LOANS, OTHERS
    )
    val all = expenseCategories + INCOME

    fun byId(id: String): CategoryInfo = all.find { it.id == id } ?: OTHERS
}

data class CurrencyOption(val code: String, val symbol: String, val label: String)

object Currencies {
    val presets = listOf(
        CurrencyOption("MYR", "RM", "Malaysian Ringgit"),
        CurrencyOption("USD", "$", "US Dollar"),
        CurrencyOption("SGD", "S$", "Singapore Dollar"),
        CurrencyOption("EUR", "€", "Euro"),
        CurrencyOption("GBP", "£", "British Pound"),
        CurrencyOption("IDR", "Rp", "Indonesian Rupiah"),
        CurrencyOption("INR", "₹", "Indian Rupee"),
        CurrencyOption("JPY", "¥", "Japanese Yen")
    )

    fun matchBySymbol(symbol: String): CurrencyOption? = presets.find { it.symbol == symbol }
}
