package application.config

import application.steps.parser.csv.finishZones
import application.steps.parser.csv.prepareZones
import core.modelsteps.Simulation
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
    override val execMode: core.modelsteps.ExecutionMode = _root_ide_package_.core.modelsteps.ExecutionMode(),
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
) : core.modelsteps.Context, core.modelsteps.Cloneable<MyContext> {
    override fun clone(): MyContext {
        return this.copy()
    }
}

class SimulationContextInitTest {
    @Test
    fun noSideEffectWhenInitInLambda() {
        Simulation {
            MyContext()
        }.steps {
            assert(!setDuringValidation) {
                "If this fails, the variable was set during the " +
                        "validation and not reset for the run. This behaviour should be prevented."
            }
            setDuringValidation = true
        }
    }

    @Test
    fun withValFactory() {
        val myContext = MyContext()
        Simulation {
            myContext
        }.steps {
            assert(!setDuringValidation) {
                "If this fails, the variable was set during the " +
                        "validation and not reset for the run. This behaviour should be prevented."
            }
            setDuringValidation = true
        }
    }

    @Test
    fun withFactoryMethodFactory() {
        val myContext: () -> MyContext = { MyContext() }
        Simulation(
            myContext
        ).steps {
            assert(!setDuringValidation) {
                "If this fails, the variable was set during the " +
                        "validation and not reset for the run. This behaviour should be prevented."
            }
            setDuringValidation = true
        }
    }

    @Test
    fun repositorySealFix() {
        val myContext = ExampleProjectContext(
            scenarioName = "",
            dataFolder = Path(""),
        )
        Simulation {
            myContext
        }.steps {
            prepareZones(Path("src/test/resources/testDemand/zone-repository/zones.csv"))
            assert(!zoneRepository.sealed) {
                "Repository sealing should be fixed by a shallow copy. " +
                        "Why does this fail?"
            }
            finishZones()
        }
    }
}
