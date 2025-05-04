package org.virtualcode.data.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.virtualcode.data.table.BookLending as BookLendingTable
import org.virtualcode.domain.model.BookLending
import org.virtualcode.domain.repository.BookLendingRepository
import java.time.LocalDateTime
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class BookLendingRepositoryImpl @Inject constructor(private val database: Database) : BookLendingRepository {

    /**
     * Initialize the database schema
     */
    fun createSchemaIfNotExists() {
        transaction(database) {
            SchemaUtils.create(BookLendingTable)
        }
    }

    override fun getLendingHistoryForBook(bookId: Int): List<BookLending> = transaction(database) {
        BookLendingTable.select { BookLendingTable.bookId eq bookId }
            .orderBy(BookLendingTable.lendingDate, SortOrder.DESC)
            .map { it.toLending() }
    }

    override fun findById(id: Int): BookLending? = transaction(database) {
        BookLendingTable.select { BookLendingTable.id eq id }
            .singleOrNull()
            ?.toLending()
    }

    override fun create(lending: BookLending): BookLending = transaction(database) {
        val id = BookLendingTable.insert {
            it[bookId] = lending.bookId
            it[borrowerName] = lending.borrowerName
            it[borrowerContact] = lending.borrowerContact
            it[lendingDate] = lending.lendingDate
            it[expectedReturnDate] = lending.expectedReturnDate
            it[actualReturnDate] = lending.actualReturnDate
            it[notes] = lending.notes
        } get BookLendingTable.id

        lending.copy(id = id)
    }

    override fun update(lending: BookLending): BookLending = transaction(database) {
        BookLendingTable.update({ BookLendingTable.id eq lending.id!! }) {
            it[borrowerName] = lending.borrowerName
            it[borrowerContact] = lending.borrowerContact
            it[lendingDate] = lending.lendingDate
            it[expectedReturnDate] = lending.expectedReturnDate
            it[actualReturnDate] = lending.actualReturnDate
            it[notes] = lending.notes
        }
        lending
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        BookLendingTable.deleteWhere { BookLendingTable.id eq id } > 0
    }

    override fun getCurrentLending(bookId: Int): BookLending? = transaction(database) {
        BookLendingTable
            .selectAll().where {
                (BookLendingTable.bookId eq bookId) and
                        (BookLendingTable.actualReturnDate.isNull())
            }
            .singleOrNull()
            ?.toLending()
    }

    override fun getOverdueLendings(): List<BookLending> = transaction(database) {
        BookLendingTable
            .select {
                (BookLendingTable.expectedReturnDate less LocalDateTime.now()) and
                        (BookLendingTable.actualReturnDate.isNull())
            }
            .map { it.toLending() }
    }

    override fun getLendingsBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<BookLending> = transaction(database) {
        BookLendingTable
            .selectAll().where {
                (BookLendingTable.lendingDate greaterEq startDate) and
                        (BookLendingTable.lendingDate lessEq endDate)
            }
            .map { it.toLending() }
    }

    override fun returnBook(lendingId: Int, returnDate: LocalDateTime): BookLending? = transaction(database) {
        BookLendingTable.update({ BookLendingTable.id eq lendingId }) {
            it[actualReturnDate] = returnDate
        }
        findById(lendingId)
    }

    override fun isBookLent(bookId: Int): Boolean = transaction(database) {
        BookLendingTable
            .selectAll().where {
                (BookLendingTable.bookId eq bookId) and
                        (BookLendingTable.actualReturnDate.isNull())
            }
            .count() > 0
    }

    private fun ResultRow.toLending(): BookLending = BookLending(
        id = this[BookLendingTable.id],
        bookId = this[BookLendingTable.bookId],
        borrowerName = this[BookLendingTable.borrowerName],
        borrowerContact = this[BookLendingTable.borrowerContact],
        lendingDate = this[BookLendingTable.lendingDate],
        expectedReturnDate = this[BookLendingTable.expectedReturnDate],
        actualReturnDate = this[BookLendingTable.actualReturnDate],
        notes = this[BookLendingTable.notes]
    )
}