package org.virtualcode.domain.repository

import org.virtualcode.domain.model.User

/**
 * Repository interface for User entity.
 * Defines operations for accessing and manipulating users.
 */
interface UserRepository {
    fun getAll(): List<User>
    fun findById(id: Int): User?
    fun findByEmail(email: String): User?
    fun findByProviderId(authProvider: String, providerId: String): User?
    fun create(user: User): User
    fun update(user: User): User
    fun delete(id: Int): Boolean
}