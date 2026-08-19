package com.expensetracker.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.expensetracker.app.ui.CategoryTotal

@Composable
fun CategoryPieChart(
    totals: List<CategoryTotal>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val total = totals.sumOf { it.total }.coerceAtLeast(0.01)

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(140.dp)) {
            var startAngle = -90f
            totals.forEach { entry ->
                val sweep = (entry.total / total * 360f).toFloat()
                drawArc(
                    color = entry.category.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }
        }
        Column(
            modifier = Modifier.padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            totals.forEach { entry ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(entry.category.color)
                    )
                    Text(
                        "  ${entry.category.label} · ${(entry.total / total * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
