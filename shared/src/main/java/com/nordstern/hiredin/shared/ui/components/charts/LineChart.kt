package com.nordstern.hiredin.shared.ui.components.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

@Composable
fun LineChart(points: List<ChartPoint>, modifier: Modifier = Modifier, title: String? = null) {
    val producer = remember { ChartEntryModelProducer() }
    LaunchedEffect(points) {
        producer.setEntries(
            listOf(points.mapIndexed { index, point -> FloatEntry(index.toFloat(), point.value) })
        )
    }
    Column(modifier) {
        title?.let { Text(it, style = MaterialTheme.typography.titleSmall) }
        Chart(
            chart = lineChart(),
            chartModelProducer = producer,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
    }
}
