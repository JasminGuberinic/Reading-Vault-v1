package org.virtualcode.api.resource

import org.virtualcode.service.BookOperationService
import com.google.inject.Inject
import com.google.inject.Singleton
import org.virtualcode.api.dto.*
import org.virtualcode.domain.service.DomainBookService
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/books/operations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
class BookOperationResource @Inject constructor(
    private val bookOperationService: BookOperationService
) {

    /**
     * Start reading a book
     */
    @POST
    @Path("/{bookId}/start")
    fun startReading(
        @PathParam("bookId") bookId: Int
    ): Response {
        return try {
            val updatedBook = bookOperationService.startReading(bookId)
            Response.ok(updatedBook.toDTO()).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    /**
     * Update reading progress
     */
    @POST
    @Path("/{bookId}/progress")
    fun updateReadingProgress(
        @PathParam("bookId") bookId: Int,
        @QueryParam("currentPage") currentPage: Int,
        @QueryParam("minutesRead") minutesRead: Int?
    ): Response {
        return try {
            val updatedBook = bookOperationService.updateReadingProgress(
                bookId = bookId,
                currentPage = currentPage,
                minutesRead = minutesRead
            )
            Response.ok(updatedBook.toDTO()).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    /**
     * Complete reading a book
     */
    @POST
    @Path("/{bookId}/complete")
    fun completeReading(
        @PathParam("bookId") bookId: Int,
        @QueryParam("rating") @Min(1) @Max(5) rating: Int?
    ): Response {
        return try {
            val updatedBook = bookOperationService.completeReading(bookId, rating)
            Response.ok(updatedBook.toDTO()).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    /**
     * Get comprehensive book statistics
     */
    @GET
    @Path("/{bookId}/statistics")
    fun getBookStatistics(
        @PathParam("bookId") bookId: Int
    ): Response {
        return try {
            val statistics = bookOperationService.getBookStatistics(bookId)
            Response.ok(BookStatisticsDTO.fromDomain(statistics)).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    /**
     * Get book with all related information
     */
    @GET
    @Path("/{bookId}/details")
    fun getBookWithDetails(
        @PathParam("bookId") bookId: Int
    ): Response {
        return try {
            val details = bookOperationService.getBookWithDetails(bookId)
            Response.ok(BookWithDetailsDTO.fromDomain(details)).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    /**
     * Get currently reading books with progress
     */
    @GET
    @Path("/currently-reading")
    fun getCurrentlyReadingBooks(): Response {
        val books = bookOperationService.getCurrentlyReadingBooks()
        return Response.ok(books.map { BookWithProgressDTO.fromDomain(it) }).build()
    }

    /**
     * Check if book can be lent
     */
    @GET
    @Path("/{bookId}/can-be-lent")
    fun canBookBeLent(
        @PathParam("bookId") bookId: Int
    ): Response {
        return try {
            val canBeLent = bookOperationService.canBookBeLent(bookId)
            Response.ok(mapOf("canBeLent" to canBeLent)).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }
}

// DTO klase za response
data class BookStatisticsDTO(
    val readingStats: ReadingStatsDTO,
    val readingPace: ReadingPaceDTO,
    val lendingHistory: List<BookLendingDTO>,
    val notes: List<BookNoteDTO>,
    val totalTimesLent: Int,
    val averageLendingDuration: Double?
) {
    companion object {
        fun fromDomain(stats: BookOperationService.BookStatistics) = BookStatisticsDTO(
            readingStats = ReadingStatsDTO.fromDomain(stats.readingStats),
            readingPace = ReadingPaceDTO.fromDomain(stats.readingPace),
            lendingHistory = stats.lendingHistory.map { it.toDTO() },
            notes = stats.notes.map { it.toDTO() },
            totalTimesLent = stats.totalTimesLent,
            averageLendingDuration = stats.averageLendingDuration
        )
    }
}

data class BookWithDetailsDTO(
    val book: BookDTO,
    val currentProgress: ReadingProgressDTO?,
    val currentLending: BookLendingDTO?,
    val notes: List<BookNoteDTO>,
    val readingStats: ReadingStatsDTO?
) {
    companion object {
        fun fromDomain(details: BookOperationService.BookWithDetails) = BookWithDetailsDTO(
            book = details.book.toDTO(),
            currentProgress = details.currentProgress?.toDTO(),
            currentLending = details.currentLending?.toDTO(),
            notes = details.notes.map { it.toDTO() },
            readingStats = details.readingStats?.let { ReadingStatsDTO.fromDomain(it) }
        )
    }
}

data class BookWithProgressDTO(
    val book: BookDTO,
    val currentProgress: ReadingProgressDTO?,
    val estimatedDaysToComplete: Int?,
    val readingConsistency: String
) {
    companion object {
        fun fromDomain(progress: BookOperationService.BookWithProgress) = BookWithProgressDTO(
            book = progress.book.toDTO(),
            currentProgress = progress.currentProgress?.toDTO(),
            estimatedDaysToComplete = progress.estimatedDaysToComplete,
            readingConsistency = progress.readingConsistency.name
        )
    }
}

data class ReadingStatsDTO(
    val percentageComplete: Double,
    val averageMinutesPerPage: Double?,
    val estimatedTimeToComplete: Int?,
    val daysReading: Int,
    val currentStreak: Int
) {
    companion object {
        fun fromDomain(stats: DomainBookService.BookReadingStats) = ReadingStatsDTO(
            percentageComplete = stats.percentageComplete,
            averageMinutesPerPage = stats.averageMinutesPerPage,
            estimatedTimeToComplete = stats.estimatedTimeToComplete,
            daysReading = stats.daysReading,
            currentStreak = stats.currentStreak
        )
    }
}

data class ReadingPaceDTO(
    val pagesPerHour: Double?,
    val estimatedDaysToComplete: Int?,
    val readingConsistency: String
) {
    companion object {
        fun fromDomain(pace: DomainBookService.ReadingPace) = ReadingPaceDTO(
            pagesPerHour = pace.pagesPerHour,
            estimatedDaysToComplete = pace.estimatedDaysToComplete,
            readingConsistency = pace.readingConsistency.name
        )
    }
}