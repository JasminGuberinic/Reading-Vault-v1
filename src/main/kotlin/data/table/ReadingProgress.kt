package org.virtualcode.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ReadingProgress : Table("reading_progress") {
    val id = integer("id").autoIncrement()
    val bookId = integer("book_id").references(Books.id)
    val currentPage = integer("current_page")
    val timestamp = datetime("timestamp")
    val minutesRead = integer("minutes_read").nullable()
    val notes = text("notes").nullable()

    override val primaryKey = PrimaryKey(id)
}