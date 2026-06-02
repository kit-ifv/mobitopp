@file:Suppress("TooManyFunctions", "MagicNumber")

package application.steps.results

import core.modelsteps.resources.Resource
import core.modelsteps.resources.asResource
import domain.shared.behavior.ChoiceModelModes
import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.enums.ActivityType
import domain.shared.enums.Mode
import domain.shared.enums.areatype.RegioStaR7
import domain.synthesis.data.household.EconomicStatus
import domain.synthesis.data.household.IHousehold
import domain.synthesis.data.person.Employment
import domain.synthesis.data.person.Graduation
import domain.synthesis.data.person.IPerson
import domain.synthesis.data.person.Sex
import edu.kit.ifv.units.Distance
import utils.collections.BaseBin
import utils.collections.Bin
import utils.collections.OpenBin
import utils.csv.CsvReader
import utils.csv.Row
import utils.csv.decode
import utils.csv.double
import utils.csv.int
import utils.csv.kilometers
import utils.units.AbsoluteTime
import utils.units.sinceStart
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun compareWithMid(csv: Path) = CsvReader.of(
    csv,
    separator = ",",
).rows().toList().asResource("MID comparison data", csv.pathString)

val Resource<Row>.persons get() = elements.map {
    MidPersonRow(it)
}.asResource(name, source)

fun Resource<Row>.legs(purposes: ChoiceModelPurposes, modes: ChoiceModelModes) = elements.map {
    MidLegRow(purposes, modes, it)
}.asResource(name, source)

private const val EDUCATION = "education"

@Suppress("ComplexInterface")
interface MidRow {

    companion object {
        val ageBinCache: MutableMap<String, Bin<Int>> = mutableMapOf()
    }

    val row: Row

    val relative: Double

    val absolute: Double

    val householdSize: String
        get() = row("hhSize")

    val hhNumberOfCars: String
        get() = row("hhNumberOfCars")

    val sex: Sex
        get() = Sex.valueOf(row("gender").uppercase())

    val age: Int
        get() = row.int("age")

    val ageBin: Bin<Int>
        get() = row("age") { key: String ->
            ageBinCache.getOrPut(key) {
                key.split(",").let {
                    BaseBin(it[0].toInt(), it[1].toInt() + 1)
                }
            }
        }

    val economicStatus: EconomicStatus
        get() = row("economicStatus").parseEconomicStatus()

    val employment: Employment
        get() = row("occupation").parseEmployment()

    val education: String
        get() = row.int(EDUCATION).parseEducation()

    val hasLicense: Boolean
        get() = row("driversLicense").parseBool()

    // TODO car availability

    val isCarsharingMember: Boolean
        get() = row("carsharingMembership").parseBool()

    val hasEBike: Boolean
        get() = row("hasEBike").parseBool()

    val hasCommuterTicket: Boolean
        get() = row("hasTransitPass").parseBool()

    val regioStaR7: RegioStaR7
        get() = row.decode("regioStar7", RegioStaR7)

    val isPlanningArea: Boolean
        get() = row("planningArea").parseBool()
}

data class MidPersonRow(override val row: Row) : MidRow {

    override val relative: Double
        get() = row.double("P_GEW")

    override val absolute: Double
        get() = row.double("P_HOCH")
}

data class MidLegRow(
    private val purposes: ChoiceModelPurposes,
    private val modes: ChoiceModelModes,
    override val row: Row,
) : MidRow {

    companion object {
        val distBinCache: MutableMap<String, Bin<Double>> = mutableMapOf()
        val durBinCache: MutableMap<String, Bin<Int>> = mutableMapOf()
    }

    override val relative: Double
        get() = row.double("W_GEW")

    override val absolute: Double
        get() = row.double("W_HOCH")

    val distanceBin: Bin<Double>
        get() = row("travelDistanceCategories") { key: String ->
            distBinCache.getOrPut(key) {
                if ("-" in key) {
                    key.split("-").let {
                        BaseBin(it[0].toDouble(), it[1].toDouble())
                    }
                } else {
                    OpenBin(
                        key.replace("+", "").toDouble(),
                    )
                }
            }
        }

    val distance: Distance
        get() = row.kilometers("travelDistance")

    val durationBin: Bin<Int>
        get() = row("travelTimeCategories") { key: String ->
            durBinCache.getOrPut(key) {
                if ("-" in key) {
                    key.split("-").let {
                        BaseBin(it[0].toInt(), it[1].toInt())
                    }
                } else {
                    OpenBin(
                        key.replace("+", "").toInt(),
                    )
                }
            }
        }

    val duration: Duration
        get() = row.int("travelTime").minutes

    val activityType: ActivityType
        get() = row("purpose").parseActivityType(purposes)

    val mode: Mode
        get() = row("modeChoice").parseMode(modes)

    val tripStart: AbsoluteTime?
        get() = row("beginTrip").parseTime()

    val activityStart: AbsoluteTime?
        get() = row("beginActivity").parseTime()
}

private fun String.parseTime() = this.takeIf {
    ":" in it
}?.split(":")?.let {
    it[0].toInt().hours.sinceStart + it[1].toInt().minutes
} ?: 0.minutes.sinceStart

