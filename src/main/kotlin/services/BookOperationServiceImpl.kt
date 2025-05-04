package org.virtualcode.service

import com.google.inject.Inject
import com.google.inject.Singleton
import org.virtualcode.domain.model.Book
import org.virtualcode.domain.model.BookLending
import org.virtualcode.domain.model.ReadingProgress
import org.virtualcode.domain.service.DomainBookService
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Singleton
class BookOperationServiceImpl @Inject constructor(
    private val bookService: BookService,
    private val domainBookService: DomainBookService,
    private val readingProgressService: ReadingProgressService,
    private val bookLendingService: BookLendingService,
    private val bookNoteService: BookNoteService
) : BookOperationService {

    override fun startReading(bookId: Int): Book {
        val book = getBookOrThrow(bookId)
        val updatedBook = domainBookService.startReading(book)
        return bookService.updateBook(updatedBook)
    }

    override fun updateReadingProgress(bookId: Int, currentPage: Int, minutesRead: Int?): Book {
        val book = getBookOrThrow(bookId)
        val updatedBook = domainBookService.updateReadingProgress(book, currentPage, minutesRead)

        // Record progress
        readingProgressService.recordProgress(
            ReadingProgress(
            bookId = bookId,
            currentPage = currentPage,
            minutesRead = minutesRead,
            timestamp = LocalDateTime.now()
        )
        )

        return bookService.updateBook(updatedBook)
    }

    override fun completeReading(bookId: Int, rating: Int?): Book {
        val book = getBookOrThrow(bookId)
        val updatedBook = domainBookService.completeReading(book, rating)
        return bookService.updateBook(updatedBook)
    }

    override fun getBookStatistics(bookId: Int): BookOperationService.BookStatistics {
        val book = getBookOrThrow(bookId)
        val progress = readingProgressService.getBookProgress(bookId)
        val lendings = bookLendingService.getLendingHistory(bookId)

        val readingStats = domainBookService.calculateReadingStats(book, progress)
        val readingPace = domainBookService.calculateReadingPace(book, progress)

        val averageLendingDuration = calculateAverageLendingDuration(lendings)

        return BookOperationService.BookStatistics(
            readingStats = readingStats,
            readingPace = readingPace,
            lendingHistory = lendings,
            notes = bookNoteService.getNotesForBook(bookId),
            totalTimesLent = lendings.size,
            averageLendingDuration = averageLendingDuration
        )
    }

    override fun getBookWithDetails(bookId: Int): BookOperationService.BookWithDetails {
        val book = getBookOrThrow(bookId)
        val currentProgress = readingProgressService.getLatestProgress(bookId)
        val currentLending = bookLendingService.getCurrentLending(bookId)
        val notes = bookNoteService.getNotesForBook(bookId)

        val readingStats = if (book.isCurrentlyReading() || book.isRead()) {
            val progress = readingProgressService.getBookProgress(bookId)
            domainBookService.calculateReadingStats(book, progress)
        } else null

        return BookOperationService.BookWithDetails(
            book = book,
            currentProgress = currentProgress,
            currentLending = currentLending,
            notes = notes,
            readingStats = readingStats
        )
    }

    override fun getCurrentlyReadingBooks(): List<BookOperationService.BookWithProgress> {
        return bookService.getAllBooks()
            .filter { it.isCurrentlyReading() }
            .map { book ->
                val progress = readingProgressService.getBookProgress(book.id!!)
                val readingPace = domainBookService.calculateReadingPace(book, progress)

                BookOperationService.BookWithProgress(
                    book = book,
                    currentProgress = readingProgressService.getLatestProgress(book.id),
                    estimatedDaysToComplete = readingPace.estimatedDaysToComplete,
                    readingConsistency = readingPace.readingConsistency
                )
            }
    }

    override fun canBookBeLent(bookId: Int): Boolean {
        val book = getBookOrThrow(bookId)
        return domainBookService.canBeLent(book) &&
                bookLendingService.getCurrentLending(bookId) == null
    }

    private fun getBookOrThrow(bookId: Int): Book {
        return bookService.getBookById(bookId)
            ?: throw IllegalArgumentException("Book not found with id: $bookId")
    }

    private fun calculateAverageLendingDuration(lendings: List<BookLending>): Double? {
        val completedLendings = lendings.filter { it.actualReturnDate != null }
        if (completedLendings.isEmpty()) return null

        return completedLendings
            .map {
                ChronoUnit.DAYS.between(
                    it.lendingDate,
                    it.actualReturnDate
                ).toDouble()
            }
            .average()
    }
}