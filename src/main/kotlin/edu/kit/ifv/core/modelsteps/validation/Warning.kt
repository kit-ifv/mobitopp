package edu.kit.ifv.core.modelsteps.validation
import edu.kit.ifv.core.modelsteps.Context
import edu.kit.ifv.core.modelsteps.NO_ERROR_MESSAGE
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

private fun Context.log(message: String, isError: Boolean) {
    if (isError) {
        logError(message)
    } else {
        logWarning(message)
    }
}

/**
 * Catch exceptions and add them as warning/error to the report.
 *
 * @receiver The context to which caught exceptions should be reported.
 * @param C The context type.
 * @param message The message to log.
 * @param exceptionsAreErrors whether exceptions added as errors (if true) or warnings (if false)
 * @param scope the scope function that may throw exceptions.
 * @return the result of the scope function or null if an exception occurred.
 */
@Suppress("TooGenericExceptionCaught")
fun <C : Context, R> C.validateNoException(
    message: C.() -> String = { "Exception" },
    exceptionsAreErrors: Boolean = true,
    scope: () -> R,
): R? = try {
    scope()
} catch (e: Throwable) {
    log(message() + ": " + (e.message ?: NO_ERROR_MESSAGE), exceptionsAreErrors)
    null
}

/**
 * Evaluates a [predicate] and logs a [message] if it fails.
 *
 * This is used during validation to check for specific conditions without throwing exceptions.
 *
 * @receiver The context to which the result is logged.
 * @param C The context type.
 * @param message A function providing the message to log if the predicate is false.
 * @param isError If true, the message is logged as an error; otherwise, as a warning.
 * @param predicate The condition to check.
 * @return True if the predicate is satisfied, false otherwise.
 */
fun <C : Context> C.validateCondition(
    message: C.() -> String,
    isError: Boolean = false,
    predicate: () -> Boolean,
): Boolean {
    if (!predicate()) {
        log(message(), isError)
        return !isError
    }
    return true
}

/**
 * Validates both read and write access for a given [path].
 *
 * @receiver The context to which the result is logged.
 * @param C The context type.
 * @param path The file or directory path to check.
 * @param isError If true, access failures are reported as errors.
 * @param fileDescription A description of the file for the report (optional).
 */
fun <C : Context> C.validateFileReadWriteAccess(path: Path, isError: Boolean = true, fileDescription: String = "") {
    validateFileReadAccess(path, isError, fileDescription)
    validateFileWriteAccess(path, isError, fileDescription)
}

/**
 * Validates that a file exists and is readable at the given [path].
 *
 * @receiver The context to which the result is logged.
 * @param C The context type.
 * @param path The file path to check.
 * @param isError If true, access failures are reported as errors.
 * @param fileDescription A description of the file (optional).
 * @return True if readable, false otherwise.
 */
fun <C : Context> C.validateFileReadAccess(
    path: Path,
    isError: Boolean = true,
    fileDescription: String = "",
): Boolean = validateNoException(
    message = { "Validate read access of: ${path.absolutePathString()} ($fileDescription)" },
    exceptionsAreErrors = isError,
) {
    require(path.exists()) {
        "File does not exist: $path!"
    }

    require(path.toFile().canRead()) {
        "Cannot read from file $path! Make sure its a file and access rights are set correctly!"
    }

    true
} ?: false

/**
 * Validates that the parent directory of [path] is writable, creating it if it doesn't exist.
 *
 * @receiver The context to which the result is logged.
 * @param C The context type.
 * @param path The file path to check write access for.
 * @param isError If true, access failures are reported as errors.
 * @param fileDescription A description of the file (optional).
 * @return True if writable, false otherwise.
 */
fun <C : Context> C.validateFileWriteAccess(path: Path, isError: Boolean = true, fileDescription: String = "") =
    validateNoException(
        message = { "Validate write access of: ${path.absolutePathString()} ($fileDescription)" },
        exceptionsAreErrors = isError,
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
