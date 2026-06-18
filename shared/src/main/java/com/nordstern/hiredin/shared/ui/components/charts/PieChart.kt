package com.nordstern.hiredin.shared.ui.components.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun PieChart(points: List<ChartPoint>, modifier: Modifier = Modifier, title: String? = null) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Column(modifier) {
        title?.let { Text(it, style = MaterialTheme.typography.titleSmall) }
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    description.isEnabled = false
                    legend.isEnabled = true
                    legend.textColor = textColor
                    setHoleColor(0) // Transparent hole
                    setDrawEntryLabels(false)
                    setUsePercentValues(true)
                }
            },
            update = { chart ->
                val entries = points.map { point ->
                    PieEntry(point.value, point.label)
                }

                val dataSet = PieDataSet(entries, "").apply {
                    colors = ColorTemplate.MATERIAL_COLORS.toList()
                    valueTextColor = textColor
                    valueTextSize = 12f
                }

                chart.data = PieData(dataSet)
                chart.invalidate()
            },
            modifier = Modifier.fillMaxWidth().height(250.dp)
        )
    }
}
