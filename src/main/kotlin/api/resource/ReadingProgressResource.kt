package org.virtualcode.api.resource

import org.virtualcode.api.dto.ReadingProgressDTO
import org.virtualcode.api.dto.toDTO
import org.virtualcode.service.ReadingProgressService
import com.google.inject.Inject
import com.google.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
import java.time.LocalDateTime

@Path("/books/{bookId}/reading-progress")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
class ReadingProgressResource @Inject constructor(private val readingProgressService: ReadingProgressService) {

    /**
     * Record new reading progress
     */
    @POST
    fun recordProgress(
        @PathParam("bookId") bookId: Int,
        @NotNull @Valid progressDTO: ReadingProgressDTO
    ): Response {
        require(progressDTO.bookId == bookId) { "Book ID mismatch" }
        val progress = readingProgressService.recordProgress(progressDTO.toDomain())
        val uri = UriBuilder.fromResource(ReadingProgressResource::class.java)
            .path(progress.id.toString())
            .build(bookId)
        return Response.created(uri)
            .entity(progress.toDTO())
            .build()
    }

    /**
     * Get all reading progress for a book
     */
    @GET
    fun getBookProgress(@PathParam("bookId") bookId: Int): Response {
        val progress = readingProgressService.getBookProgress(bookId)
        return Response.ok(progress.map { it.toDTO() }).build()
    }

    /**
     * Get latest reading progress
     */
    @GET
    @Path("/latest")
    fun getLatestProgress(@PathParam("bookId") bookId: Int): Response {
        val progress = readingProgressService.getLatestProgress(bookId)
        return if (progress != null) {
            Response.ok(progress.toDTO()).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    /**
     * Get reading progress between dates
     */
    @GET
    @Path("/between")
    fun getProgressBetweenDates(
        @PathParam("bookId") bookId: Int,
        @QueryParam("startDate") startDate: LocalDateTime,
        @QueryParam("endDate") endDate: LocalDateTime
    ): Response {
        val progress = readingProgressService.getProgressBetweenDates(bookId, startDate, endDate)
        return Response.ok(progress.map { it.toDTO() }).build()
    }

    /**
     * Get daily reading progress
     */
    @GET
    @Path("/daily")
    fun getDailyProgress(@PathParam("bookId") bookId: Int): Response {
        val progress = readingProgressService.getDailyProgress(bookId)
        return Response.ok(progress.map { it.toDTO() }).build()
    }

    /**
     * Get reading statistics
     */
    @GET
    @Path("/stats")
    fun getReadingStats(@PathParam("bookId") bookId: Int): Response {
        val stats = readingProgressService.getReadingStats(bookId)
        return Response.ok(stats.toDTO()).build()
    }

    /**
     * Get reading streak information
     */
    @GET
    @Path("/streak")
    fun getReadingStreak(@PathParam("bookId") bookId: Int): Response {
        val streak = readingProgressService.getReadingStreak(bookId)
        return Response.ok(streak.toDTO()).build()
    }
}