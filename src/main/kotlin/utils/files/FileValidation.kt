package utils.files

import utils.ErrorHandling
import utils.errorScope
import java.io.File

fun requireFileReadAccess(
    file: File,
    errorLevel: ErrorHandling = ErrorHandling.WARNING,
    messagePrefix: String = "",
): Boolean =
    errorScope(errorLevel, "$messagePrefix Error while checking access to file: '$file'!") {
        require(file.exists()) {
            "File does not exist: $file!"
        }

        require(file.canRead()) {
            "Cannot read from file $file! Make sure its a file and access rights are set correctly!"
        }

        true
    } ?: false
