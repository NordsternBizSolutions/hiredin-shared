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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun LineChart(points: List<ChartPoint>, modifier: Modifier = Modifier, title: String? = null) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()

    Column(modifier) {
        title?.let { Text(it, style = MaterialTheme.typography.titleSmall) }
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    setDrawGridBackground(false)
                    setTouchEnabled(false)

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        this.textColor = textColor
                        granularity = 1f
                    }

                    axisLeft.apply {
                        setDrawGridLines(true)
                        this.textColor = textColor
                    }

                    axisRight.isEnabled = false
                }
            },
            update = { chart ->
                val entries = points.mapIndexed { index, point ->
                    Entry(index.toFloat(), point.value)
                }

                val dataSet = LineDataSet(entries, "").apply {
                    color = primaryColor
                    setCircleColor(primaryColor)
                    lineWidth = 2f
                    circleRadius = 4f
                    setDrawCircleHole(false)
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }

                chart.xAxis.valueFormatter = IndexAxisValueFormatter(points.map { it.label })
                chart.data = LineData(dataSet)
                chart.invalidate()
            },
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
    }
}
