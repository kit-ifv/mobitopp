@file:Suppress("TooManyFunctions")

package application.steps.results

import core.modelsteps.Resource
import core.modelsteps.asResource
import core.results.plots.PlotDataTransformationBuilder
import core.results.plots.PlotterBuilder
import core.results.plots.data.Ordering
import core.results.plots.forData
import core.results.plots.normalizeByGroup
import core.results.plots.normalizeByX
import domain.shared.behavior.ChoiceModelModes
import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.enums.ActivityType
import domain.shared.enums.Mode
import domain.shared.enums.areatype.RegioStaR7
import domain.shared.location.Metrics
import domain.simulation.results.AgentResultsContext
import domain.simulation.results.PersonLeg
import domain.simulation.results.distance
import domain.simulation.results.duration
import domain.simulation.results.personLegs
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import units.Distance
import utils.collections.BaseBin
import utils.collections.Bin
import utils.collections.OpenBin
import utils.collections.mapToBins
import utils.csv.CsvReader
import utils.csv.Row
import utils.csv.decode
import utils.csv.double
import utils.csv.int
import utils.csv.kilometers
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.pathString
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun compareWithMid(csv: Path) =
    CsvReader.of(csv).rows().toList().asResource("MId comparison data", csv.pathString)

fun Resource<Row>.legs(
    purposes: ChoiceModelPurposes,
    modes: ChoiceModelModes,
) = elements.map {
    MidLegRow(purposes, modes, it)
}.asResource(name, source)

public data class MidLegRow(
    private val purposes: ChoiceModelPurposes,
    private val modes: ChoiceModelModes,
    private val row: Row
) {

    companion object {
        val distBinCache: MutableMap<String, Bin<Double>> = mutableMapOf()
        val durBinCache: MutableMap<String, Bin<Int>> = mutableMapOf()
        val ageBinCache: MutableMap<String, Bin<Int>> = mutableMapOf()
    }

    val distanceBin: Bin<Double>
        get() = row("travelDistanceCategories") { key: String ->
            distBinCache.getOrPut(key) {
                if ("-" in key) {
                    key.split("-").let {
                        BaseBin(it[0].toDouble(), it[1].toDouble())
                    }
                } else {
                    OpenBin(
                        key.replace("+", "").toDouble()
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
                        key.replace("+", "").toInt()
                    )
                }
            }
        }

    val duration: Duration
        get() = row.int("travelTime").minutes

    val ageBin: Bin<Int>
        get() = row("age") { key: String ->
            ageBinCache.getOrPut(key) {
                key.split(";").let {
                    BaseBin(it[0].toInt(), it[1].toInt())
                }
            }
        }

    val sex: Sex
        get() = Sex.valueOf(row("gender").uppercase())

    val economicStatus: EconomicStatus
        get() = row("economicStatus").parseEconomicStatus()

    val employment: Employment
        get() = row("occupation").parseEmployment()

    val hasLicense: Boolean
        get() = row("driversLicense").parseBool()

    val hasEBike: Boolean
        get() = row("hasEBike").parseBool()

    val hasCommuterTicket: Boolean
        get() = row("hasTransitPass").parseBool()

    val activityType: ActivityType
        get() = row("purpose").parseActivityType(purposes)

    val mode: Mode
        get() = row("modeChoice").parseMode(modes)

    val regioStaR7: RegioStaR7
        get() = row.decode("regioStar7", RegioStaR7)

    val isPlanningArea: Boolean
        get() = row("planningArea").parseBool()

    val absolute: Double
        get() = row.double("W_HOCH")

    val relative: Double
        get() = row.double("W_GEW")
}

