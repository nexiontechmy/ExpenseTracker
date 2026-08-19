package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryId: String,
    val payer: Payer,
    val dateMillis: Long,
    val note: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)
