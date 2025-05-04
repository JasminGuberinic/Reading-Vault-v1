package org.virtualcode.domain.repository

import org.virtualcode.domain.model.Book
import java.time.LocalDate

/**
 * Repository interface for Book entity.
 * Defines operations for accessing and manipulating books.
 */
interface BookRepository {
    /**
     * Get all books from the repository for user
     */
    fun getAllBooks(userId: Int? = null): List<Book>

    /**
     * Get a book by its ID and User ID
     */
    fun findById(id: Int, userId: Int? = null): Book?

    /**
     * Add a new book to the repository
     */
    fun create(book: Book): Book

    /**
     * Update an existing book
     */
    fun update(book: Book): Book

    /**
     * Delete a book by its ID and UserId
     */
    fun delete(id: Int, userId: Int? = null): Boolean

    /**
     * Find books by author and User Id
     */
    fun findBooksByAuthor(author: String, userId: Int? = null): List<Book>

    /**
     * Find books read between dates
     */
    fun findBooksReadBetween(startDate: LocalDate, endDate: LocalDate, userId: Int? = null): List<Book>

    fun findByQrCode(qrCode: String, userId: Int?): Book?

    fun updateReadingProgress(bookId: Int, currentPage: Int, userId: Int? = null): Book?

    fun findCurrentlyReading(userId: Int?): List<Book>
}