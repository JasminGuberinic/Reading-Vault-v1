package org.virtualcode.config.module

import com.google.inject.Inject
import com.google.inject.Singleton
import org.virtualcode.data.repository.*

@Singleton
class SchemaInitializer @Inject constructor(
    private val bookRepository: BookRepositoryImpl,
    private val bookNoteRepository: BookNoteRepositoryImpl,
    private val bookLendingRepository: BookLendingRepositoryImpl,
    private val readingProgressRepository: ReadingProgressRepositoryImpl
) {
    fun initialize() {
        bookRepository.createSchemaIfNotExists()
        bookNoteRepository.createSchemaIfNotExists()
        bookLendingRepository.createSchemaIfNotExists()
        readingProgressRepository.createSchemaIfNotExists()
    }
}
