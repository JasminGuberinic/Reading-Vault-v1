package org.virtualcode.domain.model

import java.time.LocalDateTime

data class ReadingProgress(
    val id: Int? = null,
    val bookId: Int,
    val currentPage: Int,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val minutesRead: Int? = null,
    val notes: String? = null
) {
    fun calculateDailyProgress(previousProgress: ReadingProgress?): Int {
        return if (previousProgress != null) {
            currentPage - previousProgress.currentPage
        } else {
            currentPage
        }
    }
}