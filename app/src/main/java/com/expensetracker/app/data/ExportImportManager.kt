package com.expensetracker.app.data

import android.content.Context
import android.net.Uri
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class TransactionBackup(
    val amount: Double,
    val type: String,
    val categoryId: String,
    val payer: String,
    val dateMillis: Long,
    val note: String
)

@Serializable
data class BackupFile(
    val version: Int = 1,
    val exportedAtMillis: Long,
    val transactions: List<TransactionBackup>
)

private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

class ExportImportManager(private val context: Context) {

    fun exportJson(uri: Uri, transactions: List<TransactionEntity>) {
        val backup = BackupFile(
            exportedAtMillis = System.currentTimeMillis(),
            transactions = transactions.map {
                TransactionBackup(it.amount, it.type.name, it.categoryId, it.payer.name, it.dateMillis, it.note)
            }
        )
        val text = json.encodeToString(backup)
        context.contentResolver.openOutputStream(uri)?.use { out ->
            out.write(text.toByteArray(Charsets.UTF_8))
        }
    }

    fun exportCsv(uri: Uri, transactions: List<TransactionEntity>) {
        val sb = StringBuilder()
        sb.append("date,type,category,payer,amount,note\n")
        transactions.forEach { t ->
            val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(t.dateMillis))
            val category = Categories.byId(t.categoryId).label
            val note = t.note.replace(",", ";").replace("\n", " ")
            sb.append("$date,${t.type.name},$category,${t.payer.label},${t.amount},$note\n")
        }
        context.contentResolver.openOutputStream(uri)?.use { out ->
            out.write(sb.toString().toByteArray(Charsets.UTF_8))
        }
    }

    fun importJson(uri: Uri): List<TransactionEntity> {
        val text = context.contentResolver.openInputStream(uri)?.use {
            it.readBytes().toString(Charsets.UTF_8)
        } ?: return emptyList()
        val backup = json.decodeFromString<BackupFile>(text)
        return backup.transactions.map {
            TransactionEntity(
                amount = it.amount,
                type = TransactionType.valueOf(it.type),
                categoryId = it.categoryId,
                payer = Payer.valueOf(it.payer),
                dateMillis = it.dateMillis,
                note = it.note
            )
        }
    }
}
