package utils

private const val DEFAULT_MESSAGE = "No Message Specified!"

/**
 * Error handling strategies.
 *
 * If an exception is thrown while running a code section, strategies can
 * be applied to cope with these errors:
 * - SILENT: drop the entity without warning
 * - WARNING: drop the entity and print a warning
 * - ERROR: drop the entity and print an error + stack trace
 * - THROW: throw an exception with detailed message
 */
enum class ErrorHandling {
    // TODO introduce interface? TODO maybe separate error handling for missing column

    /** Upon parsing errors: Drop the entity/row without warning. */
    SILENT {
        override fun processException(e: Exception, message: String) = Unit
    },

    /** Upon parsing errors: Drop the entity/row and print a warning */
    WARNING {
        override fun processException(e: Exception, message: String) {
            println("WARNING: $message: ${e.message}")
        }
    },

    /** Upon parsing errors: Drop the entity/row and print error and stack trace */
    ERROR {
        override fun processException(e: Exception, message: String) {
            println("ERROR: $message")
            e.printStackTrace()
        }
    },

    /** Upon parsing errors: throw an exception with detailed message. */
    THROW {
        override fun processException(e: Exception, message: String) {
            println("CRITICAL ERROR: $message")
            println("    ${e.message}")
            throw IllegalArgumentException(message, e)
            // TODO which specific exception type should we use here?
        }
    },

    /** Upon parsing errors: throw an exception with detailed message. */
    THROW_NO_LOG {
        override fun processException(e: Exception, message: String) {
            throw IllegalArgumentException(message, e)
            // TODO which specific exception type should we use here?
        }
    }, ;

    fun <E> handle(runnable: () -> E?): E? = this.handle(runnable) { e -> e.message ?: DEFAULT_MESSAGE }

    /**
     * Execute the given runnable and handle exceptions by applying the
     * specific error handling strategy.
     *
     * @param runnable a function that returns the entity or null
     * @param E the generic type of the entity to be processed
     * @return the result of the given runnable or null/exception in case of
     *     errors
     */
    @Suppress("TooGenericExceptionCaught")
    fun <E> handle(runnable: () -> E?, errorMessage: (Exception) -> String): E? {
        return try {
            runnable()
        } catch (e: Exception) { // TODO add more exceptions, can we build this without exceptions?
            processException(e, errorMessage(e))
            return null
        }
    }

    /**
     * Process exception: this abstract method specifies what is to be done
     * in case of errors. It is implemented by the concrete error handling
     * strategies.
     *
     * @param e the exception that was caught
     * @param message the error message
     */
    protected abstract fun processException(e: Exception, message: String)
}

data class ValidationMessage(val message: String, val stepIsInvalid: Boolean, val cause: Throwable?)

fun <R> errorScope(errorHandling: ErrorHandling = ErrorHandling.WARNING, message: String, runnable: () -> R): R? =
    errorHandling.handle(
        runnable,
    ) { e ->
        "$message:\n    ${e.message}"
    }

fun test(value: Boolean, lazyMessage: () -> String): Boolean = errorScope(
    errorHandling = ErrorHandling.WARNING,
    message = if (value) "" else lazyMessage(),
) {
    check(value)
    true
} ?: false
