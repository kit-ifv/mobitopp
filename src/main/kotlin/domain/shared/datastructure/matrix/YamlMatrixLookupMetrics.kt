package domain.shared.datastructure.matrix

import core.datastructure.matrix.CostMultiMatrix
import core.datastructure.matrix.DistanceMatrix
import core.datastructure.matrix.DurationMultiMatrix
import core.datastructure.matrix.MatrixFormat

import core.datastructure.matrix.YamlMultiMatrix
import core.datastructure.matrix.constantMatrixFormat
import core.datastructure.matrix.matrixAt
import domain.shared.datastructure.matrix.visum.VisumMatrix
import domain.shared.datastructure.matrix.visum.VisumMatrixFormat
import domain.shared.enums.Mode
import domain.shared.location.CostMetric
import domain.shared.location.DistanceMetric
import domain.shared.location.DurationMetric
import domain.shared.location.Location
import domain.shared.location.LocationMetric
import domain.shared.location.Metrics
import domain.shared.location.ZoneId
import units.CurrencyUnit
import units.DistanceUnit
import units.toCurrency
import units.toDistance
import utils.CodePlan
import utils.units.AbsoluteTime
import utils.units.Time
import java.nio.file.Path
import kotlin.time.DurationUnit
import kotlin.time.toDuration
//
//private val zoneMatrixFormats: List<MatrixFormat<ZoneId>> = listOf(
//    constantMatrixFormat(),
//    VisumMatrixFormat,
//    BinaryZoneFloatMatrixFormat,
//)

//@Suppress("LongParameterList")
//class YamlMatrixLookupMetrics(
//    travelTimeMatrixConfig: Path,
//    travelCostMatrixConfig: Path,
//    distanceMatrix: Path,
//    durationUnit: DurationUnit,
//    currencyUnit: CurrencyUnit,
//    distanceUnit: DistanceUnit,
//    modeCodes: CodePlan<Mode>,
//    simulationStart: AbsoluteTime,
//    simulationEnd: AbsoluteTime,
//    betterFormat: InternalMatrixLookup? = null,
//) : Metrics {
//
//    private val formats = betterFormat?.let {
//        zoneMatrixFormats.checkBinaryCache<ZoneId>(it)
//    } ?: zoneMatrixFormats
//
//    private val travelTimes: DurationMultiMatrix<Mode> = DurationMultiMatrix(
//        YamlMatrixWrapper(
//            YamlMultiMatrix<Mode, ZoneId>(
//                path = travelTimeMatrixConfig,
//
//                modeDecoder = modeCodes,
//                simulationStartInclusive = simulationStart,
//                simulationEndExclusive = simulationEnd,
//                formats = formats
//            )
//        ),
//    ) { it.toDuration(durationUnit) }
//
//
//    private val travelCosts: CostMultiMatrix<Mode> = CostMultiMatrix(
//        YamlMatrixWrapper(YamlMultiMatrix<Mode, ZoneId>(
//            path = travelCostMatrixConfig,
//
//            modeDecoder = modeCodes,
//            simulationStartInclusive = simulationStart,
//            simulationEndExclusive = simulationEnd,
//            formats = formats,
//
//            )),
//    ) { it.toCurrency(currencyUnit) }
//
//
//    private val distances = DistanceMatrix( // TODO apply parser based on file type?
//        VisumMatrix(distanceMatrix),
//        converter = { it.toDistance(distanceUnit) }
//    )
//
//    override fun costMetric(mode: Mode, time: Time): CostMetric = travelCosts[mode, time]
//
//    override fun distanceMetric(mode: Mode): DistanceMetric = distances
//
//    override fun durationMetric(mode: Mode, time: Time): DurationMetric = travelTimes[mode, time]
//}

//private const val ERROR_MSG = "MatrixMetric requires Locations with zone Infos."

//private class MatrixMetric<R>(
//    private val matrix: MatrixOld<ZoneId, R>,
//) : LocationMetric<R> {
//
//    override fun evaluate(origin: Location, destination: Location): R {
//        val o = requireNotNull(origin.zone) {
//            "$ERROR_MSG. The given origin Location $origin does not provide a zone."
//        }
//
//        val d = requireNotNull(origin.zone) {
//            "$ERROR_MSG. The given destination Location $origin does not provide a zone."
//        }
//
//        return matrix[o.id, d.id]
//    }
//}
//
//private fun <M, O> M.asMetric(): MatrixMetric<O> where M : MatrixOld<ZoneId, O> = MatrixMetric(this)
