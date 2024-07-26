package syntheticsim

import OTHER_TEST_ZONE
import TEST_ZONE
import domain.data.ActivityId
import domain.data.EconomicStatus
import domain.data.LegacyZone
import domain.data.Person
import domain.data.PlannedActivity
import domain.data.asBuilder
import domain.enums.ActivityType
import domain.enums.Bbsr17
import domain.enums.LegacyActivityType
import domain.enums.Mode
import domain.events.InitPersonEvent
import domain.location.CostMetric
import domain.location.DistanceMetric
import domain.location.DurationMetric
import domain.location.Metrics
import domain.location.Position
import domain.location.RoadPosition
import domain.location.ZoneLevelMetric
import domain.location.ZoneLocation
import generatePersons
import modeling.events.Simulator
import modeling.steps.AddResourceStep
import modeling.steps.BuildStep
import modeling.steps.CustomStep
import modeling.steps.MapRepository
import modeling.steps.ModelExecution
import modeling.steps.Run
import modeling.steps.SimulationContext
import modeling.steps.asResource
import org.junit.jupiter.api.Test
import testPerson
import units.Currency
import units.Distance
import units.euros
import units.kilometers
import usecases.AttractivenessModel
import usecases.steps.LegacyContext
import usecases.steps.LegacyZonesContext
import usecases.steps.loadChoiceModels
import utils.units.AbsoluteTime
import utils.units.Time
import java.io.File
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Person.loadActivityPlan(lambda: PlanLoader.() -> Unit) {
    PlanLoader(this).lambda()
}

class PlanLoader(val person: Person) {
    operator fun Triple<ActivityType, Number, Number>.unaryPlus(): PlannedActivity {
        return PlannedActivity(
            ActivityId(-1L),
            person,
            first,
            -1.minutes,
            AbsoluteTime(
                second.toDouble().toDuration(DurationUnit.HOURS),

            ),
            third.toDouble().toDuration(DurationUnit.HOURS),
            person.random
        )
    }
}

fun <S> S.loadSynthetic(persons: List<Person>) where S : ModelExecution<LegacyContext> {
    val simStep = CustomStep(
        name = "simulate planned activities",
        validation = { true }
    ) {
        val person: Person = testPerson
        person.loadActivityPlan {
            +Triple(LegacyActivityType.HOME, 0, 4)
            +Triple(LegacyActivityType.WORK, 8, 1)
            +Triple(LegacyActivityType.HOME, 10, 1)
        }

        persons.forEach {
            it.loadActivityPlan {
                +Triple(LegacyActivityType.HOME, 0, 4)
                +Triple(LegacyActivityType.WORK, 8, 1)
                +Triple(LegacyActivityType.HOME, 10, 1)
            }
        }
        val sim = Simulator(timeStep = context.timeStep)

        val t = persons.asSequence().asResource("nwoaiernh", "aouhin")
        val e = MapRepository(t)

        sim.addAgents(e) {
            InitPersonEvent(it, context.behavior.value)
        }
        sim.run(context.simulationStart, context.simulationEnd)
    }

    this.addStep(simStep)
}
fun <S : ModelExecution<C>, C : LegacyZonesContext> S.loadSyntheticZones() {
    val zones = sequenceOf<LegacyZone>(TEST_ZONE, OTHER_TEST_ZONE)
    val resource = zones.map { it.asBuilder() }.asResource("nope", "nope")

    this.addStep(
        AddResourceStep(
            name = "load zone csv",
            resource = resource,
            repository = context.zoneRepository
        )
    )
    context.attractivenessModel.value = AttractivenessModel { i, d ->
        1.0
    }
    this.addStep(BuildStep("finish zones", context.zoneRepository))
}
// fun <S: ModelExecution<C>, C: PersonContext> S.loadSyntheticPerson() {
//    context.personRepository.addBuilders(sequenceOf())
//    addStep(BuildStep(
//        "finish people",
//        context.personRepository
//    ))
// }

fun <S : ModelExecution<C>, C : SimulationContext> S.loadSyntheticImpedance() {
    context.impedance.value = ControllableImpedance()
}

@Suppress("NotImplementedDeclaration") // For test stubs this is not an issue
class ControllableImpedance : Metrics {
    val mep: Map<Mode, CostMetric> = emptyMap()
    val t: ZoneLevelMetric<Currency> = object : ZoneLevelMetric<Currency> {
        override fun evaluate(origin: ZoneLocation, destination: ZoneLocation): Currency {
            return 1.euros
        }

        override fun mapPosition(position: Position): ZoneLocation {
            TODO("Not yet implemented")
        }

        override fun mapRoadPosition(roadPosition: RoadPosition): ZoneLocation {
            TODO("Not yet implemented")
        }
    }

    val time: DurationMetric = object : ZoneLevelMetric<Duration> {
        override fun evaluate(origin: ZoneLocation, destination: ZoneLocation): Duration {
            return 10.minutes
        }

        override fun mapPosition(position: Position): ZoneLocation {
            TODO("Not yet implemented")
        }

        override fun mapRoadPosition(roadPosition: RoadPosition): ZoneLocation {
            TODO("Not yet implemented")
        }
    }
    val dis: DistanceMetric = object : ZoneLevelMetric<Distance> {
        override fun evaluate(origin: ZoneLocation, destination: ZoneLocation): Distance {
            return 1.kilometers
        }

        override fun mapPosition(position: Position): ZoneLocation {
            TODO("Not yet implemented")
        }

        override fun mapRoadPosition(roadPosition: RoadPosition): ZoneLocation {
            TODO("Not yet implemented")
        }
    }
    override fun costMetric(mode: Mode, time: Time): CostMetric {
        return mep[mode] ?: t
    }

    override fun distanceMetric(mode: Mode): DistanceMetric {
        return dis
    }

    override fun durationMetric(mode: Mode, time: Time): DurationMetric {
        return this.time
    }
}
class Synthetic {
    private val root = "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output"

    private val rootRastatt = File("$root\\logiktram_rastatt_long-term-module\\rastatt")

    @Test
    fun run() {
        val persons = generatePersons(100)
        Run {
            LegacyContext(
                scenarioName = "testSteps",
                areaTypeCodes = Bbsr17,
                demandFolder = rootRastatt,
                economicalStatusCodes = EconomicStatus,
                simulationSeed = 42,
            )
        }.steps {
            loadSyntheticZones()
            addStep(
                CustomStep(name = "Print Text", validation = { true }, exec = {
                    val e = context.zoneRepository.elements.toList()
                    println(e)
                })
            )

            loadSyntheticImpedance()
            loadChoiceModels()
            loadSynthetic(persons)
        }

        val target = testPerson
        println(target)
    }

    @Test
    fun estimateUtils() {
        println(1 shl 10)
        val ues = listOf(0, 1, 2)
        println(ues.map { exp(it.toDouble()) })
        val sumOf = ues.sumOf { exp(it.toDouble()) }
        val ln2 = 1 / ln(2.0)
        println(sumOf)
        println(ues.map { exp(it.toDouble()) / sumOf })
        val alteredUs = ues.map { it * ln2 }
        val alteredSum = alteredUs.sumOf { 2.0.pow(it) }
        println(alteredSum)
        println(alteredUs.map { 2.0.pow(it) / alteredSum })
    }
}
