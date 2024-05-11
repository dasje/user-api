package io.memorix.inputvalidation

import io.memorix.messages.ErrorTypes
import io.memorix.messages.OutgoingMessage

class UserInputValidation {
    companion object {
        /*
            Validate password before hashing.
            Validate for password length, upper and lower cases, numbers and special characters.
         */
        fun validatePassword(pwd: String): OutgoingMessage<Boolean> {
            val minMaxLength = """^[\s\S]{8,32}$""".toRegex()
            val upper = """[A-Z]""".toRegex()
            val lower = """[a-z]""".toRegex()
            val number = """[0-9]""".toRegex()
            val special = "[ !\"#$%&'()*+,-./:;<=>?@^_`{|}~]".toRegex()

            if (!minMaxLength.containsMatchIn(pwd)) { return OutgoingMessage.ValidationError(ErrorTypes.PWD_ERR_LEN.errorDetail) }
            if (!upper.containsMatchIn(pwd)) { return OutgoingMessage.ValidationError(ErrorTypes.PWD_ERR_UPPER.errorDetail) }
            if (!lower.containsMatchIn(pwd)) { return OutgoingMessage.ValidationError(ErrorTypes.PWD_ERR_LOWER.errorDetail) }
            if (!number.containsMatchIn(pwd)) { return OutgoingMessage.ValidationError(ErrorTypes.PWD_ERR_DIGIT.errorDetail) }
            if (!special.containsMatchIn(pwd)) { return OutgoingMessage.ValidationError(ErrorTypes.PWD_ERR_SPECIAL.errorDetail) }

            return OutgoingMessage.Success(true)
        }

        /*
            Validate email format. Email must be of format <string>@<string>.
         */
        fun validateEmail(email: String): OutgoingMessage<Boolean> {
            val emailRegex = """(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$)""".toRegex()

            if (!emailRegex.containsMatchIn(email)) { return OutgoingMessage.ValidationError(ErrorTypes.EMAIL_ERR_FORMAT.errorDetail) }
            return OutgoingMessage.Success(true)
        }
    }
}