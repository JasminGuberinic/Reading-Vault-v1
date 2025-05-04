package org.virtualcode.service

import org.virtualcode.domain.model.BookNote
import java.time.LocalDateTime

/**
 * Service interface for managing book notes.
 */
interface BookNoteService {
    /**
     * Create a new note for a book
     * @throws IllegalArgumentException if book doesn't exist
     */
    fun createNote(note: BookNote): BookNote

    /**
     * Update an existing note
     * @throws IllegalArgumentException if note or book doesn't exist
     */
    fun updateNote(note: BookNote): BookNote

    /**
     * Get all notes for a specific book
     * @throws IllegalArgumentException if book doesn't exist
     */
    fun getNotesForBook(bookId: Int): List<BookNote>

    /**
     * Get a specific note by ID
     * @throws IllegalArgumentException if note doesn't exist
     */
    fun getNoteById(id: Int): BookNote

    /**
     * Delete a note
     * @throws IllegalArgumentException if note doesn't exist
     */
    fun deleteNote(id: Int): Boolean

    /**
     * Get notes for a specific chapter in a book
     * @throws IllegalArgumentException if book doesn't exist
     */
    fun getNotesByChapter(bookId: Int, chapter: String): List<BookNote>

    /**
     * Get notes within a page range
     * @throws IllegalArgumentException if book doesn't exist or invalid page range
     */
    fun getNotesByPageRange(bookId: Int, startPage: Int, endPage: Int): List<BookNote>

    /**
     * Get notes created between specific dates
     */
    fun getNotesBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): List<BookNote>

    /**
     * Get notes statistics for a book
     */
    fun getNotesStats(bookId: Int): BookNotesStats
}