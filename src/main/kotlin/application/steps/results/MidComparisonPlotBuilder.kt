package application.steps.results

import core.modelsteps.resources.Repository
import core.results.plots.PlotDataBuilderWithGrouping
import core.results.plots.PlotDataTransformationBuilder
import core.results.plots.PlotterBuilder
import core.results.plots.data.Ordering
import core.results.plots.forData
import core.results.plots.normalizeByGroup
import core.results.plots.normalizeByX
import domain.shared.behavior.ChoiceModelModes
import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.location.Impedance
import domain.simulation.agent.PersonAgent
import domain.synthesis.data.person.Employment
import domain.synthesis.data.household.IHousehold
import domain.synthesis.data.person.IPerson
import domain.synthesis.data.person.PersonId
import utils.collections.Bin
import utils.collections.mapToBins
import utils.units.AbsoluteTime
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.pathString
import kotlin.math.min
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Suppress("LongParameterList")
fun <G> midComparisonPlotForLegs(
    personAgents: Repository<PersonAgent, PersonId>,
    midCsv: Path,
    purposes: ChoiceModelPurposes,
    modes: ChoiceModelModes,
    impedance: Impedance,
    legFilter: (PersonLeg) -> Boolean = { true },
    rowFilter: (MidLegRow) -> Boolean,
    legGroup: (PersonLeg) -> G,
    midGroup: (MidLegRow) -> G,
    normalize: Boolean = true,
) = MidComparisonLegPlotBuilder(
    personAgents, midCsv, purposes, modes, impedance, legFilter, rowFilter, legGroup, midGroup, normalize,
)

@Suppress("LongParameterList")
fun <G> midComparisonPlotForPerson(
    personAgents: Repository<PersonAgent, PersonId>,
    midCsv: Path,
    personFilter: (IPerson) -> Boolean = { true },
    rowFilter: (MidPersonRow) -> Boolean,
    personGroup: (IPerson) -> G,
    midGroup: (MidPersonRow) -> G,
    normalize: Boolean = true,
) = MidComparisonPersonPlotBuilder(
    personAgents,
    midCsv,
    personFilter,
    rowFilter,
    personGroup,
    midGroup,
    normalize,
)

@Suppress("LongParameterList")
class MidComparisonPersonPlotBuilder<G>(
    private val personAgents: Repository<PersonAgent, PersonId>,
    midCsv: Path,
    personFilter: (IPerson) -> Boolean = { true },
    rowFilter: (MidPersonRow) -> Boolean,
    personGroup: (IPerson) -> G,
    midGroup: (MidPersonRow) -> G,
    private val normalize: Boolean = true,
) {

    private val persons: List<PersonAgent>
        get() = personAgents.elements.toList()

    companion object {
        private val midPersonCache: MutableMap<String, List<MidPersonRow>> = mutableMapOf()
    }

    private val midPersons: List<MidPersonRow> =
        midPersonCache.computeIfAbsent(midCsv.absolute().pathString) {
            compareWithMid(midCsv).persons.elements.toList()
        }

    private val dataBuilder = forData {
        persons.filter(personFilter)
    }.groupBy {
        personGroup(it)
    }

    private val comparisonBuilder = forData {
        midPersons.filter(rowFilter)
    }.groupBy {
        midGroup(it)
    }

    @Suppress("MagicNumber")
    fun overAge(): PlotterBuilder<G, Bin<Int>, Double> {
        midPersons.forEach { it.ageBin }
        val ageBins = MidRow.ageBinCache.values.toList()

        val dataCount = dataBuilder.count {
            min(100, it.age).mapToBins(ageBins)
        }.let {
            if (normalize) {
                it.normalizeByX()
            } else {
                it.normalizeByGroup()
            }
        }.sortAndFill(0.0)

        val compCount = comparisonBuilder.comparisonOver(normalize) { ageBin }

        return dataCount.compareTo { compCount }
    }

    fun <T : Comparable<T>> over(
        personAtt: IPerson.() -> T,
        midAtt: MidPersonRow.() -> T,
    ): PlotterBuilder<G, T, Double> {
        val dataCount = dataBuilder.count {
            it.personAtt()
        }.let {
            if (normalize) {
                it.normalizeByX()
            } else {
                it.normalizeByGroup()
            }
        }.sortAndFill(0.0)

        val compCount = comparisonBuilder.comparisonOver(normalize) { midAtt() }

        return dataCount.compareTo { compCount }
    }

    fun <T : Comparable<T>> overHousehold(householdAtt: IHousehold.() -> T, midAtt: MidPersonRow.() -> T) = over({
        household.householdAtt()
    }, midAtt)
}

