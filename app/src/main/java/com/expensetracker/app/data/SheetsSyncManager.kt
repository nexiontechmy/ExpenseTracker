package com.expensetracker.app.data

/**
 * Placeholder for future Google Sheets sync.
 * Wiring this up requires a Google Cloud project with a configured OAuth
 * client (Google Sign-In + Sheets API scope) and the Sheets API dependency.
 * Not implemented yet — [isAvailable] gates the Settings UI entry until it is.
 */
class SheetsSyncManager {
    val isAvailable: Boolean = false

    suspend fun signIn(): Result<Unit> =
        Result.failure(NotImplementedError("Google Sheets sync is not implemented yet"))

    suspend fun pushAll(transactions: List<TransactionEntity>): Result<Unit> =
        Result.failure(NotImplementedError("Google Sheets sync is not implemented yet"))

    suspend fun pullAll(): Result<List<TransactionEntity>> =
        Result.failure(NotImplementedError("Google Sheets sync is not implemented yet"))
}
