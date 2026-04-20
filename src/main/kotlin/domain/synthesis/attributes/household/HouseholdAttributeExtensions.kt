package domain.synthesis.attributes.household

import domain.synthesis.attributes.person.HasAge
import domain.synthesis.attributes.person.HasLicence
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.attributes.person.hasLicence
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.MinimalistPerson

@Suppress("MagicNumber") // These magic numbers are ok
val MinimalistHousehold<*, HasAge>.numberOfAdults get() = members.count { it.attributes.age >= 18 }

@Suppress("MagicNumber") // These magic numbers are ok
val MinimalistHousehold<*, HasAge>.numberOfMinors get() = members.count { it.attributes.age < 18 }

val <T : HasLicence> MinimalistHousehold<*, T>.licenceHolders
    get(): List<MinimalistPerson<T>> {
        return members.filter { it.hasLicence }
    }

@Suppress("MagicNumber")
val <T : MinimumPersonAttributes> MinimalistHousehold<*, T>.adults
    get(): List<MinimalistPerson<T>> {
        return members.filter { it.attributes.age >= 18 }
    }

val <T : HasLicence> MinimalistHousehold<*, T>.numberOfDrivingLicences
    get(): Int {
        return members.count { it.hasLicence }
    }
