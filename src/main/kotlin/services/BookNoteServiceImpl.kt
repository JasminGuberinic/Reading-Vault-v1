package org.virtualcode.service

import org.virtualcode.domain.model.BookNote
import org.virtualcode.domain.repository.BookNoteRepository
import org.virtualcode.domain.repository.BookRepository
import java.time.LocalDateTime
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class BookNoteServiceImpl @Inject constructor(
    private val bookNoteRepository: BookNoteRepository,
    private val bookRepository: BookRepository
) : BookNoteService {
    /**
     * Create a new note for a book
     * @throws IllegalArgumentException if book doesn't exist
     */
    override fun createNote(note: BookNote): BookNote {
        validateBook(note.bookId)
        validatePageNumber(note)
        return bookNoteRepository.create(note)
    }

    /**
     * Update an existing note
     * @throws IllegalArgumentException if note or book doesn't exist
     */
    override fun updateNote(note: BookNote): BookNote {
        validateBook(note.bookId)
        validateNoteExists(note.id!!)
        validatePageNumber(note)
        return bookNoteRepository.update(note)
    }

    /**
     * Get all notes for a specific book
     * @throws IllegalArgumentException if book doesn't exist
     */
    override fun getNotesForBook(bookId: Int): List<BookNote> {
        validateBook(bookId)
        return bookNoteRepository.getNotesForBook(bookId)
    }

    /**
     * Get a specific note by ID
     * @throws IllegalArgumentException if note doesn't exist
     */
    override fun getNoteById(id: Int): BookNote {
        return bookNoteRepository.findById(id)
            ?: throw IllegalArgumentException("Note not found with id: $id")
    }

    /**
     * Delete a note
     * @throws IllegalArgumentException if note doesn't exist
     */
    override fun deleteNote(id: Int): Boolean {
        validateNoteExists(id)
        return bookNoteRepository.delete(id)
    }

    /**
     * Get notes for a specific chapter in a book
     * @throws IllegalArgumentException if book doesn't exist
     */
    override fun getNotesByChapter(bookId: Int, chapter: String): List<BookNote> {
        validateBook(bookId)
        return bookNoteRepository.findByChapter(bookId, chapter)
    }

    /**
     * Get notes within a page range
     * @throws IllegalArgumentException if book doesn't exist or invalid page range
     */
    override fun getNotesByPageRange(bookId: Int, startPage: Int, endPage: Int): List<BookNote> {
        validateBook(bookId)
        require(startPage <= endPage) { "Start page must be less than or equal to end page" }
        validatePageRange(bookId, startPage, endPage)
        return bookNoteRepository.findByPageRange(bookId, startPage, endPage)
    }

    /**
     * Get notes created between specific dates
     */
    override fun getNotesBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): List<BookNote> {
        require(!startDate.isAfter(endDate)) { "Start date must be before or equal to end date" }
        return bookNoteRepository.findNotesBetween(startDate, endDate)
    }

    /**
     * Get notes statistics for a book
     */
    override fun getNotesStats(bookId: Int): BookNotesStats {
        validateBook(bookId)
        val notes = bookNoteRepository.getNotesForBook(bookId)
        return BookNotesStats(
            totalNotes = notes.size,
            notesWithPages = notes.count { it.page != null },
            notesWithChapters = notes.count { it.chapter != null },
            chaptersWithNotes = notes.mapNotNull { it.chapter }.distinct().size,
            averageNotesLength = notes.map { it.content.length }.average().takeIf { notes.isNotEmpty() }
        )
    }

    private fun validateBook(bookId: Int) {
        if (bookRepository.findById(bookId) == null) {
            throw IllegalArgumentException("Book not found with id: $bookId")
        }
    }

    private fun validateNoteExists(noteId: Int) {
        if (bookNoteRepository.findById(noteId) == null) {
            throw IllegalArgumentException("Note not found with id: $noteId")
        }
    }

    private fun validatePageNumber(note: BookNote) {
        note.page?.let { page ->
            val book = bookRepository.findById(note.bookId)!!
            if (book.totalPages != null && page > book.totalPages) {
                throw IllegalArgumentException("Page number cannot be greater than total pages")
            }
        }
    }

    private fun validatePageRange(bookId: Int, startPage: Int, endPage: Int) {
        val book = bookRepository.findById(bookId)!!
        if (book.totalPages != null) {
            require(startPage > 0) { "Start page must be positive" }
            require(endPage <= book.totalPages) { "End page cannot be greater than total pages" }
        }
    }
}

/**
 * Data class for book notes statistics
 */
data class BookNotesStats(
    val totalNotes: Int,
    val notesWithPages: Int,
    val notesWithChapters: Int,
    val chaptersWithNotes: Int,
    val averageNotesLength: Double?
)