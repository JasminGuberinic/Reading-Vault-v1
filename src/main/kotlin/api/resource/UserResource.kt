package org.virtualcode.api.resource

import org.virtualcode.domain.repository.UserRepository
import org.virtualcode.domain.model.User
import com.google.inject.Inject
import com.google.inject.Singleton
import io.dropwizard.auth.Auth
import org.mindrot.jbcrypt.BCrypt
import org.virtualcode.api.dto.AuthResponse
import org.virtualcode.api.dto.LoginRequest
import org.virtualcode.api.dto.UserResponse
import org.virtualcode.auth.JwtAuthenticator
import org.virtualcode.auth.UserPrincipal
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.NameBinding
import javax.annotation.security.RolesAllowed

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
class UserResource @Inject constructor(
    private val userRepository: UserRepository,
    private val jwtAuthenticator: JwtAuthenticator
) {
    @POST
    @Path("/login")
    fun login(loginRequest: LoginRequest): Response {
        val user = userRepository.findByEmail(loginRequest.email)
            ?: return Response.status(Response.Status.UNAUTHORIZED).build()

        if (!BCrypt.checkpw(loginRequest.password, user.passwordHash)) {
            return Response.status(Response.Status.UNAUTHORIZED).build()
        }

        val token = jwtAuthenticator.generateToken(user.email)
        val userResponse = UserResponse(
            id = user.id,
            email = user.email,
            displayName = user.displayName,
            role = user.role.name
        )

        return Response.ok(AuthResponse(token, userResponse)).build()
    }

    @GET
    @Path("/me")
    @RolesAllowed("USER")
    fun getCurrentUser(@Auth principal: UserPrincipal): Response {
        return Response.ok(principal.user).build()
    }

    @GET
    fun getAllUsers(): List<User> = userRepository.getAll()

    @GET
    @Path("/{id}")
    fun getUserById(@PathParam("id") id: Int): Response {
        val user = userRepository.findById(id)
        return if (user != null) {
            Response.ok(user).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to "User not found")).build()
        }
    }

    @POST
    fun createUser(user: User): Response {
        val hashedUser = user.copy(
            passwordHash = BCrypt.hashpw(user.passwordHash, BCrypt.gensalt())
        )

        val created = userRepository.create(hashedUser)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    fun updateUser(@PathParam("id") id: Int, user: User): Response {
        val existing = userRepository.findById(id)
        return if (existing != null) {
            val updated = userRepository.update(user.copy(id = id))
            Response.ok(updated).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to "User not found")).build()
        }
    }

    @DELETE
    @Path("/{id}")
    fun deleteUser(@PathParam("id") id: Int): Response {
        val deleted = userRepository.delete(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(Response.Status.NOT_FOUND).entity(mapOf("error" to "User not found")).build()
        }
    }
}