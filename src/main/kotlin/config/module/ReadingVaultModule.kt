package org.virtualcode.config.module

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.virtualcode.api.resource.*
import org.virtualcode.auth.JwtAuthenticator
import org.virtualcode.auth.RoleAuthorizer
import org.virtualcode.config.ReadingVaultConfiguration
import org.virtualcode.data.repository.*
import org.virtualcode.domain.repository.*
import org.virtualcode.domain.service.DomainBookService
import org.virtualcode.domain.service.DomainBookServiceImpl
import org.virtualcode.service.*

/**
 * Guice module for dependency injection in the ReadingVault application.
 */
class ReadingVaultModule(private val configuration: ReadingVaultConfiguration) : AbstractModule() {

    override fun configure() {
        // Bind repositories
        bind(BookRepository::class.java).to(BookRepositoryImpl::class.java).asEagerSingleton()
        bind(BookNoteRepository::class.java).to(BookNoteRepositoryImpl::class.java).asEagerSingleton()
        bind(BookLendingRepository::class.java).to(BookLendingRepositoryImpl::class.java).asEagerSingleton()
        bind(ReadingProgressRepository::class.java).to(ReadingProgressRepositoryImpl::class.java).asEagerSingleton()

        // Bind services
        bind(BookService::class.java).to(BookServiceImpl::class.java).asEagerSingleton()
        bind(BookNoteService::class.java).to(BookNoteServiceImpl::class.java).asEagerSingleton()
        bind(BookLendingService::class.java).to(BookLendingServiceImpl::class.java).asEagerSingleton()
        bind(ReadingProgressService::class.java).to(ReadingProgressServiceImpl::class.java).asEagerSingleton()
        bind(DomainBookService::class.java)
            .to(DomainBookServiceImpl::class.java)
            .asEagerSingleton()
        bind(BookOperationService::class.java)
            .to(BookOperationServiceImpl::class.java)
            .asEagerSingleton()
        bind(UserRepository::class.java)
            .to(UserRepositoryImpl::class.java)
            .asEagerSingleton()

        // Bind resources
        bind(BookResource::class.java).asEagerSingleton()
        bind(BookNoteResource::class.java).asEagerSingleton()
        bind(BookLendingResource::class.java).asEagerSingleton()
        bind(ReadingProgressResource::class.java).asEagerSingleton()
        bind(GlobalBookNoteResource::class.java).asEagerSingleton()
        bind(GlobalBookLendingResource::class.java).asEagerSingleton()
        bind(UserResource::class.java).asEagerSingleton()

        // Auth related bindings
        bind(JwtAuthenticator::class.java).asEagerSingleton()
        bind(RoleAuthorizer::class.java).asEagerSingleton()
    }

    @Provides
    @Singleton
    fun provideConfiguration(): ReadingVaultConfiguration {
        return configuration
    }

    @Provides
    @Singleton
    fun provideDataSource(configuration: ReadingVaultConfiguration): HikariDataSource {
        val config = HikariConfig()
        config.jdbcUrl = configuration.databaseUrl
        config.username = configuration.databaseUser
        config.password = configuration.databasePassword
        config.driverClassName = "org.h2.Driver"
        config.maximumPoolSize = configuration.maxPoolSize
        config.minimumIdle = configuration.minIdle
        config.idleTimeout = configuration.idleTimeout
        config.connectionTimeout = configuration.connectionTimeout
        config.isAutoCommit = configuration.autoCommit
        config.transactionIsolation = configuration.transactionIsolation

        // Dodatne korisne konfiguracije za H2
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        return HikariDataSource(config)
    }

    @Provides
    @Singleton
    fun provideDatabase(dataSource: HikariDataSource): Database {
        return Database.connect(dataSource)
    }

    @Provides
    @Singleton
    fun provideSchemaInitializer(
        bookRepository: BookRepositoryImpl,
        bookNoteRepository: BookNoteRepositoryImpl,
        bookLendingRepository: BookLendingRepositoryImpl,
        readingProgressRepository: ReadingProgressRepositoryImpl
    ): SchemaInitializer {
        return SchemaInitializer(
            bookRepository,
            bookNoteRepository,
            bookLendingRepository,
            readingProgressRepository
        ).apply { initialize() }
    }
}