private fun String.parseBool(): Boolean = when (this.lowercase()) {
    "yes", "true", "y", "t", "1" -> true

    "na", "no", "false", "n", "f", "0" -> false

    else -> error(
        "Invalid boolean string: '$this'\n" +
            "Expected 'yes', 'true', 'y', 't', '1' for true or 'na', 'no', 'false', 'n', 'f', '0' for false.",
    )
}

private fun String.parseEconomicStatus() = when (this) {
    "average" -> EconomicStatus.MIDDLE
    "high" -> EconomicStatus.HIGH
    "low" -> EconomicStatus.LOW
    else -> error("Invalid economic status string: $this. Expected: 'heigh', 'average' or 'low'")
}

private fun String.parseEmployment() = when (this) {
    "NA" -> Employment.UNKNOWN
    "homekeeper" -> Employment.HOMEKEEPER
    "retired" -> Employment.RETIRED
    "student" -> Employment.STUDENT
    "working" -> Employment.FULLTIME
    else -> error("Invalid employment string: $this. Expected: 'NA', 'homekeeper', 'retired', 'student' or 'working'.")
}

private fun String.parseActivityType(purposes: ChoiceModelPurposes) = when (this) {
    "NA" -> purposes.undefined

    "business" -> purposes.business

    EDUCATION -> purposes.education

    "leisure" -> purposes.leisure

    "privateBusiness" -> purposes.privateBusiness

    "service" -> purposes.service

    "shopping" -> purposes.shopping

    "work" -> purposes.work

    "home" -> purposes.home

    else -> error(
        "Invalid purpose string: $this. " +
            "Expected: 'NA', 'business', 'education', 'leisure', 'privateBusiness', 'service', 'shopping' or 'work'.",
    )
}

private fun String.parseMode(modes: ChoiceModelModes) = when (this) {
    "bike" -> modes.bike
    "driver" -> modes.car
    "passenger" -> modes.passenger
    "transit" -> modes.publicTransport
    "walking" -> modes.pedestrian
    else -> error("Invalid mode string: $this. Expected: 'bike', 'driver', 'passenger', 'transit' or 'walking'.")
}

private fun Int.parseEducation(): String = when (this) {
    1 -> "no degree"
    2 -> "Real-/Hauptschulabschluss"
    3 -> "Abitur"
    4 -> "university degree"
    -99 -> "other"
    else -> "$this"
}

fun Graduation.toEducationMID(): String = when (this) {
    Graduation.UNDEFINED -> (-99).parseEducation()
    Graduation.OTHER -> (-99).parseEducation()
    Graduation.NOT_HIGH_SCHOOL -> 2.parseEducation()
    Graduation.HIGH_SCHOOL_GRADUATE -> 3.parseEducation()
    Graduation.SOME_COLLEGE_CREDIT_NO_DEGREE -> 1.parseEducation()
    Graduation.ASSOCIATE_TECHNICAL_SCHOOL_DEGREE -> 2.parseEducation()
    Graduation.BACHELOR_DEGREE -> 4.parseEducation()
    Graduation.MASTER_DEGREE -> 4.parseEducation()
}

fun ActivityType.simplifyMID(purposes: ChoiceModelPurposes) = this.simplifyEducationMID(purposes)
    .simplifyLeisureMID(purposes)
    .simplifyShoppingMID(purposes)
    .simplifyBusinessMID(purposes)

fun ActivityType.simplifyEducationMID(purposes: ChoiceModelPurposes) = if (this in purposes.educationTypes) {
    purposes.education
} else {
    this
}

fun ActivityType.simplifyLeisureMID(purposes: ChoiceModelPurposes) = if (this in purposes.leisureTypes) {
    purposes.leisure
} else {
    this
}

fun ActivityType.simplifyShoppingMID(purposes: ChoiceModelPurposes) = if (this in purposes.shoppingTypes) {
    purposes.shopping
} else {
    this
}

fun ActivityType.simplifyBusinessMID(purposes: ChoiceModelPurposes) = if (this in purposes.businessTypes) {
    purposes.business
} else {
    this
}

fun Employment.simplifyEmploymentMID() = when (this) {
    Employment.STUDENT_PRIMARY -> Employment.STUDENT

    Employment.STUDENT_SECONDARY -> Employment.STUDENT

    Employment.STUDENT_TERTIARY -> Employment.STUDENT

    Employment.PARTTIME -> Employment.FULLTIME

    Employment.MARGINAL -> Employment.FULLTIME

    Employment.FULLTIME,
    Employment.STUDENT,
    Employment.RETIRED,
    Employment.HOMEKEEPER,
    -> this

    Employment.UNEMPLOYED,
    Employment.EDUCATION,
    Employment.INFANT,
    Employment.NONE,
    Employment.UNKNOWN,
    -> Employment.UNKNOWN
}

fun EconomicStatus.simplifyMID() = when (this) {
    EconomicStatus.VERY_LOW -> EconomicStatus.LOW
    EconomicStatus.LOW -> EconomicStatus.LOW
    EconomicStatus.MIDDLE -> EconomicStatus.MIDDLE
    EconomicStatus.HIGH -> EconomicStatus.HIGH
    EconomicStatus.VERY_HIGH -> EconomicStatus.HIGH
}

fun IPerson.carOwnershipMID(): String = when (household.cars.size) {
    0 -> "0"
    1 -> "1"
    else -> "2+"
}

val IHousehold.householdSizeMID get() = members.size.let {
    if (it < 4) {
        "$it"
    } else {
        "4+"
    }
}
