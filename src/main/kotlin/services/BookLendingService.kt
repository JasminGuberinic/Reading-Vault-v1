package org.virtualcode.service

import org.virtualcode.domain.model.BookLending
import java.time.LocalDateTime

/**
 * Service interface for managing book lending.
 */
interface BookLendingService {
    /**
     * Lend a book to someone
     * @throws IllegalArgumentException if book doesn't exist or is already lent
     */
    fun lendBook(lending: BookLending): BookLending

    /**
     * Return a book
     * @throws IllegalArgumentException if lending record doesn't exist
     */
    fun returnBook(lendingId: Int, returnDate: LocalDateTime = LocalDateTime.now()): BookLending

    /**
     * Get current lending for a book
     */
    fun getCurrentLending(bookId: Int): BookLending?

    /**
     * Get lending history for a book
     */
    fun getLendingHistory(bookId: Int): List<BookLending>

    /**
     * Get all overdue lendings
     */
    fun getOverdueLendings(): List<BookLending>

    /**
     * Get lending by ID
     * @throws IllegalArgumentException if lending doesn't exist
     */
    fun getLendingById(id: Int): BookLending

    /**
     * Check if a book is currently lent
     */
    fun isBookLent(bookId: Int): Boolean

    /**
     * Get lending statistics
     */
    fun getLendingStats(bookId: Int): BookLendingStats
}