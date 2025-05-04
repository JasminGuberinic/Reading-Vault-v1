package org.virtualcode.data.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.virtualcode.domain.model.AuthProvider
import org.virtualcode.domain.model.UserRole

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255).nullable()
    val role = enumerationByName("role", 20, UserRole::class)
    val isActive = bool("is_active")
    val createdAt = datetime("created_at")
    val lastLoginAt = datetime("last_login_at").nullable()
    val authProvider = enumerationByName("auth_provider", 20, AuthProvider::class)
    val providerId = varchar("provider_id", 255).nullable()
    val displayName = varchar("display_name", 255).nullable()
    val imageUrl = varchar("image_url", 1024).nullable()

    override val primaryKey = PrimaryKey(id)
}