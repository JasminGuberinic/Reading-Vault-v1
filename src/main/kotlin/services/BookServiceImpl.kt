package org.virtualcode.service

import org.virtualcode.domain.model.Book
import org.virtualcode.domain.repository.BookRepository
import java.time.LocalDate
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * Service for book-related business logic.
 */
@Singleton
class  BookServiceImpl @Inject constructor(private val bookRepository: BookRepository) : BookService {

    override fun getAllBooks(): List<Book> {
        return bookRepository.getAllBooks()
    }

    /**
     * Get all books
     */
    override fun getAllBooks(userId: Int): List<Book> {
        return bookRepository.getAllBooks(userId)
    }

    /**
     * Get a book by ID
     */
    override fun getBookById(id: Int): Book? {
        return bookRepository.findById(id)
    }

    override fun getBookById(id: Int, userId: Int): Book? {
        return bookRepository.findById(id, userId)
    }

    /**
     * Add a new book
     */
    override fun addBook(book: Book): Book {
        require(book.isValid()) { "Invalid book data" }
        return bookRepository.create(book)
    }

    /**
     * Update an existing book
     */
    override fun updateBook(book: Book): Book {
        require(book.id != null) { "Book ID cannot be null for update" }
        require(book.isValid()) { "Invalid book data" }
        return bookRepository.update(book)
    }

    /**
     * Delete a book
     */
    override fun deleteBook(id: Int): Boolean {
        return bookRepository.delete(id)
    }

    /**
     * Find books by author
     */
    override fun findBooksByAuthor(author: String): List<Book> {
        return bookRepository.findBooksByAuthor(author)
    }

    /**
     * Find books by author
     */
    override fun findBooksByAuthor(author: String, userId: Int): List<Book> {
        return bookRepository.findBooksByAuthor(author, userId)
    }

    /**
     * Get books read in a specific year
     */
    override fun getBooksReadInYear(year: Int): List<Book> {
        val startDate = LocalDate.of(year, 1, 1)
        val endDate = LocalDate.of(year, 12, 31)
        return bookRepository.findBooksReadBetween(startDate, endDate)
    }

    /**
     * Get books read in a specific year
     */
    override fun getBooksReadInYear(year: Int, userId: Int): List<Book> {
        val startDate = LocalDate.of(year, 1, 1)
        val endDate = LocalDate.of(year, 12, 31)
        return bookRepository.findBooksReadBetween(startDate, endDate, userId)
    }

    /**
     * Get reading statistics
     */
    override fun getReadingStats(): BookService.ReadingStats {
        val allBooks = bookRepository.getAllBooks()
        val readBooks = allBooks.filter { it.isRead() }

        return BookService.ReadingStats(
            totalBooks = allBooks.size,
            readBooks = readBooks.size,
            averageRating = readBooks.mapNotNull { it.rating }.average().takeIf { !it.isNaN() },
            booksByYear = readBooks.groupBy { it.readingYear() }
                .mapValues { it.value.size }
                .filterKeys { it != null }
                .mapKeys { it.key!! }
        )
    }

    /**
     * Get reading statistics
     */
    override fun getReadingStats(userId: Int): BookService.ReadingStats {
        val allBooks = bookRepository.getAllBooks(userId)
        val readBooks = allBooks.filter { it.isRead() }

        return BookService.ReadingStats(
            totalBooks = allBooks.size,
            readBooks = readBooks.size,
            averageRating = readBooks.mapNotNull { it.rating }.average().takeIf { !it.isNaN() },
            booksByYear = readBooks.groupBy { it.readingYear() }
                .mapValues { it.value.size }
                .filterKeys { it != null }
                .mapKeys { it.key!! }
        )
    }
}