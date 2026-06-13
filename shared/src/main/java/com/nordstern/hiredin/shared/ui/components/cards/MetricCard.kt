package com.nordstern.hiredin.shared.ui.components.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MetricCard(title: String, metric: String, subtitle: String? = null, modifier: Modifier = Modifier) =
    StatsCard(label = title, value = metric, modifier = modifier, trend = subtitle)
