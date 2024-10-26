package utils.files

import utils.ErrorHandling
import utils.errorScope
import java.io.File
import kotlin.io.path.Path

fun requireFileReadAccess(
    file: File,
    errorLevel: ErrorHandling = ErrorHandling.THROW,
    messagePrefix: String = "",
): Boolean =
    errorScope(errorLevel, "$messagePrefix Error while checking read access to file: '$file'!") {
        require(file.exists()) {
            "File does not exist: $file!"
        }

        require(file.canRead()) {
            "Cannot read from file $file! Make sure its a file and access rights are set correctly!"
        }

        true
    } ?: false

fun requireFileWriteAccess(
    file: File, // TODO path
    errorLevel: ErrorHandling = ErrorHandling.THROW,
    messagePrefix: String = "",
): Boolean =
    errorScope(errorLevel, "$messagePrefix Error while checking write access to file: '$file'!") {
        val parentDir = file.parentFile ?: Path("").toFile()

        if (!parentDir.exists()) {
            require(parentDir.mkdirs()) {
                "Failed to create parent directories of ${file.absolutePath}! " +
                    "(may have succeeded in creating some of the other necessary parent directories)"
            }
        }

        require(parentDir.canWrite()) {
            "Cannot write to directory ${parentDir.absolutePath} of $file!"
        }

        true
    } ?: false
