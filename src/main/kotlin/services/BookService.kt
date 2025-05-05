package org.virtualcode.service

import org.virtualcode.domain.model.Book
import java.time.LocalDate

/**
 * Service interface for book-related business logic.
 */
interface BookService {
    /**
     * Get all books
     */
    fun getAllBooks(): List<Book>

    /**
     * Get all books for user
     */
    fun getAllBooks(userId: Int): List<Book>

    /**
     * Get a book by ID
     */
    fun getBookById(id: Int): Book?

    /**
     * Get a book by ID and User ID
     */
    fun getBookById(id: Int, userId: Int): Book?

    fun findByQRCode(qr: String, userId: Int): Book?

    /**
     * Add a new book
     * @throws IllegalArgumentException if book data is invalid
     */
    fun addBook(book: Book): Book

    /**
     * Update an existing book
     * @throws IllegalArgumentException if book ID is null or data is invalid
     */
    fun updateBook(book: Book): Book

    /**
     * Delete a book
     */
    fun deleteBook(id: Int): Boolean

    /**
     * Find books by author
     */
    fun findBooksByAuthor(author: String): List<Book>

    /**
     * Find books by author and User
     */
    fun findBooksByAuthor(author: String, userId: Int): List<Book>

    /**
     * Get books read in a specific year
     */
    fun getBooksReadInYear(year: Int): List<Book>

    /**
     * Get books read in a specific year
     */
    fun getBooksReadInYear(year: Int, userId: Int): List<Book>

    /**
     * Get reading statistics
     */
    fun getReadingStats(): ReadingStats

    /**
     * Get reading statistics
     */
    fun getReadingStats(userId: Int): ReadingStats

    /**
     * Data class for reading statistics
     */
    data class ReadingStats(
        val totalBooks: Int,
        val readBooks: Int,
        val averageRating: Double?,
        val booksByYear: Map<Int, Int>
    )
}