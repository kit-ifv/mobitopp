package modeling.validation

import utils.collections.printAsTree
import utils.files.requireFileReadAccess
import java.io.File

/**
 * Warnings can occur during validation of ModelSteps.
 * They have a message and indicate whether they are a critical error, which cause validation to fail.
 * Warnings are tree like structures and can contain sub-warnings providing more details.
 *
 * @property message the warning message
 * @property isError whether this warning is a critical error
 */
class Warning(
    val message: String,
    val isError: Boolean,
) {

    constructor(cause: Throwable, isError: Boolean) : this(
        cause.message ?: ("Validation" + if (isError) { "Error" } else { "Warning" }),
        isError
    )

    private val childWarnings: MutableList<Warning> = mutableListOf()
    val subWarnings: List<Warning>
        get() = childWarnings

    /**
     * Add the given child [Warning] as sub-warning of this [Warning].
     *
     * @param warning the sub-warning to be added.
     */
    fun addChild(warning: Warning) {
        childWarnings.add(warning)
    }

    /**
     * Add a sub-warning with the given [message] and [isError] flag.
     *
     * @param message the message of the new sub-warning
     * @param isError the error flag of the new sub-warning
     */
    fun addChild(message: String, isError: Boolean) {
        childWarnings.add(Warning(message, isError))
    }

    /**
     * Add a sub-warning with the given [Throwable]'s message and [isError] flag.
     *
     * @param cause the [Throwable] with a message to be added as a sub-warning
     * @param isError the error flag of the new sub-warning
     */
    fun addChild(cause: Throwable, isError: Boolean) {
        childWarnings.add(Warning(cause, isError))
    }

    /**
     * Check whether this warning or (recursively) any sub-warnings are marked as errors.
     * @return whether this warning or its sub warnings are errors and cause validation to fail
     */
    fun containsError(): Boolean = this.isError or subWarnings.any { it.containsError() }

    /**
     * Print this warning in tree representation to the console.     *
     */
    fun printTree() {
        printAsTree(
            this,
            { if (it.containsError()) { "ERROR: " } else { "WARNING: " } + it.message },
            false
        ) { it.subWarnings }
    }
}

/**
 * ValidateScope is a scope function to catch exceptions add add them as sub-warning
 * to the warning specified by the given [message].
 *
 * @param message the message of the root [Warning]
 * @param exceptionsAreErrors whether exceptions added as sub-warnings should be marked as errors
 * @param scope the scope function executed on the root [Warning] that may throw exceptions.
 * @return the defined root warning containing sub-warnings of caught exceptions or null if none were caught
 */
fun validateScope(
    message: String = "Validate Scope",
    exceptionsAreErrors: Boolean = true,
    scope: Warning.() -> Unit
): Warning? =
    Warning(message, false).apply {
        subWarning(exceptionsAreErrors, scope)
    }.takeIf { it.subWarnings.isNotEmpty() }

/**
 * Catch exceptions and add them as sub-warning
 * to the receiver [Warning].
 *
 * @param exceptionsAreErrors whether exceptions added as sub-warnings should be marked as errors
 * @param scope the scope function executed on the root [Warning] that may throw exceptions.
 * @receiver the paren [Warning] to which caught exceptions should be added to as child warnings
 */
@Suppress("TooGenericExceptionCaught")
fun Warning.subWarning(
    exceptionsAreErrors: Boolean = true,
    scope: Warning.() -> Unit
) = try {
    this.scope()
} catch (e: Throwable) {
    this.addChild(e, exceptionsAreErrors)
}

fun Warning.subValidateFileReadAccess(file: File, isError: Boolean = true, fileDescription: String = "") =
    validateFileReadAccess(file, isError, fileDescription)?.also {
        this.addChild(it)
    }

fun validateFileReadAccess(file: File, isError: Boolean = true, fileDescription: String = "") = validateScope(
    message = "Validate read access of: ${file.absolutePath}",
    exceptionsAreErrors = isError
) {
    requireFileReadAccess(file, messagePrefix = fileDescription)
}
