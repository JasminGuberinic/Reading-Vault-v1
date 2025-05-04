package org.virtualcode.domain.service

import com.google.inject.Inject
import com.google.inject.Singleton
import org.virtualcode.domain.model.Book
import org.virtualcode.domain.model.BookLending
import org.virtualcode.domain.model.ReadingProgress
import org.virtualcode.domain.enums.BookCondition
import org.virtualcode.domain.enums.BookStatus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.pow
import kotlin.math.sqrt

@Singleton
class DomainBookServiceImpl @Inject constructor() : DomainBookService {

    override fun startReading(book: Book): Book {
        require(!book.isCurrentlyReading()) { "Book is already being read" }
        require(!book.isRead()) { "Book has already been read" }

        return book.copy(
            status = BookStatus.IN_PROGRESS,
            startedReading = LocalDateTime.now(),
            lastReadAt = LocalDateTime.now()
        )
    }

    override fun updateReadingProgress(book: Book, currentPage: Int, minutesRead: Int?): Book {
        require(book.isCurrentlyReading()) { "Book is not currently being read" }
        require(currentPage >= book.currentPage) { "New page number cannot be less than current page" }
        require(book.totalPages == null || currentPage <= book.totalPages) {
            "Page number cannot exceed total pages"
        }

        val newStatus = when {
            book.totalPages != null && currentPage >= book.totalPages -> BookStatus.COMPLETED
            else -> BookStatus.IN_PROGRESS
        }

        return book.copy(
            currentPage = currentPage,
            lastReadAt = LocalDateTime.now(),
            status = newStatus,
            dateRead = if (newStatus == BookStatus.COMPLETED) LocalDateTime.now() else null
        )
    }

    override fun completeReading(book: Book, rating: Int?): Book {
        require(book.isCurrentlyReading()) { "Book is not currently being read" }
        require(rating == null || rating in 1..5) { "Rating must be between 1 and 5" }

        return book.copy(
            status = BookStatus.COMPLETED,
            dateRead = LocalDateTime.now(),
            rating = rating,
            currentPage = book.totalPages ?: book.currentPage
        )
    }

    override fun calculateReadingStats(
        book: Book,
        readingProgress: List<ReadingProgress>
    ): DomainBookService.BookReadingStats {
        val totalPages = book.totalPages ?: return DomainBookService.BookReadingStats(
            percentageComplete = 0.0,
            averageMinutesPerPage = null,
            estimatedTimeToComplete = null,
            daysReading = 0,
            currentStreak = 0
        )

        val percentageComplete = (book.currentPage.toDouble() / totalPages) * 100

        val validProgress = readingProgress.filter { it.minutesRead != null }
        val averageMinutesPerPage = if (validProgress.isNotEmpty()) {
            val totalMinutes = validProgress.sumOf { it.minutesRead!! }
            val totalPagesRead = validProgress.sumOf { it.currentPage - (validProgress.getOrNull(validProgress.indexOf(it) - 1)?.currentPage ?: 0) }
            if (totalPagesRead > 0) totalMinutes.toDouble() / totalPagesRead else null
        } else null

        val estimatedTimeToComplete = averageMinutesPerPage?.let {
            ((totalPages - book.currentPage) * it).toInt()
        }

        val daysReading = readingProgress.map {
            it.timestamp.toLocalDate()
        }.distinct().size

        val currentStreak = calculateCurrentStreak(readingProgress)

        return DomainBookService.BookReadingStats(
            percentageComplete = percentageComplete,
            averageMinutesPerPage = averageMinutesPerPage,
            estimatedTimeToComplete = estimatedTimeToComplete,
            daysReading = daysReading,
            currentStreak = currentStreak
        )
    }

    override fun canBeLent(book: Book): Boolean {
        return !book.isCurrentlyReading() && book.condition != BookCondition.POOR
    }

    override fun isOverdue(book: Book, lending: BookLending): Boolean {
        return lending.actualReturnDate == null &&
                LocalDateTime.now().isAfter(lending.expectedReturnDate)
    }

    override fun calculateReadingPace(
        book: Book,
        readingProgress: List<ReadingProgress>
    ): DomainBookService.ReadingPace {
        if (readingProgress.isEmpty()) {
            return DomainBookService.ReadingPace(
                pagesPerHour = null,
                estimatedDaysToComplete = null,
                readingConsistency = DomainBookService.ReadingConsistency.POOR
            )
        }

        val validProgress = readingProgress.filter { it.minutesRead != null }
        val pagesPerHour = if (validProgress.isNotEmpty()) {
            val totalMinutes = validProgress.sumOf { it.minutesRead!! }
            val totalPages = validProgress.sumOf { it.currentPage - (validProgress.getOrNull(validProgress.indexOf(it) - 1)?.currentPage ?: 0) }
            if (totalMinutes > 0) (totalPages.toDouble() / totalMinutes) * 60 else null
        } else null

        val estimatedDaysToComplete = if (pagesPerHour != null && book.totalPages != null) {
            val remainingPages = book.totalPages - book.currentPage
            val estimatedHours = remainingPages / pagesPerHour
            (estimatedHours / 2).toInt() // Assuming 2 hours of reading per day
        } else null

        val readingConsistency = calculateReadingConsistency(readingProgress)

        return DomainBookService.ReadingPace(
            pagesPerHour = pagesPerHour,
            estimatedDaysToComplete = estimatedDaysToComplete,
            readingConsistency = readingConsistency
        )
    }

    private fun calculateCurrentStreak(readingProgress: List<ReadingProgress>): Int {
        if (readingProgress.isEmpty()) return 0

        val sortedDates = readingProgress
            .map { it.timestamp.toLocalDate() }
            .distinct()
            .sorted()

        var currentStreak = 1
        for (i in 1 until sortedDates.size) {
            if (sortedDates[i].minusDays(1) == sortedDates[i-1]) {
                currentStreak++
            } else {
                break
            }
        }

        return currentStreak
    }

    private fun calculateReadingConsistency(
        readingProgress: List<ReadingProgress>
    ): DomainBookService.ReadingConsistency {
        if (readingProgress.isEmpty()) return DomainBookService.ReadingConsistency.POOR

        val dates = readingProgress
            .map { it.timestamp.toLocalDate() }
            .distinct()
            .sorted()

        if (dates.size < 2) return DomainBookService.ReadingConsistency.POOR

        val gaps = mutableListOf<Long>()
        for (i in 1 until dates.size) {
            gaps.add(ChronoUnit.DAYS.between(dates[i-1], dates[i]))
        }

        val averageGap = gaps.average()
        val standardDeviation = calculateStandardDeviation(gaps)

        return when {
            averageGap <= 1.5 && standardDeviation <= 1.0 -> DomainBookService.ReadingConsistency.EXCELLENT
            averageGap <= 2.5 && standardDeviation <= 2.0 -> DomainBookService.ReadingConsistency.GOOD
            averageGap <= 4.0 && standardDeviation <= 3.0 -> DomainBookService.ReadingConsistency.IRREGULAR
            else -> DomainBookService.ReadingConsistency.POOR
        }
    }

    private fun calculateStandardDeviation(numbers: List<Long>): Double {
        val mean = numbers.average()
        val variance = numbers.map { (it - mean).pow(2) }.average()
        return sqrt(variance)
    }
}