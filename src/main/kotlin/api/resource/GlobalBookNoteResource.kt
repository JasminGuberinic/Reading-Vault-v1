package org.virtualcode.api.resource

import org.virtualcode.api.dto.toDTO
import org.virtualcode.service.BookNoteService
import com.google.inject.Inject
import com.google.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.time.LocalDateTime

@Path("/notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
class GlobalBookNoteResource @Inject constructor(private val bookNoteService: BookNoteService) {

    /**
     * Get notes between dates
     */
    @GET
    @Path("/between")
    fun getNotesBetweenDates(
        @QueryParam("startDate") startDate: LocalDateTime,
        @QueryParam("endDate") endDate: LocalDateTime
    ): Response {
        val notes = bookNoteService.getNotesBetweenDates(startDate, endDate)
        return Response.ok(notes.map { it.toDTO() }).build()
    }
}