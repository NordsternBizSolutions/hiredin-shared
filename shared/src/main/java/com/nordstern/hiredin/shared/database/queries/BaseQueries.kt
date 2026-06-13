package com.nordstern.hiredin.shared.database.queries

object BaseQueries {
    const val SELECT_ALL = "SELECT * FROM %s"
    const val SELECT_BY_ID = "SELECT * FROM %s WHERE id = :id LIMIT 1"
    const val SELECT_UPDATED_SINCE = "SELECT * FROM %s WHERE updatedAt > :timestamp"
    const val DELETE_BY_ID = "DELETE FROM %s WHERE id = :id"
}
