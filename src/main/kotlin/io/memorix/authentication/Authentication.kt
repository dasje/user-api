package io.memorix.Authentication

import org.mindrot.jbcrypt.BCrypt

class Authentication {
    companion object {
        fun hashPassword(password: String): String {
            val salt = BCrypt.gensalt()
            return BCrypt.hashpw(password, salt)
        }

        fun checkPassword(password: String, hash: String): Boolean {
            return BCrypt.checkpw(password, hash)
        }
    }
}