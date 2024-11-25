
import domain.location.LOCATIONUNKNOWN
import kotlin.test.asserter

/**
 * Four example locations for testing
 */
val START = LOCATIONUNKNOWN
val OTHER = LOCATIONUNKNOWN
val THIRD = LOCATIONUNKNOWN
val FOURTH = LOCATIONUNKNOWN

internal fun messagePrefix(message: String?) = if (message == null) "" else "$message. "

fun assertNotContains(
    charSequence: CharSequence,
    other: CharSequence,
    ignoreCase: Boolean = false,
    message: String? = null
) {
    asserter.assertTrue(
        {
            messagePrefix(message) +
                "Expected the char sequence not to contain the substring.\n" +
                "CharSequence <$charSequence>, substring <$other>, ignoreCase <$ignoreCase>."
        },
        !charSequence.contains(other, ignoreCase)
    )
}

fun assertEmpty(charSequence: CharSequence, message: String? = null) {
    asserter.assertTrue(
        {
            messagePrefix(message) +
                "Expected the char sequence to be empty.\n" +
                "CharSequence <$charSequence>."
        },
        charSequence.isEmpty()
    )
}
