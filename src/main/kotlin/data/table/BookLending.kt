package org.virtualcode.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object BookLending : Table("book_lending") {
    val id = integer("id").autoIncrement()
    val bookId = integer("book_id").references(Books.id)
    val borrowerName = varchar("borrower_name", 255)
    val borrowerContact = varchar("borrower_contact", 255)
    val lendingDate = datetime("lending_date")
    val expectedReturnDate = datetime("expected_return_date")
    val actualReturnDate = datetime("actual_return_date").nullable()
    val notes = text("notes").nullable()

    override val primaryKey = PrimaryKey(id)
}