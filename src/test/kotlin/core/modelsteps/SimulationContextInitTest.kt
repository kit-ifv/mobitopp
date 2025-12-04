package core.modelsteps

import application.config.ExampleProjectContext
import application.steps.parser.csv.finishZones
import application.steps.parser.csv.prepareZones
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import org.junit.jupiter.api.Test
import utils.units.AbsoluteTime
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

data class MyContext(
    override val execMode: ExecutionMode = ExecutionMode(),
    override val scenarioName: String = "",
    override val dataFolder: Path = Path(""),
    override val simulationSeed: Long = 0,
    override val resultDir: Path = Path(""),
    override val simulationStart: AbsoluteTime = AbsoluteTime(0.minutes),
    override val simulationEnd: AbsoluteTime = AbsoluteTime(0.minutes),
    override val timeStep: Duration = 0.minutes,
    override val timeUnit: DurationUnit = DurationUnit.MINUTES,
    override val costUnit: CurrencyUnit = CurrencyUnit.EUROS,
    override val distanceUnit: DistanceUnit = DistanceUnit.METERS,
    var setDuringValidation: Boolean = false
) : Context, Cloneable<MyContext> {
    override fun clone(): MyContext {
        return this.copy()
    }
}

data class DeepCopyFailure(
    override val execMode: ExecutionMode = ExecutionMode(),
    override val scenarioName: String = "",
    override val dataFolder: Path = Path(""),
    override val simulationSeed: Long = 0,
    override val resultDir: Path = Path(""),
    override val simulationStart: AbsoluteTime = AbsoluteTime(0.minutes),
    override val simulationEnd: AbsoluteTime = AbsoluteTime(0.minutes),
    override val timeStep: Duration = 0.minutes,
    override val timeUnit: DurationUnit = DurationUnit.MINUTES,
    override val costUnit: CurrencyUnit = CurrencyUnit.EUROS,
    override val distanceUnit: DistanceUnit = DistanceUnit.METERS,
    var setDuringValidation: Boolean = false,
    val list: MutableList<Boolean> = mutableListOf(false)
) : Context, Cloneable<DeepCopyFailure> {
    override fun clone(): DeepCopyFailure {
        return this.copy()
    }
}

class SimulationContextInitTest {
    @Test
    fun noSideEffectWhenInitInLambda() {
        Simulation {
            MyContext()
        }.steps {
            assert(!setDuringValidation) { "If this fails, the variable was set during the " +
                "validation and not reset for the run. This behaviour should be prevented." }
            setDuringValidation = true
        }
    }

    @Test
    fun withValFactory() {
        val myContext = MyContext()
        Simulation {
            myContext
        }.steps {
            assert(!setDuringValidation) { "If this fails, the variable was set during the " +
                "validation and not reset for the run. This behaviour should be prevented." }
            setDuringValidation = true
        }
    }

    @Test
    fun withFactoryMethodFactory() {
        val myContext: () -> MyContext = { MyContext() }
        Simulation(
            myContext
        ).steps {
            assert(!setDuringValidation) { "If this fails, the variable was set during the " +
                "validation and not reset for the run. This behaviour should be prevented." }
            setDuringValidation = true
        }
    }


    @Test
    fun deepCopyFailure() {
        val myContext: () -> DeepCopyFailure = { DeepCopyFailure() }
        // with lambda that creates new instances, everything is fine.
        Simulation(
            myContext
        ).steps {
            assert(!list[0]){ "If this fails, the list element was set during the " +
                    "validation and not reset for the run. This behaviour should be prevented." }
            assert(!setDuringValidation) { "If this fails, the variable was set during the " +
                    "validation and not reset for the run. This behaviour should be prevented." }
            setDuringValidation = true
            list[0] = true
        }

        var count = 0
        val singleInstance = DeepCopyFailure()
        // if we use a lambda that returns the same instance, the copy saves us from changes to shallow variables,
        // like the setDuringValidation variable, but changes to dataStructures like the list, are still
        Simulation {
            singleInstance
        }.steps {
            if (count == 0) assert(!list[0]) // on first iteration list item is false
            if (count == 1) assert(list[0]) // on second iteration list item was changed. Because clone does not provide
                                            // deep copies this is expected behaviour.
            assert(!setDuringValidation) { "If this fails, the variable was set during the " +
                    "validation and not reset for the run. This behaviour should be prevented." }
            setDuringValidation = true
            list[0] = true
            count++
        }
    }

    @Test
    fun repositorySealFix() {
        val myContext = ExampleProjectContext(
            scenarioName = "",
            dataFolder = Path(""),
        )
        Simulation{
            myContext
        }.steps {
            prepareZones(Path("src/test/resources/testDemand/zone-repository/zones.csv"))
            assert(!zoneRepository.sealed) { "Repository sealing should be fixed by a shallow copy. " +
                    "Why does this fail?"}
            finishZones()
        }
    }
}
