package org.virtualcode.data.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.virtualcode.data.table.Books
import org.virtualcode.domain.model.Book
import org.virtualcode.domain.repository.BookRepository
import java.time.LocalDate
import com.google.inject.Inject
import com.google.inject.Singleton
import org.virtualcode.domain.enums.BookStatus
import java.time.LocalDateTime

/**
 * Implementation of BookRepository using Exposed SQL framework.
 */
@Singleton
class BookRepositoryImpl @Inject constructor(private val database: Database) : BookRepository {

    /**
     * Initialize the database schema
     */
    fun createSchemaIfNotExists() {
        transaction(database) {
            SchemaUtils.create(Books)
        }
    }

    /**
     * Get all books from the database
     */
    override fun getAllBooks(userId: Int?): List<Book> {
        return transaction(database) {
            Books.selectAll().where { (if (userId != null) Books.userId eq userId else Op.TRUE) }.map { row ->
                row.toBook()
            }
        }
    }

    /**
     * Get a book by its ID
     */
    override fun findById(id: Int, userId: Int?): Book? = transaction {
        Books.selectAll().where { Books.id eq id and (if (userId != null) Books.userId eq userId else Op.TRUE) }
    }.singleOrNull()?.toBook()

    override fun findByQRCode(qr: String, userId: Int?): Book? = transaction {
        Books.selectAll().where { Books.qrCode eq qr and (if (userId != null) Books.userId eq userId else Op.TRUE) }
    }.singleOrNull()?.toBook()

    /**
     * Add a new book to the database
     */
    override fun create(book: Book): Book = transaction {
        val id = Books.insert {
            it[title] = book.title
            it[author] = book.author
            it[isbn] = book.isbn
            it[yearPublished] = book.yearPublished
            it[totalPages] = book.totalPages
            it[status] = book.status
            it[condition] = book.condition
            it[location] = book.location
            it[qrCode] = book.qrCode
            it[dateAcquired] = book.dateAcquired
            it[currentPage] = book.currentPage
            it[userId] = book.userId
        } get Books.id

        book.copy(id = id)
    }

    /**
     * Update an existing book
     */
    override fun update(book: Book): Book = transaction {
        Books.update({ Books.id eq book.id!! }) {
            it[title] = book.title
            it[author] = book.author
            it[isbn] = book.isbn
            it[yearPublished] = book.yearPublished
            it[totalPages] = book.totalPages
            it[status] = book.status
            it[condition] = book.condition
            it[location] = book.location
            it[qrCode] = book.qrCode
            it[currentPage] = book.currentPage
            it[lastReadAt] = book.lastReadAt
            it[userId] = book.userId
        }
        book
    }

    /**
     * Delete a book by its ID
     */
    override fun delete(id: Int, userId: Int?): Boolean {
        return transaction(database) {
            Books.deleteWhere { (Books.id eq id) and (if (userId != null) Books.userId eq userId else Op.TRUE) } > 0
        }
    }

    /**
     * Find books by author and User Id
     */
    override fun findBooksByAuthor(author: String, userId: Int?): List<Book> {
        return transaction(database) {
            Books.selectAll().where { (Books.author like "%$author%") and (if (userId != null) Books.userId eq userId else Op.TRUE) }
                .map { it.toBook() }
        }
    }

    /**
     * Find books read between dates
     */
    override fun findBooksReadBetween(startDate: LocalDate, endDate: LocalDate, userId: Int?): List<Book> {
        return transaction(database) {
            Books.selectAll().where { (Books.dateRead greaterEq startDate) and
                    (Books.dateRead lessEq endDate) and
                    (if (userId != null) Books.userId eq userId else Op.TRUE)}.map { it.toBook() }
        }
    }

    override fun findByQrCode(qrCode: String, userId: Int?): Book? = transaction {
        Books.selectAll().where { Books.qrCode eq qrCode and (if (userId != null) Books.userId eq userId else Op.TRUE)}
            .singleOrNull()
            ?.toBook()
    }

    override fun updateReadingProgress(bookId: Int, currentPage: Int, userId: Int?): Book? = transaction {
        Books.update({ Books.id eq bookId  and (if (userId != null) Books.userId eq userId else Op.TRUE)}) {
            it[Books.currentPage] = currentPage
            it[lastReadAt] = LocalDateTime.now()
        }
        findById(bookId, userId)
    }

    override fun findCurrentlyReading(userId: Int?): List<Book> = transaction {
        Books.selectAll().where { Books.status eq BookStatus.IN_PROGRESS and (if (userId != null) Books.userId eq userId else Op.TRUE)}
            .map { it.toBook() }
    }

    /**
     * Convert a database row to a Book object
     */

    private fun ResultRow.toBook(): Book = Book(
        id = this[Books.id],
        title = this[Books.title],
        author = this[Books.author],
        isbn = this[Books.isbn],
        yearPublished = this[Books.yearPublished],
        totalPages = this[Books.totalPages],
        status = this[Books.status],
        condition = this[Books.condition],
        location = this[Books.location],
        qrCode = this[Books.qrCode],
        dateAcquired = this[Books.dateAcquired],
        startedReading = this[Books.startedReading],
        dateRead = this[Books.dateRead],
        rating = this[Books.rating],
        currentPage = this[Books.currentPage],
        lastReadAt = this[Books.lastReadAt],
        userId = this[Books.userId]
    )
}
