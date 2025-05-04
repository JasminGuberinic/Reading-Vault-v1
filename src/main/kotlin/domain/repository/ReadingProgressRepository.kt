package org.virtualcode.domain.repository

import org.virtualcode.domain.model.ReadingProgress
import java.time.LocalDateTime

/**
 * Repository interface for ReadingProgress entity.
 * Defines operations for accessing and manipulating reading progress records.
 */
interface ReadingProgressRepository {
    /**
     * Get all progress records for a book
     */
    fun getProgressForBook(bookId: Int): List<ReadingProgress>

    /**
     * Get a progress record by its ID
     */
    fun findById(id: Int): ReadingProgress?

    /**
     * Create new progress record
     */
    fun create(progress: ReadingProgress): ReadingProgress

    /**
     * Update existing progress record
     */
    fun update(progress: ReadingProgress): ReadingProgress

    /**
     * Delete progress record
     */
    fun delete(id: Int): Boolean

    /**
     * Get latest progress for a book
     */
    fun getLatestProgress(bookId: Int): ReadingProgress?

    /**
     * Get progress records between dates
     */
    fun getProgressBetween(
        bookId: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ReadingProgress>

    /**
     * Get daily reading statistics
     */
    fun getDailyProgress(bookId: Int): List<ReadingProgress>

    /**
     * Get total minutes read for a book
     */
    fun getTotalMinutesRead(bookId: Int): Int

    /**
     * Get average reading speed (pages per minute)
     */
    fun getAverageReadingSpeed(bookId: Int): Double?
}