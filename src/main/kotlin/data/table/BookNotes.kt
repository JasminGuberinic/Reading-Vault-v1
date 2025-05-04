package org.virtualcode.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.virtualcode.data.table.Books

object BookNotes : Table("book_notes") {
    val id = integer("id").autoIncrement()
    val bookId = integer("book_id").references(Books.id)
    val content = text("content")
    val page = integer("page").nullable()
    val chapter = varchar("chapter", 100).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}
