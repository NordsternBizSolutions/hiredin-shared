package com.nordstern.hiredin.shared.database.queries

object PaginationHelper {
    fun offset(page: Int, pageSize: Int): Int = (page.coerceAtLeast(0)) * pageSize

    fun totalPages(totalItems: Int, pageSize: Int): Int =
        if (pageSize <= 0) 0 else ((totalItems + pageSize - 1) / pageSize)

    fun hasNextPage(page: Int, pageSize: Int, totalItems: Int): Boolean =
        offset(page + 1, pageSize) < totalItems
}
