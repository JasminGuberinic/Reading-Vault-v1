package org.virtualcode.domain.repository

import org.virtualcode.domain.model.BookLending
import java.time.LocalDateTime

/**
 * Repository interface for BookLending entity.
 * Defines operations for accessing and manipulating book lending records.
 */
interface BookLendingRepository {
    /**
     * Get all lending records for a book
     */
    fun getLendingHistoryForBook(bookId: Int): List<BookLending>

    /**
     * Get a lending record by its ID
     */
    fun findById(id: Int): BookLending?

    /**
     * Create new lending record
     */
    fun create(lending: BookLending): BookLending

    /**
     * Update existing lending record
     */
    fun update(lending: BookLending): BookLending

    /**
     * Delete lending record
     */
    fun delete(id: Int): Boolean

    /**
     * Get current lending for a book
     */
    fun getCurrentLending(bookId: Int): BookLending?

    /**
     * Get all overdue lendings
     */
    fun getOverdueLendings(): List<BookLending>

    /**
     * Get lending records between dates
     */
    fun getLendingsBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<BookLending>

    /**
     * Return a book
     */
    fun returnBook(lendingId: Int, returnDate: LocalDateTime = LocalDateTime.now()): BookLending?

    /**
     * Check if book is currently lent
     */
    fun isBookLent(bookId: Int): Boolean
}