package utils

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
enum class ErrorHandling { //TODO introduce interface? TODO maybe separate error handling for missing column

    /** Upon parsing errors: Drop the entity/row without warning. */
    SILENT {
        override fun accumulating() = SILENT

        override fun processException(e: Exception, message: String) = Unit
    },

    /** Upon parsing errors: print a warning and re-throw the exception */
    WARN_COLLECT {
        override fun accumulating() = WARN_COLLECT

        override fun processException(e: Exception, message: String) {
            WARNING.processException(e, message)
            throw e
        }
    },

    /** Upon parsing errors: Drop the entity/row and print a warning */
    WARNING {
        override fun accumulating() = WARN_COLLECT

        override fun processException(e: Exception, message: String) {
            println("WARNING (dropping row): $message")
        }
    },

    /** Upon parsing errors: Drop the entity/row and print error and stack trace */
    ERROR {
        override fun accumulating() = WARN_COLLECT

        override fun processException(e: Exception, message: String) {
            println("ERROR (dropping row): $message")
            e.printStackTrace()
        }
    },

    /** Upon parsing errors: throw an exception with detailed message. */
    THROW {
        override fun accumulating() = THROW

        override fun processException(e: Exception, message: String) {
            throw IllegalArgumentException(message, e) //TODO which specific exception type should we use here?
        }
    };

    /**
     * Returns the [ErrorHandling] strategy to be used when errors should be
     * accumulated in a compact way. The returned strategy produces less or
     * equal console output than this level.
     *
     * @return the associated accumulating error handling strategy
     */
    abstract fun accumulating(): ErrorHandling

    /**
     * Execute the given runnable and handle exceptions by applying the
     * specific error handling strategy.
     *
     * @param runnable a function that applies the parsing and returns the
     *     entity or null
     * @param E the generic type of the entity to be processed
     * @return the result of the given runnable or null/exception in case of
     *     errors
     */
    @Suppress("TooGenericExceptionCaught")
    fun <E> handle(
        runnable: () -> E?,
        errorMessage: () -> String,
    ): E? {

        return try {
            runnable()
        } catch (e: Exception) { //TODO add more exceptions, can we build this without exceptions?
            processException(e, errorMessage())
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
