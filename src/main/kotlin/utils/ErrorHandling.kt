package utils

/**
 * Error handling strategies.
 *
 * If an exception is thrown while running a code section, strategies can
 * be applied to cope with these errors:
 * - SILENT_DROP: drop the entity/row without warning
 * - SILENT_KEEP: keep the entity/row without warning
 * - WARN_DROP: drop the entity/row and print a warning
 * - WARN_KEEP: keep the entity/row but print a warning
 * - ERROR_DROP: drop the entity/row and print an error + stack trace
 * - ERROR_KEEP: keep the entity/row but print an error + stack trace
 * - THROW: throw an exception with detailed message
 */
enum class ErrorHandling { //TODO introduce interface? TODO maybe separate error handling for missing column

    /** Upon parsing errors: Drop the entity/row without warning. */
    SILENT_DROP {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? = null
    },

    /** Upon parsing errors: Keep the entity/row without warning. */
    SILENT_KEEP {
        // TODO should this be discouraged ore removed entirely?
        override fun <E> handleException(e: Exception, entity: E, message: String): E? = entity
    },

    /** Upon parsing errors: Drop the entity/row and print a warning */
    WARN_DROP {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? {
            println("WARNING (dropping row): $message")
            return null
        }
    },

    /** Upon parsing errors: Keep the entity/row but print a warning */
    WARN_KEEP {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? {
            println("WARNING (keeping row): $message")
            return entity
        }
    },

    /** Upon parsing errors: Drop the entity/row and print error and stack trace */
    ERROR_DROP {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? {
            println("ERROR (dropping row): $message")
            e.printStackTrace()
            return null
        }
    },

    /** Upon parsing errors: Keep the entity/row but print error and stack trace */
    ERROR_KEEP {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? {
            println("ERROR (keeping row): $message")
            e.printStackTrace()
            return entity
        }
    },

    /** Upon parsing errors: throw an exception with detailed message. */
    THROW {
        override fun <E> handleException(e: Exception, entity: E, message: String): E? {
            throw IllegalArgumentException(message, e) //TODO which specific exception type should we use here?
        }
    };

    /**
     * Execute the given runnable and handle exceptions by applying the
     * specific error handling strategy.
     *
     * @param entity the entity for which the value is parsed
     * @param runnable a function that applies the parsing and returns the
     *     entity or null
     * @param E the generic type of the entity to be processed
     * @return the (updated) entity, may be null if errors occurred and the
     *     error handling strategies drops the entity/row
     */
    fun <E> handle(
        entity: E? = null,
        errorMessage: () -> String,
        runnable: () -> E?,
    ): E? {

        return try {
            runnable() ?: handleException( //on first null returned by any parser handle exception
                RuntimeException("The handled process returned null!"),
                entity,
                errorMessage()
            )

        } catch (e: NumberFormatException) { //TODO add more exceptions, can we build this without exceptions?
            handleException(e, entity, errorMessage())
        }
    }

    /**
     * Handle exception: this abstract method specifies what is to be done
     * in case of errors. It is implemented by the concrete error handling
     * strategies.
     *
     * @param e the exception that was caught
     * @param entity the entity being processed
     * @param message the error message
     * @param E the generic type of the processed entity
     * @return the given entity if it is to be kept despite the errors, or null
     *     if it is to be dropped
     */
    protected abstract fun <E> handleException(e: Exception, entity: E, message: String): E?

}
