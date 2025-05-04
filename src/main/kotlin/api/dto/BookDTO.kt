package org.virtualcode.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import org.virtualcode.domain.enums.BookCondition
import org.virtualcode.domain.enums.BookStatus
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data Transfer Object for Book entity.
 */
data class BookDTO(
    @JsonProperty("id")
    val id: Int? = null,

    @JsonProperty("userId")
    val userId: Int,

    @JsonProperty("title")
    val title: String,

    @JsonProperty("author")
    val author: String,

    @JsonProperty("isbn")
    val isbn: String? = null,

    @JsonProperty("yearPublished")
    val yearPublished: Int? = null,

    @JsonProperty("totalPages")
    val totalPages: Int? = null,

    @JsonProperty("status")
    val status: BookStatus = BookStatus.NOT_STARTED,

    @JsonProperty("condition")
    val condition: BookCondition = BookCondition.GOOD,

    @JsonProperty("location")
    val location: String? = null,

    @JsonProperty("qrCode")
    val qrCode: String? = null,

    @JsonProperty("dateAcquired")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val dateAcquired: LocalDate? = null,

    @JsonProperty("startedReading")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startedReading: LocalDateTime? = null,

    @JsonProperty("dateRead")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val dateRead: LocalDateTime? = null,

    @JsonProperty("rating")
    val rating: Int? = null,

    @JsonProperty("currentPage")
    val currentPage: Int = 0,

    @JsonProperty("lastReadAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastReadAt: LocalDateTime? = null
) {
    /**
     * Extension function to convert DTO to domain model
     */
    fun toDomain(): org.virtualcode.domain.model.Book {
        return org.virtualcode.domain.model.Book(
            id = this.id,
            userId = this.userId,
            title = this.title,
            author = this.author,
            isbn = this.isbn,
            yearPublished = this.yearPublished,
            totalPages = this.totalPages,
            status = this.status,
            condition = this.condition,
            location = this.location,
            qrCode = this.qrCode,
            dateAcquired = this.dateAcquired,
            startedReading = this.startedReading,
            dateRead = this.dateRead,
            rating = this.rating,
            currentPage = this.currentPage,
            lastReadAt = this.lastReadAt
        )
    }
}

/**
 * Extension function to convert domain model to DTO
 */
fun org.virtualcode.domain.model.Book.toDTO(): BookDTO {
    return BookDTO(
        id = this.id,
        title = this.title,
        author = this.author,
        isbn = this.isbn,
        yearPublished = this.yearPublished,
        totalPages = this.totalPages,
        status = this.status,
        condition = this.condition,
        location = this.location,
        qrCode = this.qrCode,
        dateAcquired = this.dateAcquired,
        startedReading = this.startedReading,
        dateRead = this.dateRead,
        rating = this.rating,
        currentPage = this.currentPage,
        lastReadAt = this.lastReadAt,
        userId = this.userId
    )
}