package io.memorix.authentication

import org.mindrot.jbcrypt.BCrypt

class PasswordAuthentication {
    companion object {

        /*
            Hash user provided password for persistent storage.
         */
        fun hashPassword(password: String): String {
            val salt = BCrypt.gensalt()
            return BCrypt.hashpw(password, salt)
        }

        /*
            Confirm provided password matches stored hash.
         */
        fun checkPassword(password: String, hash: String): Boolean {
            return BCrypt.checkpw(password, hash)
        }
    }
}