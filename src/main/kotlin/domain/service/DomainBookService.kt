package org.virtualcode.domain.service

import org.virtualcode.domain.model.Book
import org.virtualcode.domain.model.BookLending
import org.virtualcode.domain.model.ReadingProgress

interface DomainBookService {
    /**
     * Start reading a book
     */
    fun startReading(book: Book): Book

    /**
     * Update reading progress
     */
    fun updateReadingProgress(book: Book, currentPage: Int, minutesRead: Int?): Book

    /**
     * Complete reading a book
     */
    fun completeReading(book: Book, rating: Int?): Book

    /**
     * Calculate reading statistics for a book
     */
    fun calculateReadingStats(book: Book, readingProgress: List<ReadingProgress>): BookReadingStats

    /**
     * Validate book can be lent
     */
    fun canBeLent(book: Book): Boolean

    /**
     * Check if book is overdue
     */
    fun isOverdue(book: Book, lending: BookLending): Boolean

    /**
     * Calculate reading pace
     */
    fun calculateReadingPace(book: Book, readingProgress: List<ReadingProgress>): ReadingPace

    data class BookReadingStats(
        val percentageComplete: Double,
        val averageMinutesPerPage: Double?,
        val estimatedTimeToComplete: Int?,
        val daysReading: Int,
        val currentStreak: Int
    )

    data class ReadingPace(
        val pagesPerHour: Double?,
        val estimatedDaysToComplete: Int?,
        val readingConsistency: ReadingConsistency
    )

    enum class ReadingConsistency {
        EXCELLENT, GOOD, IRREGULAR, POOR
    }
}