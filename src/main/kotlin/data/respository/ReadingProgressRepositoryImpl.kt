package org.virtualcode.data.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.virtualcode.data.table.ReadingProgress as ReadingProgressTable
import org.virtualcode.domain.model.ReadingProgress
import org.virtualcode.domain.repository.ReadingProgressRepository
import java.time.LocalDateTime
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class ReadingProgressRepositoryImpl @Inject constructor(private val database: Database) : ReadingProgressRepository {

    /**
     * Initialize the database schema
     */
    fun createSchemaIfNotExists() {
        transaction(database) {
            SchemaUtils.create(ReadingProgressTable)
        }
    }

    override fun getProgressForBook(bookId: Int): List<ReadingProgress> = transaction(database) {
        ReadingProgressTable
            .selectAll()
            .where { ReadingProgressTable.bookId eq bookId }
            .orderBy(ReadingProgressTable.timestamp, SortOrder.DESC)
            .map { it.toProgress() }
    }

    override fun findById(id: Int): ReadingProgress? = transaction(database) {
        ReadingProgressTable
            .selectAll()
            .where { ReadingProgressTable.id eq id }
            .singleOrNull()
            ?.toProgress()
    }

    override fun create(progress: ReadingProgress): ReadingProgress = transaction(database) {
        val id = ReadingProgressTable.insert {
            it[bookId] = progress.bookId
            it[currentPage] = progress.currentPage
            it[timestamp] = progress.timestamp
            it[minutesRead] = progress.minutesRead
            it[notes] = progress.notes
        } get ReadingProgressTable.id

        progress.copy(id = id)
    }

    override fun update(progress: ReadingProgress): ReadingProgress = transaction(database) {
        ReadingProgressTable.update({ ReadingProgressTable.id eq progress.id!! }) {
            it[currentPage] = progress.currentPage
            it[timestamp] = progress.timestamp
            it[minutesRead] = progress.minutesRead
            it[notes] = progress.notes
        }
        progress
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        ReadingProgressTable.deleteWhere { ReadingProgressTable.id eq id } > 0
    }

    override fun getLatestProgress(bookId: Int): ReadingProgress? = transaction(database) {
        ReadingProgressTable
            .selectAll()
            .where { ReadingProgressTable.bookId eq bookId }
            .orderBy(ReadingProgressTable.timestamp, SortOrder.DESC)
            .limit(1)
            .singleOrNull()
            ?.toProgress()
    }

    override fun getProgressBetween(
        bookId: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ReadingProgress> = transaction(database) {
        ReadingProgressTable
            .selectAll()
            .where {
                (ReadingProgressTable.bookId eq bookId) and
                        (ReadingProgressTable.timestamp greaterEq startDate) and
                        (ReadingProgressTable.timestamp lessEq endDate)
            }
            .orderBy(ReadingProgressTable.timestamp, SortOrder.ASC)
            .map { it.toProgress() }
    }

    override fun getDailyProgress(bookId: Int): List<ReadingProgress> = transaction(database) {
        // Note: This implementation might need to be adjusted based on your specific database
        // as the date truncation syntax varies between databases
        ReadingProgressTable
            .slice(
                ReadingProgressTable.id,
                ReadingProgressTable.bookId,
                ReadingProgressTable.currentPage,
                ReadingProgressTable.timestamp,
                ReadingProgressTable.minutesRead,
                ReadingProgressTable.notes
            )
            .selectAll()
            .where { ReadingProgressTable.bookId eq bookId }
            .orderBy(ReadingProgressTable.timestamp, SortOrder.ASC)
            .map { it.toProgress() }
            .groupBy { it.timestamp.toLocalDate() }
            .map { (_, progressList) -> progressList.maxBy { it.timestamp } }
    }

    override fun getTotalMinutesRead(bookId: Int): Int = transaction(database) {
        ReadingProgressTable
            .slice(ReadingProgressTable.minutesRead.sum())
            .selectAll()
            .where {
                (ReadingProgressTable.bookId eq bookId) and
                        (ReadingProgressTable.minutesRead.isNotNull())
            }
            .singleOrNull()
            ?.get(ReadingProgressTable.minutesRead.sum()) ?: 0
    }

    override fun getAverageReadingSpeed(bookId: Int): Double? = transaction(database) {
        val progressRecords = getProgressForBook(bookId)
        if (progressRecords.size < 2) return@transaction null

        var totalPagesRead = 0
        var totalMinutes = 0

        for (i in 1 until progressRecords.size) {
            val current = progressRecords[i]
            val previous = progressRecords[i - 1]

            val pagesRead = current.currentPage - previous.currentPage
            val minutesRead = current.minutesRead ?: continue

            if (pagesRead > 0 && minutesRead > 0) {
                totalPagesRead += pagesRead
                totalMinutes += minutesRead
            }
        }

        if (totalMinutes > 0) totalPagesRead.toDouble() / totalMinutes else null
    }

    private fun ResultRow.toProgress(): ReadingProgress = ReadingProgress(
        id = this[ReadingProgressTable.id],
        bookId = this[ReadingProgressTable.bookId],
        currentPage = this[ReadingProgressTable.currentPage],
        timestamp = this[ReadingProgressTable.timestamp],
        minutesRead = this[ReadingProgressTable.minutesRead],
        notes = this[ReadingProgressTable.notes]
    )
}