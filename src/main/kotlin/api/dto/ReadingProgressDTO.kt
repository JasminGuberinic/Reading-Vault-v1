package org.virtualcode.api.dto

import org.virtualcode.domain.model.ReadingProgress
import org.virtualcode.service.ReadingStats
import org.virtualcode.service.ReadingStreak
import java.time.LocalDateTime
import java.time.LocalDate

data class ReadingProgressDTO(
    val id: Int? = null,
    val bookId: Int,
    val currentPage: Int,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val minutesRead: Int? = null,
    val notes: String? = null
) {
    fun toDomain(): ReadingProgress = ReadingProgress(
        id = id,
        bookId = bookId,
        currentPage = currentPage,
        timestamp = timestamp,
        minutesRead = minutesRead,
        notes = notes
    )
}

fun ReadingProgress.toDTO(): ReadingProgressDTO = ReadingProgressDTO(
    id = id,
    bookId = bookId,
    currentPage = currentPage,
    timestamp = timestamp,
    minutesRead = minutesRead,
    notes = notes
)

data class ReadingStatsDTO(
    val totalPagesRead: Int,
    val totalMinutesRead: Int,
    val averageReadingSpeed: Double?, // pages per minute
    val completionPercentage: Double?,
    val daysRead: Int,
    val lastReadDate: LocalDateTime?
)

fun ReadingStats.toDTO(): ReadingStatsDTO = ReadingStatsDTO(
    totalPagesRead = totalPagesRead,
    totalMinutesRead = totalMinutesRead,
    averageReadingSpeed = averageReadingSpeed,
    completionPercentage = completionPercentage,
    daysRead = daysRead,
    lastReadDate = lastReadDate
)

data class ReadingStreakDTO(
    val currentStreak: Int,
    val longestStreak: Int,
    val currentStreakStartDate: LocalDate?,
    val longestStreakStartDate: LocalDate?
)

fun ReadingStreak.toDTO(): ReadingStreakDTO = ReadingStreakDTO(
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    currentStreakStartDate = currentStreakStartDate,
    longestStreakStartDate = longestStreakStartDate
)