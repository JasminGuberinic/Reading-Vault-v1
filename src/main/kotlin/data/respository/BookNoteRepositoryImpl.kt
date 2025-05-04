package org.virtualcode.data.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.virtualcode.data.table.BookNotes as BookNotesTable
import org.virtualcode.domain.model.BookNote
import org.virtualcode.domain.repository.BookNoteRepository
import java.time.LocalDateTime
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class BookNoteRepositoryImpl @Inject constructor(private val database: Database) : BookNoteRepository {

    /**
     * Initialize the database schema
     */
    fun createSchemaIfNotExists() {
        transaction(database) {
            SchemaUtils.create(BookNotesTable)
        }
    }

    override fun getNotesForBook(bookId: Int): List<BookNote> = transaction(database) {
        BookNotesTable
            .selectAll()
            .where { BookNotesTable.bookId eq bookId }
            .orderBy(BookNotesTable.createdAt, SortOrder.DESC)
            .map { it.toNote() }
    }

    override fun findById(id: Int): BookNote? = transaction(database) {
        BookNotesTable
            .selectAll()
            .where { BookNotesTable.id eq id }
            .singleOrNull()
            ?.toNote()
    }

    override fun create(note: BookNote): BookNote = transaction(database) {
        val now = LocalDateTime.now()
        val id = BookNotesTable.insert {
            it[bookId] = note.bookId
            it[content] = note.content
            it[page] = note.page
            it[chapter] = note.chapter
            it[createdAt] = now
            it[updatedAt] = now
        } get BookNotesTable.id

        note.copy(
            id = id,
            createdAt = now,
            updatedAt = now
        )
    }

    override fun update(note: BookNote): BookNote = transaction(database) {
        val now = LocalDateTime.now()
        BookNotesTable.update({ BookNotesTable.id eq note.id!! }) {
            it[content] = note.content
            it[page] = note.page
            it[chapter] = note.chapter
            it[updatedAt] = now
        }
        note.copy(updatedAt = now)
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        BookNotesTable.deleteWhere { BookNotesTable.id eq id } > 0
    }

    override fun findNotesBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<BookNote> = transaction(database) {
        BookNotesTable
            .selectAll()
            .where {
                (BookNotesTable.createdAt greaterEq startDate) and
                        (BookNotesTable.createdAt lessEq endDate)
            }
            .orderBy(BookNotesTable.createdAt, SortOrder.DESC)
            .map { it.toNote() }
    }

    override fun findByChapter(bookId: Int, chapter: String): List<BookNote> = transaction(database) {
        BookNotesTable
            .selectAll()
            .where {
                (BookNotesTable.bookId eq bookId) and
                        (BookNotesTable.chapter eq chapter)
            }
            .orderBy(BookNotesTable.page, SortOrder.ASC)
            .map { it.toNote() }
    }

    override fun findByPageRange(
        bookId: Int,
        startPage: Int,
        endPage: Int
    ): List<BookNote> = transaction(database) {
        BookNotesTable
            .selectAll()
            .where {
                (BookNotesTable.bookId eq bookId) and
                        (BookNotesTable.page greaterEq startPage) and
                        (BookNotesTable.page lessEq endPage)
            }
            .orderBy(BookNotesTable.page, SortOrder.ASC)
            .map { it.toNote() }
    }

    private fun ResultRow.toNote(): BookNote = BookNote(
        id = this[BookNotesTable.id],
        bookId = this[BookNotesTable.bookId],
        content = this[BookNotesTable.content],
        page = this[BookNotesTable.page],
        chapter = this[BookNotesTable.chapter],
        createdAt = this[BookNotesTable.createdAt],
        updatedAt = this[BookNotesTable.updatedAt]
    )
}