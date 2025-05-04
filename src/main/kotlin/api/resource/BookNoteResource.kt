package org.virtualcode.api.resource

import org.virtualcode.api.dto.BookNoteDTO
import org.virtualcode.api.dto.toDTO
import org.virtualcode.service.BookNoteService
import com.google.inject.Inject
import com.google.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
import java.time.LocalDateTime

@Path("/books/{bookId}/notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
class BookNoteResource @Inject constructor(private val bookNoteService: BookNoteService) {

    /**
     * Get all notes for a book
     */
    @GET
    fun getNotesForBook(@PathParam("bookId") bookId: Int): Response {
        val notes = bookNoteService.getNotesForBook(bookId)
        return Response.ok(notes.map { it.toDTO() }).build()
    }

    /**
     * Create a new note
     */
    @POST
    fun createNote(
        @PathParam("bookId") bookId: Int,
        @NotNull @Valid noteDTO: BookNoteDTO
    ): Response {
        require(noteDTO.bookId == bookId) { "Book ID mismatch" }
        val note = bookNoteService.createNote(noteDTO.toDomain())
        val uri = UriBuilder.fromResource(BookNoteResource::class.java)
            .path(note.id.toString())
            .build(bookId)
        return Response.created(uri)
            .entity(note.toDTO())
            .build()
    }

    /**
     * Get a specific note
     */
    @GET
    @Path("/{id}")
    fun getNoteById(
        @PathParam("bookId") bookId: Int,
        @PathParam("id") id: Int
    ): Response {
        val note = bookNoteService.getNoteById(id)
        require(note.bookId == bookId) { "Note does not belong to specified book" }
        return Response.ok(note.toDTO()).build()
    }

    /**
     * Update a note
     */
    @PUT
    @Path("/{id}")
    fun updateNote(
        @PathParam("bookId") bookId: Int,
        @PathParam("id") id: Int,
        @NotNull @Valid noteDTO: BookNoteDTO
    ): Response {
        require(noteDTO.id == id) { "Note ID mismatch" }
        require(noteDTO.bookId == bookId) { "Book ID mismatch" }
        val note = bookNoteService.updateNote(noteDTO.toDomain())
        return Response.ok(note.toDTO()).build()
    }

    /**
     * Delete a note
     */
    @DELETE
    @Path("/{id}")
    fun deleteNote(
        @PathParam("bookId") bookId: Int,
        @PathParam("id") id: Int
    ): Response {
        val note = bookNoteService.getNoteById(id)
        require(note.bookId == bookId) { "Note does not belong to specified book" }
        bookNoteService.deleteNote(id)
        return Response.noContent().build()
    }

    /**
     * Get notes by chapter
     */
    @GET
    @Path("/chapter/{chapter}")
    fun getNotesByChapter(
        @PathParam("bookId") bookId: Int,
        @PathParam("chapter") chapter: String
    ): Response {
        val notes = bookNoteService.getNotesByChapter(bookId, chapter)
        return Response.ok(notes.map { it.toDTO() }).build()
    }

    /**
     * Get notes by page range
     */
    @GET
    @Path("/pages")
    fun getNotesByPageRange(
        @PathParam("bookId") bookId: Int,
        @QueryParam("startPage") startPage: Int,
        @QueryParam("endPage") endPage: Int
    ): Response {
        val notes = bookNoteService.getNotesByPageRange(bookId, startPage, endPage)
        return Response.ok(notes.map { it.toDTO() }).build()
    }

    /**
     * Get notes statistics
     */
    @GET
    @Path("/stats")
    fun getNotesStats(@PathParam("bookId") bookId: Int): Response {
        val stats = bookNoteService.getNotesStats(bookId)
        return Response.ok(stats.toDTO()).build()
    }
}