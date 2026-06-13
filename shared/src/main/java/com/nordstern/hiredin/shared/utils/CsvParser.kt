package com.nordstern.hiredin.shared.utils

object CsvParser {
    fun parse(content: String, delimiter: Char = ','): List<List<String>> =
        content.lineSequence()
            .filter { it.isNotBlank() }
            .map { line -> line.split(delimiter).map { it.trim().trim('"') } }
            .toList()

    fun toCsv(rows: List<List<String>>, delimiter: Char = ','): String =
        rows.joinToString("\n") { row ->
            row.joinToString(delimiter.toString()) { value ->
                if (value.contains(delimiter) || value.contains('"')) {
                    "\"${value.replace("\"", "\"\"")}\""
                } else {
                    value
                }
            }
        }
}
