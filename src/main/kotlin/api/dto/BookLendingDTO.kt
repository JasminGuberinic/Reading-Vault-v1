package org.virtualcode.api.dto

import org.virtualcode.domain.model.BookLending
import org.virtualcode.service.BookLendingStats
import java.time.LocalDateTime

data class BookLendingDTO(
    val id: Int? = null,
    val bookId: Int,
    val borrowerName: String,
    val borrowerContact: String,
    val lendingDate: LocalDateTime,
    val expectedReturnDate: LocalDateTime,
    val actualReturnDate: LocalDateTime? = null,
    val notes: String? = null
) {
    fun toDomain(): BookLending = BookLending(
        id = id,
        bookId = bookId,
        borrowerName = borrowerName,
        borrowerContact = borrowerContact,
        lendingDate = lendingDate,
        expectedReturnDate = expectedReturnDate,
        actualReturnDate = actualReturnDate,
        notes = notes
    )
}

fun BookLending.toDTO(): BookLendingDTO = BookLendingDTO(
    id = id,
    bookId = bookId,
    borrowerName = borrowerName,
    borrowerContact = borrowerContact,
    lendingDate = lendingDate,
    expectedReturnDate = expectedReturnDate,
    actualReturnDate = actualReturnDate,
    notes = notes
)

data class BookLendingStatsDTO(
    val totalLendings: Int,
    val currentlyLent: Boolean,
    val averageLendingDuration: Double?, // in days
    val overdueCount: Int,
    val uniqueBorrowers: Int
)

fun BookLendingStats.toDTO(): BookLendingStatsDTO = BookLendingStatsDTO(
    totalLendings = totalLendings,
    currentlyLent = currentlyLent,
    averageLendingDuration = averageLendingDuration,
    overdueCount = overdueCount,
    uniqueBorrowers = uniqueBorrowers
)