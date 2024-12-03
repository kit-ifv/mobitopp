package synthesis

import domain.enums.ActivityType
import domain.location.Location
import org.jetbrains.kotlinx.dataframe.io.CSV

interface CSVOutput<T> {
    val header: List<String>
    fun convert(element: T): String
    fun toCSV(elements: Collection<T>) = header.joinToString { it } + elements.joinToString { convert(it) }
}


data class FixedDestinationElements(
    val household: SynthesisHouseholdBuilder,
    val person: SurveyPerson,
    val activityType: ActivityType,
    val location: Location
)

object FixedDestinationOutput : CSVOutput<FixedDestinationElements> {
    override val header: List<String> = listOf(
        "personOid",
        "personNumber",
        "householdOid",
        "householdYear",
        "householdNumber",
        "activityType",
        "zoneId",
        "location",
        "locationX",
        "locationY"
    )

    override fun convert(element: FixedDestinationElements): String {
        return element.run {
            usecases.steps.toCSV(
                person.id,
                "TODO personNumber",
                household.id,
                "TODO household Year",
                "TODO household number",
                activityType.description,
                location.zone?.id ?: "NULL",
                location,
                location.coordinate.longitudeDegrees,
                location.coordinate.latitudeDegrees

            )
        }
    }
}

object HouseholdOutput : CSVOutput<SynthesisHouseholdBuilder> {
    override val header: List<String> = listOf(
        "householdId",
        "year",
        "householdNumber",
        "nominalSize",
        "domCode",
        "type",
        "homeZone",
        "homeLocation",
        "homeX",
        "homeY",
        "numberOfMinors",
        "numberOfNotSimulatedChildren",
        "totalNumberOfCars",
        "income",
        "incomeClass",
        "economicalStatus",
        "canChargePrivately"
    )

    override fun convert(element: SynthesisHouseholdBuilder): String {
        return element.run {
            usecases.steps.toCSV(
                id,
                "TODO year",
                "TODO householdNumber",
                members.size,
                "TODO domcode",
                "TODO type",
                location.zone?:"NULL",
                location,
                location.coordinate.longitudeDegrees,
                location.coordinate.latitudeDegrees,
                members.count{it.age < 18},
                "TODO nomberofnotsimulatdchildren",
                amountOfCars,
                income,
                "TODO incomeclass",
                economicStatus,
                "TODO can charge privately"

            )
        }
    }

}