@Suppress("LongParameterList")
class MidComparisonLegPlotBuilder<G>(
    private val personAgents: Repository<PersonAgent, PersonId>,
    midCsv: Path,
    purposes: ChoiceModelPurposes,
    modes: ChoiceModelModes,
    private val impedance: Impedance,
    legFilter: (PersonLeg) -> Boolean = { true },
    rowFilter: (MidLegRow) -> Boolean,
    legGroup: (PersonLeg) -> G,
    midGroup: (MidLegRow) -> G,
    private val normalize: Boolean = true,
) {

    private val persons: List<PersonAgent>
        get() = personAgents.elements.toList()

    companion object {
        private val midLegCache: MutableMap<String, List<MidLegRow>> = mutableMapOf()
    }

    private val midLegs: List<MidLegRow> =
        midLegCache.computeIfAbsent(midCsv.absolute().pathString) {
            compareWithMid(midCsv).legs(purposes, modes).elements.toList()
        }

    private val dataBuilder = forData {
        persons.legs().filter(legFilter)
    }.groupBy {
        legGroup(it)
    }

    private val comparisonBuilder = forData {
        midLegs.filter(rowFilter)
    }.groupBy {
        midGroup(it)
    }

    @Suppress("MagicNumber")
    fun overAge(): PlotterBuilder<G, Bin<Int>, Double> {
        midLegs.forEach { it.ageBin }
        val ageBins = MidRow.ageBinCache.values.toList()

        val dataCount = dataBuilder.count {
            it.person.age.let { a -> min(100, a) }.mapToBins(ageBins)
        }.let {
            if (normalize) {
                it.normalizeByX()
            } else {
                it.normalizeByGroup()
            }
        }.sortAndFill(0.0)

        val compCount = comparisonBuilder.comparisonOver(normalize) { ageBin }

        return dataCount.compareTo { compCount }
    }

    fun overEmployment(): PlotterBuilder<G, Employment, Double> {
        val dataCount = dataBuilder.count {
            it.person.employment.simplifyEmploymentMID()
        }.let {
            if (normalize) {
                it.normalizeByX()
            } else {
                it.normalizeByGroup()
            }
        }.sortAndFill(0.0)

        val compCount = comparisonBuilder.comparisonOver(normalize) { employment }

        return dataCount.compareTo { compCount }
    }

    fun overDistance(): PlotterBuilder<G, Bin<Double>, Double> {
        midLegs.forEach { it.distanceBin }
        val distanceBins = MidLegRow.distBinCache.values.toList()

        val dataCount = dataBuilder.count {
            it.distance(impedance).inKilometers.mapToBins(distanceBins)
        }.let {
            if (normalize) {
                it.normalizeByX()
            } else {
                it.normalizeByGroup()
            }
        }.sortAndFill(0.0)

        val compCount = comparisonBuilder.comparisonOver(normalize) { distanceBin }

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
            if (normalize) {
                it.normalizeByX()
            } else {
                it.normalizeByGroup()
            }
        }.sortAndFill(0.0)

        val compCount = comparisonBuilder.comparisonOver(normalize) { durationBin }

        return dataCount.compareTo {
            compCount
        }
    }

    fun overTripStart() = overTime({ it.leg.startTime }, { it.tripStart ?: AbsoluteTime.Companion.START })
    fun overActivityStart() = overTime({ it.leg.endTime }, { it.activityStart ?: AbsoluteTime.Companion.START })

    fun overTime(
        legToTime: (PersonLeg) -> AbsoluteTime,
        midRowToTime: (MidLegRow) -> AbsoluteTime,
    ): PlotterBuilder<G, AbsoluteTime, Double> {
        val dataCount = dataBuilder.count {
            legToTime(it).let { t -> t - t.daysSinceStart.days }.roundToMultipleOf(1.hours)
        }.let {
            if (normalize) {
                it.normalizeByX()
            } else {
                it.normalizeByGroup()
            }
        }.fillMissingXValues {
            0.0
        }.sortAndFill(0.0)

        val compCount = comparisonBuilder.comparisonOver(normalize) {
            midRowToTime(this).roundToMultipleOf(1.hours)
        }

        return dataCount.compareTo {
            compCount
        }
    }
}

private fun <E : MidRow, T : Comparable<T>, G> PlotDataBuilderWithGrouping<E, G>.comparisonOver(
    normalize: Boolean,
    scope: E.() -> T,
): PlotDataTransformationBuilder<G, T, Double> = this.plotSumOf {
    if (normalize) {
        it.absolute
    } else {
        it.relative
    }
}.over {
    it.scope()
}.let {
    if (normalize) {
        it.normalizeByX()
    } else {
        it.normalizeByGroup()
    }
}.sortAndFill(0.0)

private fun <G, B : Comparable<B>, T : Number> PlotDataTransformationBuilder<G, B, T>.sortAndFill(default: T) =
    this.fillMissingXValues {
        default
    }.sortX {
        Ordering.Ascending()
    }.sortGroups {
        Ordering.AscendingBy { it.hashCode() }
    }
