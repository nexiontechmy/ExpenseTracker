package com.expensetracker.app.repository

import com.expensetracker.app.data.TransactionDao
import com.expensetracker.app.data.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAll()

    fun between(start: Long, end: Long): Flow<List<TransactionEntity>> = dao.getBetween(start, end)

    suspend fun add(transaction: TransactionEntity): Long = dao.insert(transaction)

    suspend fun update(transaction: TransactionEntity) = dao.update(transaction)

    suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)

    suspend fun getById(id: Long): TransactionEntity? = dao.getById(id)

    suspend fun replaceAll(transactions: List<TransactionEntity>) {
        dao.deleteAll()
        dao.insertAll(transactions)
    }

    suspend fun importMerge(transactions: List<TransactionEntity>) {
        dao.insertAll(transactions)
    }
}
