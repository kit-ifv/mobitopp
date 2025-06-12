package domain.simulation.results

import core.modelsteps.Repository
import core.results.plots.Ordering
import core.results.plots.RGB
import core.results.plots.forData
import core.results.plots.modeStringColor
import core.results.plots.randomColor
import domain.shared.datastructure.schedule.MovingAction
import domain.shared.enums.Mode
import domain.simulation.agent.PersonAgent
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.IPerson
import domain.synthesis.data.PersonId
import utils.collections.asBins
import utils.collections.mapToBins

interface AgentResultsContext {
    val personAgents: Repository<PersonAgent, PersonId>
    val householdRepository: Repository<Household, HouseholdId>
}

val AgentResultsContext.persons: List<PersonAgent>
    get() = personAgents.elements.toList()

val AgentResultsContext.households: List<Household>
    get() = householdRepository.elements.toList()

data class PersonLeg(val person: PersonAgent, val leg: MovingAction)

val AgentResultsContext.personLegs: List<PersonLeg>
    get() = persons.flatMap { person ->
        person.schedule.pastLegs().map { leg -> PersonLeg(person, leg) }
    }.toList()

@Suppress("MagicNumber")
private val ageGroups = listOf(
    0 to 10,
    10 to 20,
    20 to 30,
    30 to 40,
    40 to 50,
    50 to 60,
    60 to 70,
    70 to 80,
    80 to 90,
    90 to 100,
    100 to 120,
)

fun Int.ageGroup() = this.ageGroup(ageGroups)

fun Int.ageGroup(intervals: List<Pair<Int, Int>>) =
    intervals.find { it.first <= this && this < it.second }?.let {
        "[${it.first},${it.second})"
    } ?: "UNDEFINED"

@Suppress("MagicNumber")
private val ageBins = listOf(
    0 to 7, 7 to 11, 11 to 14, 14 to 18, 18 to 30, 30 to 40,
    40 to 50, 50 to 60, 60 to 65, 65 to 75, 75 to 80, 80 to 120
).asBins()

fun <G> AgentResultsContext.agePlot(
    groupBy: (IPerson) -> G,
    label: String = "group",
    order: Ordering<G> = Ordering.Arbitrary(),
    coloring: (G) -> RGB = { _ -> randomColor() }
) =
    forData {
        persons
    }.groupBy {
        groupBy(it)
    }.count {
        it.age.mapToBins(ageBins)
    }.colorByGroup().withStyle {
        xLabel = "age"
        xOrder = Ordering.Ascending()
        groupOrder = order
        this.name = "age distribution by $label"
        groupLabel = label
        colorMap = coloring
    }

fun modeColor(mode: Mode): RGB = modeStringColor(mode.toString().lowercase())
