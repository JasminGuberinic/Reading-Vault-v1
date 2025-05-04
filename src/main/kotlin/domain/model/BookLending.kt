package org.virtualcode.domain.model

import java.time.LocalDateTime

data class BookLending(
    val id: Int? = null,
    val bookId: Int,
    val borrowerName: String,
    val borrowerContact: String,
    val lendingDate: LocalDateTime,
    val expectedReturnDate: LocalDateTime,
    val actualReturnDate: LocalDateTime? = null,
    val notes: String? = null
) {
    fun isOverdue(): Boolean {
        return actualReturnDate == null && LocalDateTime.now() > expectedReturnDate
    }

    fun isActive(): Boolean {
        return actualReturnDate == null
    }
}