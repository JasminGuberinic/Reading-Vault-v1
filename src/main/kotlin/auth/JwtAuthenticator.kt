package org.virtualcode.auth

import com.google.inject.Inject
import io.dropwizard.auth.Authenticator
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.virtualcode.domain.repository.UserRepository
import java.util.Optional

class JwtAuthenticator @Inject constructor(
    private val userRepository: UserRepository
) : Authenticator<String, UserPrincipal> {

    private val secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256)

    override fun authenticate(token: String): Optional<UserPrincipal> {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body

            val email = claims.subject
            val user = userRepository.findByEmail(email)

            if (user != null && user.isActive) {
                Optional.of(UserPrincipal(user))
            } else {
                Optional.empty()
            }
        } catch (e: Exception) {
            Optional.empty()
        }
    }

    fun generateToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .signWith(secretKey)
            .compact()
    }
}