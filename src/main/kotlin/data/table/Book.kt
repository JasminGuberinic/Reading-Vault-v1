package org.virtualcode.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.virtualcode.data.table.Books.nullable
import org.jetbrains.exposed.sql.javatime.datetime
import org.virtualcode.domain.enums.BookCondition
import org.virtualcode.domain.enums.BookStatus

/**
 * Exposed table definition for books.
 */
object Books : Table("books") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id) // Dodano
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val isbn = varchar("isbn", 20).nullable()
    val yearPublished = integer("year_published").nullable()
    val totalPages = integer("total_pages").nullable()
    val status = enumerationByName("status", 20, BookStatus::class)
    val condition = enumerationByName("condition", 20, BookCondition::class)
    val location = varchar("location", 255).nullable()
    val qrCode = varchar("qr_code", 255).nullable()
    val dateAcquired = date("date_acquired").nullable()
    val startedReading = datetime("started_reading").nullable()
    val dateRead = datetime("date_read").nullable()
    val rating = integer("rating").nullable()
    val currentPage = integer("current_page").default(0)
    val lastReadAt = datetime("last_read_at").nullable()
    override val primaryKey = PrimaryKey(id)
}