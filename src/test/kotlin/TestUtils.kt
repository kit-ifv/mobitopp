import domain.shared.location.StandardLocation
import kotlin.test.asserter

/**
 * Four example locations for testing
 */
val START = StandardLocation.LOCATIONUNKNOWN
val OTHER = StandardLocation.LOCATIONUNKNOWN
val THIRD = StandardLocation.LOCATIONUNKNOWN
val FOURTH = StandardLocation.LOCATIONUNKNOWN

internal fun messagePrefix(message: String?) = if (message == null) "" else "$message. "

fun assertNotContains(
    charSequence: CharSequence,
    other: CharSequence,
    ignoreCase: Boolean = false,
    message: String? = null,
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

fun <T> assertNotContains(
    collection: Collection<T>,
    element: T,
    message: String? = null,
) {
    asserter.assertTrue(
        {
            messagePrefix(message) +
                    "Expected the collection not to contain the element.\n" +
                    "Collection <$collection>, element <$element>."
        },
        !collection.contains(element)
    )
}