private fun String.parseBool(): Boolean = when (this.lowercase()) {
    "yes", "true", "y", "t", "1" -> true
    "no", "false", "n", "f", "0" -> false
    else -> error(
        "Invalid boolean string: $this\n." +
            "Expected 'yes', 'true', 'y', 't', '1' for true or 'no', 'false', 'n', 'f', '0' for false."
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
    "education" -> purposes.education
    "leisure" -> purposes.leisure
    "privateBusiness" -> purposes.privateBusiness
    "service" -> purposes.service
    "shopping" -> purposes.shopping
    "work" -> purposes.work
    else -> error(
        "Invalid purpose string: $this. " +
            "Expected: 'NA', 'business', 'education', 'leisure', 'privateBusiness', 'service', 'shopping' or 'work'."
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

@Suppress("LongParameterList")
fun <G> AgentResultsContext.midComparisonPlotForLegs(
    midCsv: Path,
    purposes: ChoiceModelPurposes,
    modes: ChoiceModelModes,
    impedance: Metrics,
    legFilter: (PersonLeg) -> Boolean = { true },
    legGroup: (PersonLeg) -> G,
    midGroup: (MidLegRow) -> G,
    normalize: Boolean = true
) = MidComparisonLegPlotBuilder(
    this, midCsv, purposes, modes, impedance, legFilter, legGroup, midGroup, normalize
)

@Suppress("LongParameterList")
class MidComparisonLegPlotBuilder<G>(
    context: AgentResultsContext,
    midCsv: Path,
    purposes: ChoiceModelPurposes,
    modes: ChoiceModelModes,
    private val impedance: Metrics,
    legFilter: (PersonLeg) -> Boolean = { true },
    legGroup: (PersonLeg) -> G,
    midGroup: (MidLegRow) -> G,
    private val normalize: Boolean = true,
) {

    companion object {
        private val midLegCache: MutableMap<String, List<MidLegRow>> = mutableMapOf()
    }

    private val midLegs: List<MidLegRow> =
        midLegCache.computeIfAbsent(midCsv.absolute().pathString) {
            compareWithMid(midCsv).legs(purposes, modes).elements.toList()
        }

    private val dataBuilder = forData {
        context.personLegs.filter(legFilter)
    }.groupBy {
        legGroup(it)
    }

    private val comparisonBuilder = forData {
        midLegs
    }.groupBy {
        midGroup(it)
    }

    fun overDistance(): PlotterBuilder<G, Bin<Double>, Double> {
        midLegs.forEach { it.distanceBin }
        val distanceBins = MidLegRow.distBinCache.values.toList()

        val dataCount = dataBuilder.count {
            it.distance(impedance).inKilometers.mapToBins(distanceBins)
        }.let {
            if (normalize) { it.normalizeByX() } else { it.normalizeByGroup() }
        }.sortAndFill(0.0)

        val compCount = comparisonBuilder.plotSumOf {
            if (normalize) { it.absolute } else { it.relative }
        }.over {
            it.distanceBin
        }.let {
            if (normalize) { it.normalizeByX() } else { it.normalizeByGroup() }
        }.sortAndFill(0.0)

        return dataCount.compareTo {
            compCount
        }
    }

    fun overDuration(): PlotterBuilder<G, Bin<Int>, Double> {
        midLegs.forEach { it.durationBin }
        val durationBins = MidLegRow.durBinCache.values.toList()

        val dataCount = dataBuilder.count {
            it.duration(impedance).inWholeMinutes.toInt().mapToBins(durationBins)
        }.let {
            if (normalize) { it.normalizeByX() } else { it.normalizeByGroup() }
        }.sortAndFill(0.0)

        val compCount = comparisonBuilder.plotSumOf {
            if (normalize) { it.absolute } else { it.relative }
        }.over {
            it.durationBin
        }.let {
            if (normalize) { it.normalizeByX() } else { it.normalizeByGroup() }
        }.sortAndFill(0.0)

        return dataCount.compareTo {
            compCount
        }
    }

    private fun <B : Comparable<B>, T : Number> PlotDataTransformationBuilder<G, Bin<B>, T>.sortAndFill(default: T) =
        this.fillMissingXValues {
            default
        }.sortX {
            Ordering.Ascending()
        }.sortGroups {
            Ordering.AscendingBy { it.hashCode() }
        }
}

fun ActivityType.simplifyMID(purposes: ChoiceModelPurposes) =
    this.simplifyEducationMID(purposes)
        .simplifyLeisureMID(purposes)
        .simplifyShoppingMID(purposes)
        .simplifyBusinessMID(purposes)

fun ActivityType.simplifyEducationMID(purposes: ChoiceModelPurposes) =
    if (this in purposes.educationTypes) {
        purposes.education
    } else {
        this
    }

fun ActivityType.simplifyLeisureMID(purposes: ChoiceModelPurposes) =
    if (this in purposes.leisureTypes) {
        purposes.leisure
    } else {
        this
    }

fun ActivityType.simplifyShoppingMID(purposes: ChoiceModelPurposes) =
    if (this in purposes.shoppingTypes) {
        purposes.shopping
    } else {
        this
    }

fun ActivityType.simplifyBusinessMID(purposes: ChoiceModelPurposes) =
    if (this in purposes.businessTypes) {
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
    Employment.HOMEKEEPER -> this

    Employment.UNEMPLOYED,
    Employment.EDUCATION,
    Employment.INFANT,
    Employment.NONE,
    Employment.UNKNOWN -> Employment.UNKNOWN
}
