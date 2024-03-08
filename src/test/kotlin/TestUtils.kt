import kotlin.test.asserter

internal fun messagePrefix(message: String?) = if (message == null) "" else "$message. "

fun assertNotContains(
    charSequence: CharSequence,
    other: CharSequence,
    ignoreCase: Boolean = false,
    message: String? = null
) {
    asserter.assertTrue(
        { messagePrefix(message) +
                "Expected the char sequence not to contain the substring.\n" +
                "CharSequence <$charSequence>, substring <$other>, ignoreCase <$ignoreCase>."
        },
        !charSequence.contains(other, ignoreCase)
    )
}

fun assertEmpty(charSequence: CharSequence, message: String? = null) {
    asserter.assertTrue(
        { messagePrefix(message) +
                "Expected the char sequence to be empty.\n" +
                "CharSequence <$charSequence>."
        },
        charSequence.isEmpty()
    )
}
