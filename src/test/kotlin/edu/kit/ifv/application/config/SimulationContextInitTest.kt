package edu.kit.ifv.application.config
import edu.kit.ifv.application.config.subconfigs.BaseCSVFiles
import edu.kit.ifv.application.config.subconfigs.CoreCSVConfig
import edu.kit.ifv.application.steps.HasZoneRepo
import edu.kit.ifv.application.steps.RegionCodesConfig
import edu.kit.ifv.application.steps.SourceFilesConfig
import edu.kit.ifv.application.steps.UnitConfig
import edu.kit.ifv.application.steps.parser.csv.loadZones
import edu.kit.ifv.application.steps.parser.csv.zoneCsv
import edu.kit.ifv.application.steps.parser.csv.zones
import edu.kit.ifv.core.modelsteps.Cloneable
import edu.kit.ifv.core.modelsteps.Config
import edu.kit.ifv.core.modelsteps.Context
import edu.kit.ifv.core.modelsteps.ExecutionMode
import edu.kit.ifv.core.modelsteps.Simulation
import edu.kit.ifv.core.modelsteps.initReport
import edu.kit.ifv.core.modelsteps.resources.MapRepository
import edu.kit.ifv.core.modelsteps.resources.MutableRepository
import edu.kit.ifv.domain.shared.enums.areatype.RegioStaR17
import edu.kit.ifv.domain.shared.enums.areatype.RegionType
import edu.kit.ifv.domain.shared.location.zone.MaximalZone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.utils.CodePlan
import edu.kit.ifv.utils.ErrorHandling
import edu.kit.ifv.utils.report.ReportBuilder
import edu.kit.ifv.utils.units.AbsoluteTime
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

data class MyContext(
    override val execMode: ExecutionMode = ExecutionMode(),
    override val scenarioName: String = "",
    var setDuringValidation: Boolean = false,
) : Context,
    Cloneable<MyContext>,
    HasZoneRepo<MaximalZone, MaximalZone> {
    override var currentStep: String = ""
    override val report: ReportBuilder = initReport()

    override fun clone(): MyContext = this.copy()

    override val mutableZoneRepository: MutableRepository<MaximalZone, ZoneId> =
        MapRepository<MaximalZone, ZoneId>("zones")
}

@Suppress("LongParameterList")
class MyConfig(
    val dataFolder: Path = Path(""),
    val resultDir: Path = Path(""),
    val simulationStart: AbsoluteTime = AbsoluteTime(0.minutes),
    val simulationEnd: AbsoluteTime = AbsoluteTime(0.minutes),
    val timeStep: Duration = 0.minutes,
    val costUnit: CurrencyUnit = CurrencyUnit.EUROS,
    override val distanceUnit: DistanceUnit = DistanceUnit.METERS,
    override val seed: Long = 42L,
    override val errorHandling: ErrorHandling = ErrorHandling.ERROR,
    override val sourceFiles: BaseCSVFiles = CoreCSVConfig(dataRepo = dataFolder, zoneRepo = dataFolder),
    override val cachePath: Path = Path("cache"),
    override val durationUnit: DurationUnit = DurationUnit.MINUTES,
    override val currencyUnit: CurrencyUnit = CurrencyUnit.EUROS,
    override val regionTypeCodes: CodePlan<RegionType> = RegioStaR17,
) : Config,
    SourceFilesConfig,
    UnitConfig,
    RegionCodesConfig

class SimulationContextInitTest {
    @Test
    fun noSideEffectWhenInitInLambda() {
        Simulation(MyConfig()) {
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
        Simulation(MyConfig()) {
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
            MyConfig(),
            myContext,
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
        Simulation(MyConfig()) {
            MyContext()
        }.steps {
            zones(sealed = false) {
                loadZones(
                    zoneCsv(
                        path = Path("src/test/resources/testDemand/zone-repository/zones.csv"),
                    ),
                )
            }

            assert(!zoneRepository.sealed) {
                "Repository sealing should be fixed by a shallow copy. " +
                    "Why does this fail?"
            }

            zones(sealed = true) { }

            assert(zoneRepository.sealed) {
                "Zone repository should now be sealed!"
            }
        }
    }
}
