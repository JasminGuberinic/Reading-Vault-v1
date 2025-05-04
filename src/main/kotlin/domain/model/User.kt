package org.virtualcode.domain.model

import java.time.LocalDateTime

enum class UserRole {
    USER,
    ADMIN
}

enum class AuthProvider {
    LOCAL,
    GOOGLE
}

data class User(
    val id: Int? = null,
    val email: String,  // Email je username
    val passwordHash: String?, // Nullable jer kod SSO nije potreban
    val role: UserRole = UserRole.USER,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastLoginAt: LocalDateTime? = null,
    val authProvider: AuthProvider = AuthProvider.LOCAL,
    val providerId: String? = null, // Google user ID
    val displayName: String? = null, // Ime koje će se prikazivati (može biti iz Google profila)
    val imageUrl: String? = null // Profile slika (može biti iz Google profila)
)