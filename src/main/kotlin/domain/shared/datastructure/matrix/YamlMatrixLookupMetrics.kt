package domain.shared.datastructure.matrix

import core.datastructure.matrix.Matrix
import core.datastructure.matrix.MatrixFormat
import core.datastructure.matrix.MultiMatrix
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
import units.Currency
import units.CurrencyUnit
import units.Distance
import units.DistanceUnit
import units.toCurrency
import units.toDistance
import utils.CodePlan
import utils.units.AbsoluteTime
import utils.units.Time
import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private val zoneMatrixFormats: List<MatrixFormat<ZoneId>> = listOf(
    constantMatrixFormat(),
    VisumMatrixFormat,
    BinaryZoneFloatMatrixFormat,
)

@Suppress("LongParameterList")
class YamlMatrixLookupMetrics(
    travelTimeMatrixConfig: Path,
    travelCostMatrixConfig: Path,
    distanceMatrix: Path,
    durationUnit: DurationUnit,
    currencyUnit: CurrencyUnit,
    distanceUnit: DistanceUnit,
    modeCodes: CodePlan<Mode>,
    simulationStart: AbsoluteTime,
    simulationEnd: AbsoluteTime,
    betterFormat: InternalMatrixLookup? = null
) : Metrics {

    private val formats = betterFormat?.let {
        zoneMatrixFormats.checkBinaryCache<ZoneId>(it)
    } ?: zoneMatrixFormats

    private val travelTimes: MultiMatrix<Mode, ZoneId, Duration> = YamlMultiMatrix<Mode, ZoneId, Duration>(
        path = travelTimeMatrixConfig,
        parser = { it.toDuration(durationUnit) },
        modeDecoder = modeCodes,
        simulationStartInclusive = simulationStart,
        simulationEndExclusive = simulationEnd,
        formats = formats
    )

    private val travelCosts: MultiMatrix<Mode, ZoneId, Currency> = YamlMultiMatrix<Mode, ZoneId, Currency>(
        path = travelCostMatrixConfig,
        parser = { it.toCurrency(currencyUnit) },
        modeDecoder = modeCodes,
        simulationStartInclusive = simulationStart,
        simulationEndExclusive = simulationEnd,
        formats = formats,

    )

    private val distances: Matrix<ZoneId, Distance> = VisumMatrix( // TODO apply parser based on file type?
        path = distanceMatrix,
        converter = { it.toDistance(distanceUnit) }
    )

    override fun costMetric(mode: Mode, time: Time): CostMetric = travelCosts.matrixAt(mode, time).asMetric()

    override fun distanceMetric(mode: Mode): DistanceMetric = distances.asMetric()

    override fun durationMetric(mode: Mode, time: Time): DurationMetric = travelTimes.matrixAt(mode, time).asMetric()
}

private const val ERROR_MSG = "MatrixMetric requires Locations with zone Infos."

private class MatrixMetric<R>(
    private val matrix: Matrix<ZoneId, R>,
) : LocationMetric<R> {

    override fun evaluate(origin: Location, destination: Location): R {
        val o = requireNotNull(origin.zone) {
            "$ERROR_MSG. The given origin Location $origin does not provide a zone."
        }

        val d = requireNotNull(origin.zone) {
            "$ERROR_MSG. The given destination Location $origin does not provide a zone."
        }

        return matrix[o.id, d.id]
    }
}

private fun <M, O> M.asMetric(): MatrixMetric<O> where M : Matrix<ZoneId, O> = MatrixMetric(this)
