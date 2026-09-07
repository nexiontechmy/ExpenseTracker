package com.expensetracker.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class BarEntry(val label: String, val value1: Double, val value2: Double = 0.0)

@Composable
fun DualBarChart(
    entries: List<BarEntry>,
    color1: Color,
    color2: Color,
    modifier: Modifier = Modifier,
    label1: String = "Expense",
    label2: String = "Income"
) {
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    // 15% headroom so the tallest bar doesn't touch the top edge of the chart.
    val maxValue = (entries.maxOfOrNull { maxOf(it.value1, it.value2) } ?: 0.0)
        .coerceAtLeast(1.0) * 1.15

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LegendSwatch(color1, label1)
            LegendSwatch(color2, label2)
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 8.dp)
        ) {
            if (entries.isEmpty()) return@Canvas
            val barGroupWidth = size.width / entries.size
            val barWidth = barGroupWidth * 0.28f
            val radius = androidx.compose.ui.geometry.CornerRadius(barWidth * 0.25f, barWidth * 0.25f)

            entries.forEachIndexed { index, entry ->
                val groupStart = index * barGroupWidth
                val h1 = (entry.value1 / maxValue * size.height).toFloat()
                val h2 = (entry.value2 / maxValue * size.height).toFloat()

                if (entry.value1 > 0) {
                    drawRoundRect(
                        color = color1,
                        topLeft = Offset(groupStart + barGroupWidth * 0.18f, size.height - h1),
                        size = Size(barWidth, h1),
                        cornerRadius = radius
                    )
                }
                if (entry.value2 > 0) {
                    drawRoundRect(
                        color = color2,
                        topLeft = Offset(groupStart + barGroupWidth * 0.54f, size.height - h2),
                        size = Size(barWidth, h2),
                        cornerRadius = radius
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            entries.forEach { entry ->
                Text(
                    entry.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = labelColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LegendSwatch(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            "  $label",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
