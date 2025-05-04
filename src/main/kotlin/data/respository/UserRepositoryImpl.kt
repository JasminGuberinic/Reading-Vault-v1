package org.virtualcode.data.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.virtualcode.data.table.Users
import org.virtualcode.domain.model.User
import org.virtualcode.domain.model.AuthProvider
import org.virtualcode.domain.repository.UserRepository
import com.google.inject.Inject
import com.google.inject.Singleton
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val database: Database
) : UserRepository {

    override fun getAll(): List<User> = transaction(database) {
        Users.selectAll().map { it.toUser() }
    }

    override fun findById(id: Int): User? = transaction(database) {
        Users.selectAll().where { Users.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }

    override fun findByEmail(email: String): User? = transaction(database) {
        Users.selectAll().where { Users.email eq email }
            .map { it.toUser() }
            .singleOrNull()
    }


    override fun findByProviderId(authProvider: String, providerId: String): User? = transaction(database) {
        Users.selectAll().where { (Users.authProvider eq AuthProvider.valueOf(authProvider)) and (Users.providerId eq providerId) }
            .map { it.toUser() }
            .singleOrNull()
    }

    override fun create(user: User): User = transaction(database) {
        val id = Users.insert { row ->
            row[email] = user.email
            row[passwordHash] = user.passwordHash
            row[role] = user.role
            row[isActive] = user.isActive
            row[createdAt] = user.createdAt
            row[lastLoginAt] = user.lastLoginAt
            row[authProvider] = user.authProvider
            row[providerId] = user.providerId
            row[displayName] = user.displayName
            row[imageUrl] = user.imageUrl
        } get Users.id
        user.copy(id = id)
    }

    override fun update(user: User): User = transaction(database) {
        Users.update({ Users.id eq user.id!! }) { row ->
            row[email] = user.email
            row[passwordHash] = user.passwordHash
            row[role] = user.role
            row[isActive] = user.isActive
            row[createdAt] = user.createdAt
            row[lastLoginAt] = user.lastLoginAt
            row[authProvider] = user.authProvider
            row[providerId] = user.providerId
            row[displayName] = user.displayName
            row[imageUrl] = user.imageUrl
        }
        user
    }

    override fun delete(id: Int): Boolean = transaction(database) {
        Users.deleteWhere { Users.id eq id } > 0
    }

    // Helper extension function to map ResultRow to User
    private fun ResultRow.toUser() = User(
        id = this[Users.id],
        email = this[Users.email],
        passwordHash = this[Users.passwordHash],
        role = this[Users.role],
        isActive = this[Users.isActive],
        createdAt = this[Users.createdAt],
        lastLoginAt = this[Users.lastLoginAt],
        authProvider = this[Users.authProvider],
        providerId = this[Users.providerId],
        displayName = this[Users.displayName],
        imageUrl = this[Users.imageUrl]
    )
}