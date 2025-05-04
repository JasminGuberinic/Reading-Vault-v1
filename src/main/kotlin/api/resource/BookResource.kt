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
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
class BookResource @Inject constructor(private val bookService: BookService) {

    /**
     * Get all books
     */
    @GET
    fun getAllBooks(
        @QueryParam("status") status: String?,
        @QueryParam("condition") condition: String?,
        @QueryParam("userId") userId: Int
    ): Response {
        val books = bookService.getAllBooks(userId)
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
     * Get a book by ID
     */
    @GET
    @Path("/{id}")
    fun getBook(@PathParam("id") id: Int, @QueryParam("userId") userId: Int): Response {
        val book = bookService.getBookById(id, userId)
        return if (book != null) {
            Response.ok(book.toDTO()).build()
        } else {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to "Book not found with id: $id"))
                .build()
        }
    }

    /**
     * Add a new book
     */
    @POST
    fun addBook(@NotNull @Valid bookDTO: BookDTO): Response {
        return try {
            val savedBook = bookService.addBook(bookDTO.toDomain())
            val uri = UriBuilder.fromResource(BookResource::class.java)
                .path(savedBook.id.toString())
                .build()
            Response.created(uri)
                .entity(savedBook.toDTO())
                .build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    /**
     * Update an existing book
     */
    @PUT
    @Path("/{id}")
    fun updateBook(
        @PathParam("id") id: Int,
        @NotNull @Valid bookDTO: BookDTO
    ): Response {
        if (bookDTO.id != id) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Book ID in path must match book ID in body"))
                .build()
        }

        return try {
            val updatedBook = bookService.updateBook(bookDTO.toDomain())
            Response.ok(updatedBook.toDTO()).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    /**
     * Delete a book
     */
    @DELETE
    @Path("/{id}")
    fun deleteBook(@PathParam("id") id: Int): Response {
        val deleted = bookService.deleteBook(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to "Book not found with id: $id"))
                .build()
        }
    }

    /**
     * Find books by author
     */
    @GET
    @Path("/search/author/{author}")
    fun findBooksByAuthor(@PathParam("author") author: String, @QueryParam("userId") userId: Int): Response {
        val books = bookService.findBooksByAuthor(author, userId).map { it.toDTO() }
        return Response.ok(books).build()
    }

    /**
     * Get reading statistics
     */
    @GET
    @Path("/stats")
    fun getReadingStats(@QueryParam("userId") userId: Int): Response {
        val stats = bookService.getReadingStats(userId)
        return Response.ok(stats).build()
    }

    /**
     * Get books read in a specific year
     */
    @GET
    @Path("/year/{year}")
    fun getBooksReadInYear(@PathParam("year") year: Int, @QueryParam("userId") userId: Int): Response {
        require(year > 0) { "Year must be positive" }
        val books = bookService.getBooksReadInYear(year, userId).map { it.toDTO() }
        return Response.ok(books).build()
    }

    /**
     * Get books by status
     */
    @GET
    @Path("/status/{status}")
    fun getBooksByStatus(@PathParam("status") status: String, @QueryParam("userId") userId: Int): Response {
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
    fun getBooksByCondition(@PathParam("condition") condition: String, @QueryParam("userId") userId: Int): Response {
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
        @QueryParam("exact") exact: Boolean = false,
        @QueryParam("userId") userId: Int
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
    fun getBookByQrCode(@PathParam("qrCode") qrCode: String, @QueryParam("userId") userId: Int): Response {
        val book = bookService.getAllBooks(userId)
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
    fun getCurrentlyReadingBooks(@QueryParam("userId") userId: Int): Response {
        val books = bookService.getAllBooks(userId)
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
        @QueryParam("endDate") endDate: LocalDate?,
        @QueryParam("userId") userId: Int
    ): Response {
        var books = bookService.getAllBooks(userId)
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
    fun getBookProgress(@PathParam("id") id: Int, @QueryParam("userId") userId: Int): Response {
        val book = bookService.getBookById(id, userId)
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