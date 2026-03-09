package domain.synthesis.results

import domain.shared.behavior.AttractivenessModel
import domain.shared.datastructure.schedule.Activity
import domain.shared.enums.ActivityType
import domain.shared.location.Location
import domain.shared.location.Zone
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.RawSurveyInfo
import domain.synthesis.behavior.SurveyInfo
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.behavior.SynthesisCar
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.employment
import domain.synthesis.behavior.numberOfDrivingLicences
import java.nio.file.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createDirectories

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
        path.parent.createDirectories() // Ensure that the necessary parent directories exist.
        path.bufferedWriter().use { writer ->
            writer.write(header.joinToString(separator = ";") { it })
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
object LegacyActivityOutput : CSVOutput<Pair<SynthesisPerson<*>, Collection<Activity>>> {
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

    override fun convert(element: Pair<SynthesisPerson<*>, Collection<Activity>>): String {
        val (person, activities) = element
        var tournr = 0
        return activities.joinToString(separator = "\n") { activity: Activity ->
            activity.run {
                toCSV(
                    person.personId,
                    type.code,
                    -1, // TODO this was originally the observed trip duration from actitopp, which we cannot access
                    startTime.minutesSinceStart,
                    duration.inWholeMinutes,
                    tournr++,
                    "false", // TODO this is an actitopp information, that does not pass through
                    "false", // TODO this is also actitopp specific.
                )
            }
        }
    }
}

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object LegacyCarOutput : CSVOutput<SynthesisCar> {
    override val header: List<String> = listOf(
        "ownerId",
        "mainUserId",
        "personalUserId",
        "carType",
        "car attributes"
    )

    @Suppress("MagicNumber")
    override fun convert(element: SynthesisCar): String {
        return element.run {
            toCSV(
                mainUser?.household?.id ?: "Null",
                mainUser?.personId ?: "-1",
                mainUser?.personId ?: "-1",
                this.engine.type.asText,
                id,
                "0", // TODO verify that this is acurraty
                mainUser?.household?.location ?: "Null",
                segment,
                seats,
                0.0, // TODO this appears to be a fixed value.
                1.0, // TODO this appears to be a fixed value.
                1000.0, // TODO this appears to be a fixed value.

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
object LegacyFixedDestinationOutput : CSVOutput<FixedDestinationElements> {
    override val header: List<String> = listOf(
        "personOid",
        "personNumber", // Thats the number of the person in the household, no Idea why anyone would ever need that.
        "householdOid",
        "householdYear",
        "householdNumber",
        "activityType",
        "zoneId",
        "location",
        "locationX",
        "locationY"
    )

    @Suppress("MagicNumber")
    override fun convert(element: FixedDestinationElements): String {
        return element.run {
            toCSV(
                person.personId,
                -1, // Dummy value for dummy output: This is the number in the household.
                person.household.id,
                1970, // Dummy value for dumb output household year taken from survey data.
                -1, // Dummy value for dumb output: household ID from the survey data
                activityType.description,
                location.zone?.id?.value ?: "NULL",
                location.legacyStringRepresentation(),
                location.coordinate.longitudeDegrees,
                location.coordinate.latitudeDegrees

            )
        }
    }
}

object SurveyHouseholdOutput : CSVOutput<ISurveyHousehold<out SurveyInfo>> {
    override val header: List<String> = listOf("nominalSize", "numberOfMinors", "income")

    @Suppress("MagicNumber")
    override fun convert(element: ISurveyHousehold<out SurveyInfo>): String {
        return element.run {
            toCSV(
                members.size,
                members.count { it.age < 18 },
                income.inEuros
            )
        }
    }
}

object ModernizedHouseholdOutput : CSVOutput<SynthesisHousehold<out SurveyInfo>> {
    override val header: List<String> = listOf(
        "id",
        "zoneId",
        "surveyHouseholdId",
        "location",
        "longitudeDegrees",
        "latitudeDegrees",
        "amountOfCars",
        "economicStatusCode"
    )

    override fun convert(element: SynthesisHousehold<out SurveyInfo>): String {
        return element.run {
            toCSV(
                id,
                location.zone?.id?.value ?: "NULL",
                surveyHouseholdId,
                location,
                location.coordinate.longitudeDegrees,
                location.coordinate.latitudeDegrees,
//                "TODO nomberofnotsimulatdchildren",
                amountOfCars,
//                "TODO incomeclass",
                economicStatus.code,
            )
        }
    }
}

// Sorry detekt, householdId and other strings may occur more often.
@Suppress("StringLiteralDuplication", "MagicNumber")
object LegacyHouseholdOutput : CSVOutput<SynthesisHousehold<out SurveyInfo>> {

    override val header: List<String> = listOf(
        "householdId",
        "year",
        "householdNumber",
        "domCode",
        "type",
        "homeZone", // Why in the everloving F is homezone using the internal enumeration of zones: 6113 -> 0, 6114 ->1
        "actualHomeZone",
        "homeLocation",
        "homeX",
        "homeY",
        "numberOfNotSimulatedChildren",
        "totalNumberOfCars",
        "incomeClass",
        "economicalStatus",
        "canChargePrivately"
    ) + SurveyHouseholdOutput.header

    override fun convert(element: SynthesisHousehold<out SurveyInfo>): String {
        return element.run {
            toCSV(
                id,
                1970, // Dummy value: Originally the year from Survey Info. Now useless.
                -13379001, // Dummy value: Originally the ID in the Survey Info.
                -1, // Ok, here I am lost, I have absolutely no idea what "domcode" is supposed to be.
                -1, // The household type. Again taken from survey data. Again crazy that this exists as an int field.
                location.zone?.legacyId ?: "NULL", // I HATE OLD MOBITOPP
                location.zone?.id?.value ?: "NULL",
                location.legacyStringRepresentation(),
                location.coordinate.longitudeDegrees,
                location.coordinate.latitudeDegrees,
                -1, // ActiTopp once cared about the number of childern, but it is entirely irrelevant
                amountOfCars,
                5, // I would assume that this is the encoding of the income based on some classes, but used it is not.
                economicStatus.code,
                "true", // Everyone can charge privately. Why this field was added to the general output / No one knows

            )
        } + ";" + SurveyHouseholdOutput.convert(element)
    }
}

data class OpportunityOutput(
    val location: Location,
    val attractivenessModel: AttractivenessModel,
    val activityType: ActivityType,
)

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object LegacyOpportunitiesOutput : CSVOutput<OpportunityOutput> {
    override val header: List<String> =
        listOf("zoneId", "activityType", "location", "attractivity", "locationX", "locationY")

    override fun convert(element: OpportunityOutput): String {
        return element.run {
            toCSV(
                location.zone?.id?.value ?: -1,
                activityType,
                location.legacyStringRepresentation(),
                attractivenessModel.nullableAttractiveness(location.zone, activityType),
                location.coordinate.latitudeDegrees,
                location.coordinate.longitudeDegrees

            )
        }
    }
}

object SurveyPersonOutput : CSVOutput<SurveyPerson<out RawSurveyInfo>> {
    override val header: List<String> = listOf(
        "personId",
        "age",
        "gender",
        "householdIncome",
        "hasBike",
        "hasLicence"
    )

    override fun convert(element: SurveyPerson<out RawSurveyInfo>): String {
        return element.run {
            toCSV(
                personId,
                age,
                sex,
                information.householdIncome.inEuros,
                information.hasBicycle,
                information.hasLicence
            )
        }
    }
}

@Suppress("StringLiteralDuplication") // Sorry detekt, householdId and other strings may occur more often.
object LegacyPersonOutput : CSVOutput<SynthesisPerson<out RawSurveyInfo>> {
    private const val SURVEY_DUMMY = "BIKE=0.0,CAR=0.0,PASSENGER=0.0,PEDESTRIAN=0.0,PUBLICTRANSPORT=0.0"
    override val header: List<String> = SurveyPersonOutput.header + listOf(

        "personNumber",
        "householdId",
        "employment",
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

    @Suppress("MagicNumber")
    override fun convert(element: SynthesisPerson<out RawSurveyInfo>): String {
        val first = SurveyPersonOutput.convert(element)
        val second = element.run {
            toCSV(
                -1, // Dummy value. person Number is not a useful attribute
                household.id,

                employment,
                household.amountOfCars > 0,
                household.amountOfCars <= household.numberOfDrivingLicences,
                hasTransitPass,
                information.hasLicence,
                SURVEY_DUMMY,
                SURVEY_DUMMY,
                0.5,
                "NEVER",
                this.getSharingMemberships()

            )
        }
        return "$first;$second"
    }
}

fun toCSV(vararg elements: Any): String {
    return elements.joinToString(";") { it.toString() }
}
