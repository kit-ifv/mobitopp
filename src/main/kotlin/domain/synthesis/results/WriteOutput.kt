package domain.synthesis.results

import domain.shared.behavior.AttractivenessModel
import domain.shared.datastructure.schedule.Activity
import domain.shared.enums.ActivityType
import domain.shared.location.Location
import domain.shared.location.Zone
import domain.synthesis.behavior.RawSurveyInfo
import domain.synthesis.behavior.SurveyInfo
import domain.synthesis.behavior.SynthesisCar
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.employment
import java.nio.file.Path
import kotlin.io.path.bufferedWriter

//
// Extension functions on existing classes.
//

/**
 * Converts the Location to the standard representation found in legacy mobitopp input files which is the format
 * (lat, lon: roadId, accessShare)
 */
fun Location.legacyStringRepresentation(): String =
    "(${coordinate.latitudeDegrees}, ${coordinate.longitudeDegrees}: ${roadAccess?.roadId}, ${roadAccess?.position})"

/**
 * Extend the AttractivenessModel returning 0.0 for the attractiveness when the zone is null
 */
fun AttractivenessModel.nullableAttractiveness(zone: Zone?, activityType: ActivityType): Double {
    return zone?.let { attractivenessFor(it.id, activityType) } ?: 0.0
}
// End extension functions

interface CSVOutput<T> {
    // TODO move to utils, maybe use composition of column converters over interface implementation,
    //  buffered writing might be required if string gets large!

    val header: List<String>
    fun convert(element: T): String
    fun generateCSVString(elements: Collection<T>) =
        header.joinToString(separator = ";", postfix = "\n") { it } + elements.joinToString(separator = "\n") {
            convert(
                it
            )
        }

    fun writeCSVToFile(path: Path, elements: Collection<T>) {
        path.bufferedWriter().use { writer ->
            writer.write(header.joinToString(separator = ";",) { it })
            writer.newLine()
            elements.forEach { element ->
                writer.write(convert(element))
                writer.newLine()
            }
        }
    }
}

// TODO the synthesis activity will probably not match with the simulation activity.
@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object ActivityOutput : CSVOutput<Pair<SynthesisPerson<*>, Collection<Activity>>> {
    override val header: List<String> = listOf(
        "personId",
        "activityType",
//        "observedTripDuration",
        "startTime",
        "duration",
//        "tournr",
//        "isMainActivity",
//        "isSupertour"
    )

    override fun convert(element: Pair<SynthesisPerson<*>, Collection<Activity>>): String {
        val (person, activities) = element
        return activities.joinToString(separator = "\n") { activity: Activity ->
            activity.run {
                toCSV(
                    person.personId,
                    type.code,
//                    "TODO observedTripDuration",
                    startTime,
                    duration,
//                    "TODO tournr",
//                    "TODO isMainActivity",
//                    "TODO isSupertour"
                )
            }
        }
    }
}

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object CarOutput : CSVOutput<SynthesisCar> {
    override val header: List<String> = listOf(
        "ownerId",
        "mainUserId",
        "personalUserId",
        "carType",
        "car attributes"
    )

    override fun convert(element: SynthesisCar): String {
        return element.run {
            toCSV(
                mainUser?.household?.id ?: "Null",
                mainUser?.personId ?: "Null",
                mainUser?.personId ?: "Null",
                id, // TODO verify that this is always the car ID
                "0", // TODO verify that this is acurraty
                engine.type,
                mainUser?.household?.location ?: "Null",
                segment,
                seats,
//                "TODO always 0.0?",
//                "TODO always 1.0?",
//                "TODO always 1000?",

            )
        }
    }
}

