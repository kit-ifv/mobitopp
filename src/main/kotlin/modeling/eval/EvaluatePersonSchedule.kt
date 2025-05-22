package modeling.eval

import datastructure.MovingAction
import domain.agent.PersonAgent
import domain.data.Household
import domain.data.IPerson
import usecases.steps.ProjectContext
import utils.collections.asBins
import utils.collections.mapToBins

val ProjectContext.persons: List<PersonAgent>
    get() = personAgents.elements.toList()

val ProjectContext.households: List<Household>
    get() = householdRepository.elements.toList()

data class PersonLeg(val person: PersonAgent, val leg: MovingAction)

val ProjectContext.personLegs: List<PersonLeg>
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

fun <G> ProjectContext.agePlot(
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

// interface EvaluateLegs {
//
//    val name: String
//
//    fun evaluate(persons: Collection<Person>) {
//        doEvaluate(persons.flatMap { it.schedule.pastLegs() })
//    }
//
//    fun doEvaluate(legs: List<MovingAction>)
//
// }
//
// fun EvaluateLegs.asStep(repo: Repository<Person, PersonId>): ModelStep {
//    return object:ModelStep {
//        override val name: String = this@asStep.name
//
//        override fun execute() = this@asStep.evaluate(
//            repo.elements.toList()
//        )
//
//        override fun validate(): Boolean = true
//    }
// }

// fun <S, C> S.plotModalSplit() where S: ModelExecution<C>, C: PersonContext {
//
//    addStep(object:ModelStep {
//        override val name = "Plot Modal Split"
//
//        override fun execute() {
//            HistogramPlotter<Pair<Person, MovingAction>, Mode, Sex, Unit>(
//                style = PlotLayoutSpecification(
//                    name = "Modal Split by Sex normalized",
//                    xLabel = "Mode of Transportation",
//                    groupLabel = "sex"
//                ),
//                entities = context.personRepository.elements.flatMap {
//                    it.schedule.pastLegs().map { l -> it to l }
//                }.toList(),
//                xAttribute = { it.second.transportType },
//                groupBy = { it.first.sex }
//            ).plot()
//
//            HistogramPlotter<Pair<Person, MovingAction>, Mode, Sex, Unit>(
//                style = PlotLayoutSpecification(
//                    name = "Modal Split by Sex relative",
//                    xLabel = "Mode of Transportation",
//                    groupLabel = "sex"
//                ),
//                entities = context.personRepository.elements.flatMap {
//                    it.schedule.pastLegs().map { l -> it to l }
//                }.toList(),
//                xAttribute = { it.second.transportType },
//                groupBy = { it.first.sex },
//                normalize = false,
//            ).plot()
//
//            HistogramPlotter<Pair<Person, MovingAction>, Mode, Unit, Unit>(
//                style = PlotLayoutSpecification(
//                    name = "Modal Split absolute",
//                    xLabel = "Mode of Transportation",
//                ),
//                entities = context.personRepository.elements.flatMap {
//                    it.schedule.pastLegs().map { l -> it to l }
//                }.toList(),
//                xAttribute = { it.second.transportType },
//                normalize = false,
//                relative = false,
//            ).plot()
//
//            HistogramPlotter<Pair<Person, MovingAction>, Mode, Unit, Unit>(
//                style = PlotLayoutSpecification(
//                    name = "Modal Split relative",
//                    xLabel = "Mode of Transportation",
//                ),
//                entities = context.personRepository.elements.flatMap {
//                    it.schedule.pastLegs().map { l -> it to l }
//                }.toList(),
//                xAttribute = { it.second.transportType },
//                normalize = false,
//                relative = true,
//            ).plot()
//        }
//
//        override fun validate() = true
//
//    })
//
// }

// @Suppress("MagicNumber")
// class PlotModalSplit: EvaluateLegs {
//    override val name: String = "Plot Modal Split"
//
//    override fun doEvaluate(legs: List<MovingAction>) {
//
//        val modes = legs.map { it.transportType }.groupingBy { it }.eachCount()
//        val totalCount = modes.values.sum().toDouble()
//
//        val df = dataFrameOf(
//            "mode" to modes.keys.toList(),
//            "count" to modes.values.map { it / totalCount }
//        )
//
//        df.plot {
//            bars {
//                x("mode") {
//                    axis.name = "Mode of Transport"
//                }
//                y("count") {
//                    axis.name = "relative share [%]"
//                }
//            }
//            layout.title = "Modal Split"
//        }.save("modal_split.png", path="results")
//
//    }
//
//
// }
