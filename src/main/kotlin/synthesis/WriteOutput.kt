package synthesis

import datastructure.Activity
import domain.data.Zone
import domain.enums.ActivityType
import domain.location.Location
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson
import usecases.AttractivenessModel
import usecases.steps.toCSV
import java.nio.file.Path
import kotlin.io.path.writeText

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
    val header: List<String>
    fun convert(element: T): String
    fun generateCSVString(elements: Collection<T>) =
        header.joinToString(separator = ";", postfix = "\n") { it } + elements.joinToString(separator = "\n") {
            convert(
                it
            )
        }

    fun writeCSVToFile(path: Path, elements: Collection<T>) {
        path.writeText(generateCSVString(elements))
    }
}

// TODO the synthesis activity will probably not match with the simulation activity.
object ActivityOutput : CSVOutput<Activity> {
    override val header: List<String> = listOf(
        "personId",
        "activityType",
        "observedTripDuration",
        "startTime",
        "duration",
        "tournr",
        "isMainActivity",
        "isSupertour"
    )

    override fun convert(element: Activity): String {
        return element.run {
            toCSV(
                "TODO personID",
                type.encode(),
                "TODO observedTripDuration",
                startTime,
                duration,
                "TODO tournr",
                "TODO isMainActivity",
                "TODO isSupertour"
            )
        }
    }
}

object CarOutput : CSVOutput<SynthesisCar> {
    override val header: List<String> = listOf("ownerId", "mainUserId", "personalUserId", "carType", "car attributes")

    override fun convert(element: SynthesisCar): String {
        return element.run {
            toCSV(
                mainUser?.household?.id ?: "Null",
                mainUser?.personId ?: "Null",
                mainUser?.personId ?: "Null",
                id, // TODO verify that this is always the car ID
                "0", // TODO verify that this is acurraty
                engine.type,
                location,
                segment,
                seats,
                "TODO always 0.0?",
                "TODO always 1.0?",
                "TODO always 1000?",

            )
        }
    }
}

data class FixedDestinationElements(
    val person: SynthesisPerson<*>,
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
            toCSV(
                person.personId,
                "TODO personNumber",
                person.household.id,
                "TODO household Year",
                "TODO household number",
                activityType.description,
                location.zone?.id ?: "NULL",
                location.legacyStringRepresentation(),
                location.coordinate.longitudeDegrees,
                location.coordinate.latitudeDegrees

            )
        }
    }
}

object HouseholdOutput : CSVOutput<SynthesisHousehold<out SurveyInfo>> {
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

    override fun convert(element: SynthesisHousehold<out SurveyInfo>): String {
        return element.run {
            toCSV(
                id,
                "TODO year",
                "TODO householdNumber",
                members.size,
                "TODO domcode",
                "TODO type",
                location.zone ?: "NULL",
                location,
                location.coordinate.longitudeDegrees,
                location.coordinate.latitudeDegrees,
                members.count { it.age < 18 },
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

data class OpportunityOutput(
    val location: Location,
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType
)

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

object PersonOutput : CSVOutput<SynthesisPerson<out SurveyInfo>> {
    override val header: List<String> = listOf(
        "personId",
        "personNumber",
        "householdId",
        "age",
        "employment",
        "gender",
        "graduation",
        "income",
        "hasBike",
        "hasAccessToCar",
        "hasPersonalCar",
        "hasCommuterTicket",
        "hasLicense",
        "preferencesSurvey",
        "preferencesSimulation",
        "eMobilityAcceptance",
        "chargingInfluencesDestinationChoice",
        "mobilityProviderCustomership"

    )

    override fun convert(element: SynthesisPerson<out SurveyInfo>): String {
        return element.run {
            toCSV(
                personId,
                "TODO personNumber",
                household.id,
                age,
                employment,
                sex,
                "TODO graduation",
                "TODO income",
                "TODO hasBike",
                "TODO hasAccessToCar",
                hasTransitPass,
                "TODO preferencesSurvey",
                "TODO preferencesSimulation",
                "TODO emobilityAcceptance",
                "TODO chargingInfluencesDestiantionChoice",
                "TODO mobilityProviderCustomership"
            )
        }
    }
}
