package org.virtualcode.service

import org.virtualcode.domain.model.BookLending
import org.virtualcode.domain.repository.BookLendingRepository
import org.virtualcode.domain.repository.BookRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class BookLendingServiceImpl @Inject constructor(
    private val bookLendingRepository: BookLendingRepository,
    private val bookRepository: BookRepository
) : BookLendingService {
    /**
     * Lend a book to someone
     * @throws IllegalArgumentException if book doesn't exist or is already lent
     */
    override fun lendBook(lending: BookLending): BookLending {
        validateBook(lending.bookId)
        validateBookNotLent(lending.bookId)
        validateLendingDates(lending.lendingDate, lending.expectedReturnDate)
        return bookLendingRepository.create(lending)
    }

    /**
     * Return a book
     * @throws IllegalArgumentException if lending record doesn't exist
     */
    override fun returnBook(
        lendingId: Int,
        returnDate: LocalDateTime
    ): BookLending {
        val lending = getLendingById(lendingId)
        require(lending.actualReturnDate == null) { "Book has already been returned" }
        return bookLendingRepository.returnBook(lendingId, returnDate)
            ?: throw IllegalStateException("Failed to update lending record")
    }

    /**
     * Get current lending for a book
     */
    override fun getCurrentLending(bookId: Int): BookLending? {
        validateBook(bookId)
        return bookLendingRepository.getCurrentLending(bookId)
    }

    /**
     * Get lending history for a book
     */
    override fun getLendingHistory(bookId: Int): List<BookLending> {
        validateBook(bookId)
        return bookLendingRepository.getLendingHistoryForBook(bookId)
    }

    /**
     * Get all overdue lendings
     */
    override fun getOverdueLendings(): List<BookLending> {
        return bookLendingRepository.getOverdueLendings()
    }

    /**
     * Get lending by ID
     * @throws IllegalArgumentException if lending doesn't exist
     */
    override fun getLendingById(id: Int): BookLending {
        return bookLendingRepository.findById(id)
            ?: throw IllegalArgumentException("Lending record not found with id: $id")
    }

    /**
     * Check if a book is currently lent
     */
    override fun isBookLent(bookId: Int): Boolean {
        validateBook(bookId)
        return bookLendingRepository.isBookLent(bookId)
    }

    /**
     * Get lending statistics
     */
    override fun getLendingStats(bookId: Int): BookLendingStats {
        validateBook(bookId)
        val lendings = bookLendingRepository.getLendingHistoryForBook(bookId)

        return BookLendingStats(
            totalLendings = lendings.size,
            currentlyLent = lendings.any { it.actualReturnDate == null },
            averageLendingDuration = calculateAverageLendingDuration(lendings),
            overdueCount = lendings.count { it.isOverdue() },
            uniqueBorrowers = lendings.map { it.borrowerName }.distinct().size
        )
    }

    private fun validateBook(bookId: Int) {
        if (bookRepository.findById(bookId) == null) {
            throw IllegalArgumentException("Book not found with id: $bookId")
        }
    }

    private fun validateBookNotLent(bookId: Int) {
        if (bookLendingRepository.isBookLent(bookId)) {
            throw IllegalStateException("Book is already lent")
        }
    }

    private fun validateLendingDates(lendingDate: LocalDateTime, expectedReturnDate: LocalDateTime) {
        require(!lendingDate.isAfter(expectedReturnDate)) {
            "Lending date must be before or equal to expected return date"
        }
        require(!lendingDate.isAfter(LocalDateTime.now())) {
            "Lending date cannot be in the future"
        }
    }

    private fun calculateAverageLendingDuration(lendings: List<BookLending>): Double? {
        val completedLendings = lendings.filter { it.actualReturnDate != null }
        if (completedLendings.isEmpty()) return null

        val totalDays = completedLendings.sumOf { lending ->
            ChronoUnit.DAYS.between(
                lending.lendingDate,
                lending.actualReturnDate
            )
        }

        return totalDays.toDouble() / completedLendings.size
    }
}

/**
 * Data class for book lending statistics
 */
data class BookLendingStats(
    val totalLendings: Int,
    val currentlyLent: Boolean,
    val averageLendingDuration: Double?, // in days
    val overdueCount: Int,
    val uniqueBorrowers: Int
)