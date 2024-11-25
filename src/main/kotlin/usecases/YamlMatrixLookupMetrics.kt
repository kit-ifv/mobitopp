package usecases

import datastructure.matrix.Matrix
import datastructure.matrix.MultiMatrix
import datastructure.matrix.VisumMatrix
import datastructure.matrix.YamlMultiMatrix
import datastructure.matrix.matrixAt
import domain.data.ZoneId
import domain.enums.Mode
import domain.location.CostMetric
import domain.location.DistanceMetric
import domain.location.DurationMetric
import domain.location.Location
import domain.location.LocationMetric
import domain.location.Metrics
import units.Currency
import units.CurrencyUnit
import units.Distance
import units.DistanceUnit
import units.toCurrency
import units.toDistance
import usecases.steps.InternalMatrixLookup
import utils.CodePlan
import utils.units.AbsoluteTime
import utils.units.Time
import java.io.File
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Suppress("LongParameterList")
class YamlMatrixLookupMetrics(
    travelTimeMatrixConfig: File,
    travelCostMatrixConfig: File,
    distanceMatrix: File,
    durationUnit: DurationUnit,
    currencyUnit: CurrencyUnit,
    distanceUnit: DistanceUnit,
    modeCodes: CodePlan<Mode>,
    simulationStart: AbsoluteTime,
    simulationEnd: AbsoluteTime,
    betterFormat: InternalMatrixLookup? = null
) : Metrics {

    private val travelTimes: MultiMatrix<Mode, ZoneId, Duration> = YamlMultiMatrix<Mode, ZoneId, Duration>(
        path = travelTimeMatrixConfig.toPath(),
        parser = { it.toDuration(durationUnit) },
        modeDecoder = modeCodes,
        simulationStartInclusive = simulationStart,
        simulationEndExclusive = simulationEnd,
        betterFormat

    )

    private val travelCosts: MultiMatrix<Mode, ZoneId, Currency> = YamlMultiMatrix<Mode, ZoneId, Currency>(
        path = travelCostMatrixConfig.toPath(),
        parser = { it.toCurrency(currencyUnit) },
        modeDecoder = modeCodes,
        simulationStartInclusive = simulationStart,
        simulationEndExclusive = simulationEnd,
        betterFormat

    )

    private val distances: Matrix<ZoneId, Distance> = VisumMatrix(
        path = distanceMatrix.toPath(),
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
