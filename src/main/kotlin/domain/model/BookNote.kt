package org.virtualcode.domain.model

import java.time.LocalDateTime

data class BookNote(
    val id: Int? = null,
    val bookId: Int,
    val content: String,
    val page: Int? = null,
    val chapter: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)