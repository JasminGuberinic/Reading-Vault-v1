package org.virtualcode.auth

import org.virtualcode.domain.model.User
import java.security.Principal

data class UserPrincipal(val user: User) : Principal {
    override fun getName(): String = user.email
}