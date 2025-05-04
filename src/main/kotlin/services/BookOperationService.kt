package org.virtualcode.service

import org.virtualcode.domain.model.ReadingProgress
import org.virtualcode.domain.model.Book
import org.virtualcode.domain.model.BookLending
import org.virtualcode.domain.model.BookNote
import org.virtualcode.domain.service.DomainBookService

/**
 * Service interface for book operations that coordinates between domain and other services.
 */
interface BookOperationService {
    /**
     * Start reading a book
     */
    fun startReading(bookId: Int): Book

    /**
     * Update reading progress
     */
    fun updateReadingProgress(bookId: Int, currentPage: Int, minutesRead: Int?): Book

    /**
     * Complete reading a book
     */
    fun completeReading(bookId: Int, rating: Int?): Book

    /**
     * Get comprehensive book statistics
     */
    fun getBookStatistics(bookId: Int): BookStatistics

    /**
     * Get book with all related information
     */
    fun getBookWithDetails(bookId: Int): BookWithDetails

    /**
     * Get currently reading books with progress
     */
    fun getCurrentlyReadingBooks(): List<BookWithProgress>

    /**
     * Check if book can be lent
     */
    fun canBookBeLent(bookId: Int): Boolean

    data class BookStatistics(
        val readingStats: DomainBookService.BookReadingStats,
        val readingPace: DomainBookService.ReadingPace,
        val lendingHistory: List<BookLending>,
        val notes: List<BookNote>,
        val totalTimesLent: Int,
        val averageLendingDuration: Double?
    )

    data class BookWithDetails(
        val book: Book,
        val currentProgress: ReadingProgress?,
        val currentLending: BookLending?,
        val notes: List<BookNote>,
        val readingStats: DomainBookService.BookReadingStats?
    )

    data class BookWithProgress(
        val book: Book,
        val currentProgress: ReadingProgress?,
        val estimatedDaysToComplete: Int?,
        val readingConsistency: DomainBookService.ReadingConsistency
    )
}