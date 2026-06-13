package com.nordstern.hiredin.shared.ui.components.charts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PieChart(points: List<ChartPoint>, modifier: Modifier = Modifier, title: String? = null) =
    BarChart(points, modifier, title)