data class FixedDestinationElements(
    val person: SynthesisPerson<*>,
    val activityType: ActivityType,
    val location: Location,
)

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object FixedDestinationOutput : CSVOutput<FixedDestinationElements> {
    override val header: List<String> = listOf(
        "personOid",
//        "personNumber",
        "householdOid",
//        "householdYear",
//        "householdNumber",
        "activityType",
        "zoneId",
        "location",
        "locationX",
        "locationY"
    )

    override fun convert(element: FixedDestinationElements): String {
        return element.run {
            toCSV(
                person.personId,
//                "TODO personNumber",
                person.household.id,
//                "TODO household Year",
//                "TODO household number",
                activityType.description,
                location.zone?.id ?: "NULL",
                location.legacyStringRepresentation(),
                location.coordinate.longitudeDegrees,
                location.coordinate.latitudeDegrees

            )
        }
    }
}

// Sorry detekt, householdId and other strings may occur more often.
@Suppress("StringLiteralDuplication", "MagicNumber")
object HouseholdOutput : CSVOutput<SynthesisHousehold<out SurveyInfo>> {

    override val header: List<String> = listOf(
        "householdId",
//        "year",
//        "householdNumber",
        "nominalSize",
//        "domCode",
//        "type",
        "homeZone",
        "homeLocation",
        "homeX",
        "homeY",
        "numberOfMinors",
//        "numberOfNotSimulatedChildren",
        "totalNumberOfCars",
        "income",
//        "incomeClass",
        "economicalStatus",
//        "canChargePrivately"
    )

    override fun convert(element: SynthesisHousehold<out SurveyInfo>): String {
        return element.run {
            toCSV(
                id,
//                "TODO year",
//                "TODO householdNumber",
                members.size,
//                "TODO domcode",
//                "TODO type",
                location.zone ?: "NULL",
                location,
                location.coordinate.longitudeDegrees,
                location.coordinate.latitudeDegrees,
                members.count { it.age < 18 },
//                "TODO nomberofnotsimulatdchildren",
                amountOfCars,
                income,
//                "TODO incomeclass",
                economicStatus,
//                "TODO can charge privately"

            )
        }
    }
}

data class OpportunityOutput(
    val location: Location,
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType,
)

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object OpportunitiesOutput : CSVOutput<OpportunityOutput> {
    override val header: List<String> =
        listOf("zoneId", "activityType", "location", "attractivity", "locationX", "locationY")

    override fun convert(element: OpportunityOutput): String {
        return element.run {
            toCSV(
                location.zone?.id ?: -1,
                activityType,
                location.legacyStringRepresentation(),
                attractivenessModel.nullableAttractiveness(location.zone, activityType),
                location.coordinate.latitudeDegrees,
                location.coordinate.longitudeDegrees

            )
        }
    }
}

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object PersonOutput : CSVOutput<SynthesisPerson<out RawSurveyInfo>> {
    override val header: List<String> = listOf(
        "personId",
//        "personNumber",
        "householdId",
        "age",
        "employment",
        "gender",
//        "graduation",
//        "income",
        "hasBike",
//        "hasAccessToCar",
//        "hasPersonalCar",
        "hasCommuterTicket",
        "hasLicense",
//        "preferencesSurvey",
//        "preferencesSimulation",
//        "eMobilityAcceptance",
//        "chargingInfluencesDestinationChoice",
//        "mobilityProviderCustomership"

    )

    override fun convert(element: SynthesisPerson<out RawSurveyInfo>): String {
        element.info
        return element.run {
            toCSV(
                personId,
                "TODO personNumber",
                household.id,
                age,
                employment,
                sex,
//                this.info.graduation TODO this is not in
                this.info.householdIncome,
                this.info.hasBicycle,
//                "TODO hasAccessToCar",
                hasTransitPass,
                this.info.hasLicence,
//                "TODO preferencesSurvey",
//                "TODO preferencesSimulation",
//                "TODO emobilityAcceptance",
//                "TODO chargingInfluencesDestiantionChoice",
//                "TODO mobilityProviderCustomership"
            )
        }
    }
}

fun toCSV(vararg elements: Any): String {
    return elements.joinToString(";") { it.toString() }
}
