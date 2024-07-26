
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import kotlin.test.asserter

/**
 * Four example locations for testing
 */
object START : Location by LOCATIONUNKNOWN
object OTHER : Location by LOCATIONUNKNOWN
object THIRD : Location by LOCATIONUNKNOWN
object FOURTH : Location by LOCATIONUNKNOWN

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
