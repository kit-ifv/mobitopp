package application.config

import application.config.subconfigs.BaseCSVFiles
import application.config.subconfigs.CoreCSVConfig
import application.steps.HasZoneRepo
import application.steps.RegionCodesConfig
import application.steps.SourceFilesConfig
import application.steps.UnitConfig
import application.steps.parser.csv.loadZones
import application.steps.parser.csv.zoneCsv
import application.steps.parser.csv.zones
import core.modelsteps.Cloneable
import core.modelsteps.Config
import core.modelsteps.Context
import core.modelsteps.ExecutionMode
import core.modelsteps.Simulation
import core.modelsteps.initReport
import core.modelsteps.resources.MapRepository
import core.modelsteps.resources.MutableRepository
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.MutableZone
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import org.junit.jupiter.api.Test
import utils.CodePlan
import utils.ErrorHandling
import utils.report.ReportBuilder
import utils.units.AbsoluteTime
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

data class MyContext(
    override val execMode: ExecutionMode = ExecutionMode(),
    override val scenarioName: String = "",
    var setDuringValidation: Boolean = false,
) : Context, Cloneable<MyContext>, HasZoneRepo<MutableZone, Zone> {
    override var currentStep: String = ""
    override val report: ReportBuilder = initReport()

    override fun clone(): MyContext {
        return this.copy()
    }

    override val mutableZoneRepository: MutableRepository<MutableZone, ZoneId> =
        MapRepository<MutableZone, ZoneId>("zones")
}

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
): Config, SourceFilesConfig, UnitConfig, RegionCodesConfig

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

        Simulation(MyConfig()) {
            MyContext()
        }.steps {

            zones(sealed=false) {
                loadZones(zoneCsv(
                    path = Path("src/test/resources/testDemand/zone-repository/zones.csv")
                ))
            }

            assert(!zoneRepository.sealed) {
                "Repository sealing should be fixed by a shallow copy. " +
                    "Why does this fail?"
            }

            zones(sealed=true) { }

            assert(zoneRepository.sealed) {
                "Zone repository should now be sealed!"
            }

        }
    }
}
