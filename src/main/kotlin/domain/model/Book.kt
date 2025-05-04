package org.virtualcode.domain.model

import org.virtualcode.domain.enums.BookCondition
import org.virtualcode.domain.enums.BookStatus
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain model representing a book in the reading vault.
 * Contains core business logic related to books.
 */
data class Book(
    val id: Int? = null,
    val userId: Int,
    val title: String,
    val author: String,
    val isbn: String? = null,
    val yearPublished: Int? = null,
    val totalPages: Int? = null,
    val status: BookStatus = BookStatus.NOT_STARTED,
    val condition: BookCondition = BookCondition.GOOD,
    val location: String? = null,
    var qrCode: String? = null,
    val dateAcquired: LocalDate? = null,
    val startedReading: LocalDateTime? = null,
    val dateRead: LocalDateTime? = null,
    val rating: Int? = null,
    val currentPage: Int = 0,
    val lastReadAt: LocalDateTime? = null
) {
    /**
     * Validates if the book data is valid according to business rules
     */
    fun isValid(): Boolean {
        return title.isNotBlank() &&
                author.isNotBlank() &&
                (rating == null || (rating in 1..5)) &&
                (yearPublished == null || yearPublished > 0) &&
                (isbn == null || isValidIsbn(isbn)) &&
                (totalPages == null || totalPages > 0) &&
                (currentPage <= (totalPages ?: Int.MAX_VALUE))
    }

    fun readingProgress(): Double? {
        return if (totalPages != null && totalPages > 0) {
            (currentPage.toDouble() / totalPages.toDouble()) * 100
        } else null
    }

    /**
     * Checks if the book has been read
     */
    fun isRead(): Boolean {
        return status == BookStatus.COMPLETED
    }

    fun isCurrentlyReading(): Boolean {
        return status == BookStatus.IN_PROGRESS
    }

    fun generateQrCode(): String {
        // Implementation za generisanje QR koda koji sadrži ID knjige ili ISBN
        // Ovo bi trebalo implementirati sa pravom QR code bibliotekom
        return "book-${id ?: isbn}"
    }

    /**
     * Returns the year when the book was read, or null if not read
     */
    fun readingYear(): Int? {
        return dateRead?.year
    }

    /**
     * Basic ISBN validation (simplified)
     */
    private fun isValidIsbn(isbn: String): Boolean {
        // Remove hyphens and spaces for validation
        val cleanIsbn = isbn.replace("-", "").replace(" ", "")

        // Basic validation for ISBN-10 or ISBN-13
        return when (cleanIsbn.length) {
            10 -> isValidIsbn10(cleanIsbn)
            13 -> isValidIsbn13(cleanIsbn)
            else -> false
        }
    }

    /**
     * Validate ISBN-10 format
     */
    private fun isValidIsbn10(isbn: String): Boolean {
        // Simple check - could be expanded with checksum validation
        return isbn.all { it.isDigit() || it == 'X' && isbn.indexOf(it) == 9 }
    }

    /**
     * Validate ISBN-13 format
     */
    private fun isValidIsbn13(isbn: String): Boolean {
        // Simple check - could be expanded with checksum validation
        return isbn.all { it.isDigit() }
    }

    companion object {
        fun createNewBook(
            id: Int? = null,
            userId: Int,
            title: String,
            author: String,
            totalPages: Int? = null,
            isbn: String? = null,
            yearPublished: Int? = null,
            location: String? = null
        ): Book {
            return Book(
                id = id,
                userId = userId,
                title = title,
                author = author,
                isbn = isbn,
                yearPublished = yearPublished,
                totalPages = totalPages,
                location = location,
                dateAcquired = LocalDate.now()
            ).also {
                it.qrCode = it.generateQrCode()
            }
        }

        fun startReading(book: Book): Book {
            return book.copy(
                status = BookStatus.IN_PROGRESS,
                startedReading = LocalDateTime.now(),
                lastReadAt = LocalDateTime.now()
            )
        }

        fun updateReadingProgress(book: Book, currentPage: Int): Book {
            require(currentPage <= (book.totalPages ?: Int.MAX_VALUE)) {
                "Current page cannot be greater than total pages"
            }

            return book.copy(
                currentPage = currentPage,
                lastReadAt = LocalDateTime.now(),
                status = if (book.totalPages != null && currentPage >= book.totalPages)
                    BookStatus.COMPLETED else BookStatus.IN_PROGRESS
            )
        }
    }
}