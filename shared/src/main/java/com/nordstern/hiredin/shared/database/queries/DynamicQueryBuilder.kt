package com.nordstern.hiredin.shared.database.queries

class DynamicQueryBuilder(private val tableName: String) {
    private val conditions = mutableListOf<String>()
    private val args = mutableListOf<Any>()

    fun whereEquals(column: String, value: Any): DynamicQueryBuilder {
        conditions.add("$column = ?")
        args.add(value)
        return this
    }

    fun whereGreaterThan(column: String, value: Any): DynamicQueryBuilder {
        conditions.add("$column > ?")
        args.add(value)
        return this
    }

    fun orderBy(column: String, ascending: Boolean = true): DynamicQueryBuilder {
        conditions.add("ORDER BY $column ${if (ascending) "ASC" else "DESC"}")
        return this
    }

    fun limit(count: Int): DynamicQueryBuilder {
        conditions.add("LIMIT $count")
        return this
    }

    fun buildSelect(): Pair<String, Array<Any>> {
        val where = conditions.filter { !it.startsWith("ORDER") && !it.startsWith("LIMIT") }
        val suffix = conditions.filter { it.startsWith("ORDER") || it.startsWith("LIMIT") }
        val sql = buildString {
            append("SELECT * FROM $tableName")
            if (where.isNotEmpty()) append(" WHERE ").append(where.joinToString(" AND "))
            if (suffix.isNotEmpty()) append(" ").append(suffix.joinToString(" "))
        }
        return sql to args.toTypedArray()
    }
}
