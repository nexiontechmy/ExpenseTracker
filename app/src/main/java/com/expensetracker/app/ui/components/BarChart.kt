package com.expensetracker.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BarEntry(val label: String, val value1: Double, val value2: Double = 0.0)

@Composable
fun DualBarChart(
    entries: List<BarEntry>,
    color1: Color,
    color2: Color,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val maxValue = (entries.maxOfOrNull { maxOf(it.value1, it.value2) } ?: 0.0).coerceAtLeast(1.0)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 8.dp)
        ) {
            val barGroupWidth = size.width / entries.size
            val barWidth = (barGroupWidth * 0.28f)
            entries.forEachIndexed { index, entry ->
                val groupStart = index * barGroupWidth
                val h1 = (entry.value1 / maxValue * size.height).toFloat()
                val h2 = (entry.value2 / maxValue * size.height).toFloat()

                drawRect(
                    color = color1,
                    topLeft = androidx.compose.ui.geometry.Offset(groupStart + barGroupWidth * 0.18f, size.height - h1),
                    size = androidx.compose.ui.geometry.Size(barWidth, h1)
                )
                if (entry.value2 > 0) {
                    drawRect(
                        color = color2,
                        topLeft = androidx.compose.ui.geometry.Offset(groupStart + barGroupWidth * 0.54f, size.height - h2),
                        size = androidx.compose.ui.geometry.Size(barWidth, h2)
                    )
                }
            }
        }
        androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth()) {
            entries.forEach { entry ->
                Text(
                    entry.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = labelColor,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
