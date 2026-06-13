package com.nordstern.hiredin.shared.ui.components.charts

data class ChartPoint(val label: String, val value: Float)

object ChartDataMapper {
    fun toEntries(points: List<ChartPoint>): List<Float> = points.map { it.value }
    fun labels(points: List<ChartPoint>): List<String> = points.map { it.label }
}
