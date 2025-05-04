package org.virtualcode.api.resource

import org.virtualcode.api.dto.BookLendingDTO
import org.virtualcode.api.dto.toDTO
import org.virtualcode.service.BookLendingService
import com.google.inject.Inject
import com.google.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
import java.time.LocalDateTime

@Path("/books/{bookId}/lendings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
class BookLendingResource @Inject constructor(private val bookLendingService: BookLendingService) {

    /**
     * Lend a book
     */
    @POST
    fun lendBook(
        @PathParam("bookId") bookId: Int,
        @NotNull @Valid lendingDTO: BookLendingDTO
    ): Response {
        require(lendingDTO.bookId == bookId) { "Book ID mismatch" }
        val lending = bookLendingService.lendBook(lendingDTO.toDomain())
        val uri = UriBuilder.fromResource(BookLendingResource::class.java)
            .path(lending.id.toString())
            .build(bookId)
        return Response.created(uri)
            .entity(lending.toDTO())
            .build()
    }

    /**
     * Get current lending for a book
     */
    @GET
    @Path("/current")
    fun getCurrentLending(@PathParam("bookId") bookId: Int): Response {
        val lending = bookLendingService.getCurrentLending(bookId)
        return if (lending != null) {
            Response.ok(lending.toDTO()).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    /**
     * Get lending history for a book
     */
    @GET
    fun getLendingHistory(@PathParam("bookId") bookId: Int): Response {
        val lendings = bookLendingService.getLendingHistory(bookId)
        return Response.ok(lendings.map { it.toDTO() }).build()
    }

    /**
     * Return a book
     */
    @PUT
    @Path("/{id}/return")
    fun returnBook(
        @PathParam("bookId") bookId: Int,
        @PathParam("id") id: Int,
        @QueryParam("returnDate") returnDate: LocalDateTime?
    ): Response {
        val lending = bookLendingService.getLendingById(id)
        require(lending.bookId == bookId) { "Lending does not belong to specified book" }
        val updatedLending = bookLendingService.returnBook(id, returnDate ?: LocalDateTime.now())
        return Response.ok(updatedLending.toDTO()).build()
    }

    /**
     * Get lending statistics
     */
    @GET
    @Path("/stats")
    fun getLendingStats(@PathParam("bookId") bookId: Int): Response {
        val stats = bookLendingService.getLendingStats(bookId)
        return Response.ok(stats.toDTO()).build()
    }
}