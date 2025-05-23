package utils.files

import utils.ErrorHandling
import utils.errorScope
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

fun requireFileReadAccess(
    path: Path,
    errorLevel: ErrorHandling = ErrorHandling.THROW,
    messagePrefix: String = "",
): Boolean =
    errorScope(errorLevel, "$messagePrefix Error while checking read access to file: '$path'!") {
        require(path.exists()) {
            "File does not exist: $path!"
        }

        require(path.toFile().canRead()) {
            "Cannot read from file $path! Make sure its a file and access rights are set correctly!"
        }

        true
    } ?: false

fun requireFileWriteAccess(
    path: Path, // TODO path
    errorLevel: ErrorHandling = ErrorHandling.THROW,
    messagePrefix: String = "",
): Boolean =
    errorScope(errorLevel, "$messagePrefix Error while checking write access to file: '$path'!") {
        val parentDir = path.parent ?: Path("")

        if (!parentDir.exists()) {
            require(parentDir.createDirectories().exists()) {
                "Failed to create parent directories of ${path.absolutePathString()}! " +
                    "(may have succeeded in creating some of the other necessary parent directories)"
            }
        }

        require(parentDir.toFile().canWrite()) {
            "Cannot write to directory ${parentDir.absolutePathString()} of $path!"
        }

        true
    } ?: false
