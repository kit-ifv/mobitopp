package core.modelsteps.validation

import core.modelsteps.Context
import utils.report.ReportBuilder
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

private fun ReportBuilder.log(title: String, message: String, isError: Boolean) {
    if (isError) {
        addErrorLog(title, message)
    } else {
        addWarningLog(title, message)
    }
}

/**
 * Catch exceptions and add them as warning/error to the report.
 *
 * @param exceptionsAreErrors whether exceptions added as errors (if true) or warnings (if false)
 * @param scope the scope function that may throw exceptions.
 * @receiver the [ReportBuilder] to which caught exceptions should be added to
 */
@Suppress("TooGenericExceptionCaught")
fun <C: Context, R> C.validateNoException(
    title: C.() -> String = { "Exception" },
    exceptionsAreErrors: Boolean = true,
    scope: () -> R,
): R? = try {
    scope()
} catch (e: Throwable) {
    report.log(title(), e.message ?: "", exceptionsAreErrors)
    null
}

fun <C: Context> C.validateCondition(message: C.() -> String, isError: Boolean = false, predicate: () -> Boolean): Boolean {
    if (!predicate()) {
        report.log("validate condition", message(), isError)
        return false
    }
    return true
}

fun <C: Context> C.validateFileReadWriteAccess(path: Path, isError: Boolean = true, fileDescription: String = "") {
    validateFileReadAccess(path, isError, fileDescription)
    validateFileWriteAccess(path, isError, fileDescription)
}

fun <C: Context> C.validateFileReadAccess(path: Path, isError: Boolean = true, fileDescription: String = ""): Boolean =
    validateNoException(
        title = { "Validate read access of: ${path.absolutePathString()}" },
        exceptionsAreErrors = isError
    ) {
        require(path.exists()) {
            "File does not exist: $path!"
        }

        require(path.toFile().canRead()) {
            "Cannot read from file $path! Make sure its a file and access rights are set correctly!"
        }

        true
    } ?: false

fun <C: Context> C.validateFileWriteAccess(path: Path, isError: Boolean = true, fileDescription: String = "") =
    validateNoException(
        title = { "Validate write access of: ${path.absolutePathString()}" },
        exceptionsAreErrors = isError
    ) {
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
