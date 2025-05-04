package org.virtualcode.api.resource

import org.virtualcode.api.dto.toDTO
import org.virtualcode.service.BookLendingService
import com.google.inject.Inject
import com.google.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/lendings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
class GlobalBookLendingResource @Inject constructor(private val bookLendingService: BookLendingService) {

    /**
     * Get all overdue lendings
     */
    @GET
    @Path("/overdue")
    fun getOverdueLendings(): Response {
        val lendings = bookLendingService.getOverdueLendings()
        return Response.ok(lendings.map { it.toDTO() }).build()
    }

    /**
     * Check if book is currently lent
     */
    @GET
    @Path("/status/{bookId}")
    fun isBookLent(@PathParam("bookId") bookId: Int): Response {
        val isLent = bookLendingService.isBookLent(bookId)
        return Response.ok(mapOf("isLent" to isLent)).build()
    }
}