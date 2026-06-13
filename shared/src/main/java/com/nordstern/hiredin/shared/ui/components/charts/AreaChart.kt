package com.nordstern.hiredin.shared.ui.components.charts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AreaChart(points: List<ChartPoint>, modifier: Modifier = Modifier, title: String? = null) =
    LineChart(points, modifier, title)
