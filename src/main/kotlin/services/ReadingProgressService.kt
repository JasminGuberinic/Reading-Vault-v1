package org.virtualcode.service

import org.virtualcode.domain.model.ReadingProgress
import java.time.LocalDateTime

/**
 * Service interface for managing reading progress.
 */
interface ReadingProgressService {
    /**
     * Record new reading progress
     * @throws IllegalArgumentException if book doesn't exist or invalid page number
     */
    fun recordProgress(progress: ReadingProgress): ReadingProgress

    /**
     * Get all reading progress for a book
     */
    fun getBookProgress(bookId: Int): List<ReadingProgress>

    /**
     * Get latest reading progress for a book
     */
    fun getLatestProgress(bookId: Int): ReadingProgress?

    /**
     * Get reading progress between dates
     */
    fun getProgressBetweenDates(
        bookId: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ReadingProgress>

    /**
     * Get daily reading progress
     */
    fun getDailyProgress(bookId: Int): List<ReadingProgress>

    /**
     * Get reading statistics for a book
     */
    fun getReadingStats(bookId: Int): ReadingStats

    /**
     * Get reading streak information
     */
    fun getReadingStreak(bookId: Int): ReadingStreak
}