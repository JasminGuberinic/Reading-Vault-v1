package org.virtualcode.api.resource

import org.virtualcode.api.dto.BookDTO
import org.virtualcode.api.dto.toDTO
import com.google.inject.Inject
import com.google.inject.Singleton
import org.virtualcode.service.BookService
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
import java.time.LocalDate

/**
 * REST resource for book operations.
 */
@Path("/books/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
class BookResourceAdmin @Inject constructor(private val bookService: BookService) {

    /**
     * Get all books
     */
    @GET
    fun getAllBooks(
        @QueryParam("status") status: String?,
        @QueryParam("condition") condition: String?,
    ): Response {
        val books = bookService.getAllBooks()
            .let { books ->
                when {
                    status != null -> books.filter { it.status.name == status.uppercase() }
                    condition != null -> books.filter { it.condition.name == condition.uppercase() }
                    else -> books
                }
            }
            .map { it.toDTO() }
        return Response.ok(books).build()
    }

    /**
     * Find books by author
     */
    @GET
    @Path("/search/author/{author}")
    fun findBooksByAuthor(@PathParam("author") author: String): Response {
        val books = bookService.findBooksByAuthor(author).map { it.toDTO() }
        return Response.ok(books).build()
    }

    /**
     * Get reading statistics
     */
    @GET
    @Path("/stats")
    fun getReadingStats(): Response {
        val stats = bookService.getReadingStats()
        return Response.ok(stats).build()
    }

    /**
     * Get books read in a specific year
     */
    @GET
    @Path("/year/{year}")
    fun getBooksReadInYear(@PathParam("year") year: Int): Response {
        require(year > 0) { "Year must be positive" }
        val books = bookService.getBooksReadInYear(year).map { it.toDTO() }
        return Response.ok(books).build()
    }

    /**
     * Get books by status
     */
    @GET
    @Path("/status/{status}")
    fun getBooksByStatus(@PathParam("status") status: String): Response {
        val books = bookService.getAllBooks()
            .filter { it.status.name == status.uppercase() }
            .map { it.toDTO() }
        return Response.ok(books).build()
    }

    /**
     * Get books by condition
     */
    @GET
    @Path("/condition/{condition}")
    fun getBooksByCondition(@PathParam("condition") condition: String): Response {
        val books = bookService.getAllBooks()
            .filter { it.condition.name == condition.uppercase() }
            .map { it.toDTO() }
        return Response.ok(books).build()
    }

    /**
     * Get books by location
     */
    @GET
    @Path("/location/{location}")
    fun getBooksByLocation(
        @PathParam("location") location: String,
        @QueryParam("exact") exact: Boolean = false
    ): Response {
        val books = bookService.getAllBooks()
            .filter { book ->
                if (exact) book.location == location
                else book.location?.contains(location, ignoreCase = true) == true
            }
            .map { it.toDTO() }
        return Response.ok(books).build()
    }

    /**
     * Get book by QR code
     */
    @GET
    @Path("/qr/{qrCode}")
    fun getBookByQrCode(@PathParam("qrCode") qrCode: String): Response {
        val book = bookService.getAllBooks()
            .firstOrNull { it.qrCode == qrCode }
        return if (book != null) {
            Response.ok(book.toDTO()).build()
        } else {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to "Book not found with QR code: $qrCode"))
                .build()
        }
    }

    /**
     * Get currently reading books
     */
    @GET
    @Path("/currently-reading")
    fun getCurrentlyReadingBooks(): Response {
        val books = bookService.getAllBooks()
            .filter { it.isCurrentlyReading() }
            .map { it.toDTO() }
        return Response.ok(books).build()
    }

    /**
     * Get completed books
     */
    @GET
    @Path("/completed")
    fun getCompletedBooks(
        @QueryParam("startDate") startDate: LocalDate?,
        @QueryParam("endDate") endDate: LocalDate?
    ): Response {
        var books = bookService.getAllBooks()
            .filter { it.isRead() }

        if (startDate != null && endDate != null) {
            books = books.filter { book ->
                book.dateRead?.toLocalDate()?.let { date ->
                    date.isAfter(startDate.minusDays(1)) &&
                            date.isBefore(endDate.plusDays(1))
                } ?: false
            }
        }

        return Response.ok(books.map { it.toDTO() }).build()
    }

    /**
     * Get reading progress for a book
     */
    @GET
    @Path("/{id}/progress")
    fun getBookProgress(@PathParam("id") id: Int): Response {
        val book = bookService.getBookById(id)
        return if (book != null) {
            Response.ok(mapOf(
                "currentPage" to book.currentPage,
                "totalPages" to book.totalPages,
                "progressPercentage" to book.readingProgress(),
                "isCompleted" to book.isRead(),
                "lastReadAt" to book.lastReadAt
            )).build()
        } else {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to "Book not found with id: $id"))
                .build()
        }
    }
}