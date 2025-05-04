package org.virtualcode.api.dto

import org.virtualcode.domain.model.BookNote
import org.virtualcode.service.BookNotesStats
import java.time.LocalDateTime

data class BookNoteDTO(
    val id: Int? = null,
    val bookId: Int,
    val content: String,
    val page: Int? = null,
    val chapter: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    fun toDomain(): BookNote = BookNote(
        id = id,
        bookId = bookId,
        content = content,
        page = page,
        chapter = chapter,
        createdAt = createdAt ?: LocalDateTime.now(),
        updatedAt = updatedAt ?: LocalDateTime.now()
    )
}

fun BookNote.toDTO(): BookNoteDTO = BookNoteDTO(
    id = id,
    bookId = bookId,
    content = content,
    page = page,
    chapter = chapter,
    createdAt = createdAt,
    updatedAt = updatedAt
)

data class BookNoteStatsDTO(
    val totalNotes: Int,
    val notesWithPages: Int,
    val notesWithChapters: Int,
    val chaptersWithNotes: Int,
    val averageNotesLength: Double?
)

fun BookNotesStats.toDTO(): BookNoteStatsDTO = BookNoteStatsDTO(
    totalNotes = totalNotes,
    notesWithPages = notesWithPages,
    notesWithChapters = notesWithChapters,
    chaptersWithNotes = chaptersWithNotes,
    averageNotesLength = averageNotesLength
)