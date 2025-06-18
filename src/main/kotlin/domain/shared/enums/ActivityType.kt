package domain.shared.enums

import utils.Encodable

/**
 * An activity type describes which kind of activity is executed by a person.
 * There is no general definition of activity types:
 * hence each project can provide a custom definition of activity types.
 */
interface ActivityType : Encodable {
    override val description: String

    companion object {
        val UNKNOWN = object : ActivityType {
            override val description: String = "Unknown"

            @Suppress("MagicNumber")
            override val code: Int = -2
        }
    }
}
