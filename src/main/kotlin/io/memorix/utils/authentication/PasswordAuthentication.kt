package io.memorix.authentication

import org.mindrot.jbcrypt.BCrypt

class PasswordAuthentication {
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