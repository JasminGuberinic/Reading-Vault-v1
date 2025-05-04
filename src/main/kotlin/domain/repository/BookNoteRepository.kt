package org.virtualcode.domain.repository

import org.virtualcode.domain.model.BookNote
import java.time.LocalDateTime

/**
 * Repository interface for BookNote entity.
 * Defines operations for accessing and manipulating book notes.
 */
interface BookNoteRepository {
    /**
     * Get all notes for a specific book
     */
    fun getNotesForBook(bookId: Int): List<BookNote>

    /**
     * Get a note by its ID
     */
    fun findById(id: Int): BookNote?

    /**
     * Add a new note
     */
    fun create(note: BookNote): BookNote

    /**
     * Update an existing note
     */
    fun update(note: BookNote): BookNote

    /**
     * Delete a note by its ID
     */
    fun delete(id: Int): Boolean

    /**
     * Find notes created between dates
     */
    fun findNotesBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<BookNote>

    /**
     * Find notes by chapter
     */
    fun findByChapter(bookId: Int, chapter: String): List<BookNote>

    /**
     * Find notes by page range
     */
    fun findByPageRange(bookId: Int, startPage: Int, endPage: Int): List<BookNote>
}