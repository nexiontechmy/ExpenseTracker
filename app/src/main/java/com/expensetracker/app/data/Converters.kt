package com.expensetracker.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromPayer(value: Payer): String = value.name

    @TypeConverter
    fun toPayer(value: String): Payer = Payer.valueOf(value)
}
