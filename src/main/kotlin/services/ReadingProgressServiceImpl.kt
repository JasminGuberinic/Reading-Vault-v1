package org.virtualcode.service

import org.virtualcode.domain.model.ReadingProgress
import org.virtualcode.domain.repository.ReadingProgressRepository
import org.virtualcode.domain.repository.BookRepository
import java.time.LocalDateTime
import java.time.LocalDate
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class ReadingProgressServiceImpl @Inject constructor(
    private val readingProgressRepository: ReadingProgressRepository,
    private val bookRepository: BookRepository
) : ReadingProgressService{
    /**
     * Record new reading progress
     * @throws IllegalArgumentException if book doesn't exist or invalid page number
     */
    override fun recordProgress(progress: ReadingProgress): ReadingProgress {
        validateBook(progress.bookId)
        validatePageNumber(progress)
        return readingProgressRepository.create(progress)
    }

    /**
     * Get all reading progress for a book
     */
    override fun getBookProgress(bookId: Int): List<ReadingProgress> {
        validateBook(bookId)
        return readingProgressRepository.getProgressForBook(bookId)
    }

    /**
     * Get latest reading progress for a book
     */
    override fun getLatestProgress(bookId: Int): ReadingProgress? {
        validateBook(bookId)
        return readingProgressRepository.getLatestProgress(bookId)
    }

    /**
     * Get reading progress between dates
     */
    override fun getProgressBetweenDates(
        bookId: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ReadingProgress> {
        validateBook(bookId)
        validateDateRange(startDate, endDate)
        return readingProgressRepository.getProgressBetween(bookId, startDate, endDate)
    }

    /**
     * Get daily reading progress
     */
    override fun getDailyProgress(bookId: Int): List<ReadingProgress> {
        validateBook(bookId)
        return readingProgressRepository.getDailyProgress(bookId)
    }

    /**
     * Get reading statistics for a book
     */
    override fun getReadingStats(bookId: Int): ReadingStats {
        validateBook(bookId)
        val book = bookRepository.findById(bookId)!!
        val progress = readingProgressRepository.getProgressForBook(bookId)
        val totalMinutes = readingProgressRepository.getTotalMinutesRead(bookId)
        val averageSpeed = readingProgressRepository.getAverageReadingSpeed(bookId)

        return ReadingStats(
            totalPagesRead = progress.lastOrNull()?.currentPage ?: 0,
            totalMinutesRead = totalMinutes,
            averageReadingSpeed = averageSpeed,
            completionPercentage = calculateCompletionPercentage(book.totalPages, progress),
            daysRead = progress.map { it.timestamp.toLocalDate() }.distinct().size,
            lastReadDate = progress.maxByOrNull { it.timestamp }?.timestamp
        )
    }

    /**
     * Get reading streak information
     */
    override fun getReadingStreak(bookId: Int): ReadingStreak {
        validateBook(bookId)
        val progress = readingProgressRepository.getDailyProgress(bookId)
        val readDates = progress.map { it.timestamp.toLocalDate() }.distinct().sorted()

        return calculateReadingStreak(readDates)
    }

    private fun validateBook(bookId: Int) {
        if (bookRepository.findById(bookId) == null) {
            throw IllegalArgumentException("Book not found with id: $bookId")
        }
    }

    private fun validatePageNumber(progress: ReadingProgress) {
        val book = bookRepository.findById(progress.bookId)!!
        if (book.totalPages != null && progress.currentPage > book.totalPages) {
            throw IllegalArgumentException("Page number cannot be greater than total pages")
        }
    }

    private fun validateDateRange(startDate: LocalDateTime, endDate: LocalDateTime) {
        require(!startDate.isAfter(endDate)) {
            "Start date must be before or equal to end date"
        }
    }

    private fun calculateCompletionPercentage(totalPages: Int?, progress: List<ReadingProgress>): Double? {
        if (totalPages == null || totalPages == 0 || progress.isEmpty()) return null
        val currentPage = progress.maxByOrNull { it.timestamp }?.currentPage ?: 0
        return (currentPage.toDouble() / totalPages.toDouble()) * 100
    }

    private fun calculateReadingStreak(readDates: List<LocalDate>): ReadingStreak {
        if (readDates.isEmpty()) return ReadingStreak(0, 0, null, null)

        var currentStreak = 0
        var longestStreak = 0
        var streakStartDate: LocalDate? = null
        var longestStreakStartDate: LocalDate? = null
        var previousDate = readDates.first()

        readDates.forEach { date ->
            if (date.minusDays(1) == previousDate) {
                currentStreak++
                if (streakStartDate == null) streakStartDate = previousDate
                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak
                    longestStreakStartDate = streakStartDate
                }
            } else {
                currentStreak = 1
                streakStartDate = date
            }
            previousDate = date
        }

        return ReadingStreak(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            currentStreakStartDate = streakStartDate,
            longestStreakStartDate = longestStreakStartDate
        )
    }
}

/**
 * Data class for reading statistics
 */
data class ReadingStats(
    val totalPagesRead: Int,
    val totalMinutesRead: Int,
    val averageReadingSpeed: Double?, // pages per minute
    val completionPercentage: Double?,
    val daysRead: Int,
    val lastReadDate: LocalDateTime?
)

/**
 * Data class for reading streak information
 */
data class ReadingStreak(
    val currentStreak: Int,
    val longestStreak: Int,
    val currentStreakStartDate: LocalDate?,
    val longestStreakStartDate: LocalDate?
)