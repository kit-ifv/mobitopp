package utils

/**
 * Error handling strategies.
 *
 * If an exception is thrown while running a code section, strategies can
 * be applied to cope with these errors:
 * - SILENT_DROP: drop the entity/row without warning
 * - WARN_DROP: drop the entity/row and print a warning
 * - ERROR_DROP: drop the entity/row and print an error + stack trace
 * - THROW: throw an exception with detailed message
 */
enum class ErrorHandling { //TODO introduce interface? TODO maybe separate error handling for missing column

    /** Upon parsing errors: Drop the entity/row without warning. */
    SILENT_DROP {
        override fun handleException(e: Exception, message: String) = Unit
    },

    /** Upon parsing errors: Drop the entity/row and print a warning */
    WARN_DROP {
        override fun handleException(e: Exception, message: String) {
            println("WARNING (dropping row): $message")
        }
    },

    /** Upon parsing errors: Drop the entity/row and print error and stack trace */
    ERROR_DROP {
        override fun handleException(e: Exception, message: String) {
            println("ERROR (dropping row): $message")
            e.printStackTrace()
        }
    },

    /** Upon parsing errors: throw an exception with detailed message. */
    THROW {
        override fun handleException(e: Exception, message: String) {
            throw IllegalArgumentException(message, e) //TODO which specific exception type should we use here?
        }
    };

    /**
     * Execute the given runnable and handle exceptions by applying the
     * specific error handling strategy.
     *
     * @param runnable a function that applies the parsing and returns the
     *     entity or null
     * @param E the generic type of the entity to be processed
     * @return the (updated) entity, may be null if errors occurred and the
     *     error handling strategies drops the entity/row
     */
    @Suppress("TooGenericExceptionCaught")
    fun <E> handle(
        runnable: () -> E?,
        errorMessage: () -> String,
    ): E? {

        return try {
            runnable()
        } catch (e: Exception) { //TODO add more exceptions, can we build this without exceptions?
            handleException(e, errorMessage())
            return null
        }
    }

    /**
     * Handle exception: this abstract method specifies what is to be done
     * in case of errors. It is implemented by the concrete error handling
     * strategies.
     *
     * @param e the exception that was caught
     * @param message the error message
     * @param E the generic type of the processed entity
     */
    protected abstract fun handleException(e: Exception, message: String)

}