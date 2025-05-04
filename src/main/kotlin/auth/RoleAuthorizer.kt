package org.virtualcode.auth

import io.dropwizard.auth.Authorizer
import org.virtualcode.domain.model.UserRole

class RoleAuthorizer : Authorizer<UserPrincipal> {
    @Deprecated("Deprecated in Java")
    override fun authorize(principal: UserPrincipal, role: String): Boolean {
        return principal.user.role == UserRole.valueOf(role)
    }
}