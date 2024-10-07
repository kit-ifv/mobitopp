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
import domain.location.Metrics
import domain.location.Position
import domain.location.RoadPosition
import domain.location.ZoneLevelMetric
import domain.location.ZoneLocation
import units.Currency
import units.CurrencyUnit
import units.Distance
import units.DistanceUnit
import units.toCurrency
import units.toDistance
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
) : Metrics {

    private val travelTimes: MultiMatrix<Mode, ZoneId, Duration> = YamlMultiMatrix<Mode, ZoneId, Duration>(
        path = travelTimeMatrixConfig.toPath(),
        parser = { it.toDuration(durationUnit) },
        modeDecoder = modeCodes,
        simulationStartInclusive = simulationStart,
        simulationEndExclusive = simulationEnd,

    ).also {
        println("\nFinished init travel time matrices: ${travelTimeMatrixConfig.name}")
    }

    private val travelCosts: MultiMatrix<Mode, ZoneId, Currency> = YamlMultiMatrix<Mode, ZoneId, Currency>(
        path = travelCostMatrixConfig.toPath(),
        parser = { it.toCurrency(currencyUnit) },
        modeDecoder = modeCodes,
        simulationStartInclusive = simulationStart,
        simulationEndExclusive = simulationEnd,

    ).also {
        println("\nFinished init travel cost matrices: ${travelCostMatrixConfig.name}")
    }

    private val distances: Matrix<ZoneId, Distance> = VisumMatrix(
        path = distanceMatrix.toPath(),
        converter = { it.toDistance(distanceUnit) }

    ).also {
        println("Finished init travel distance matrix: ${distanceMatrix.name}")
    }

    override fun costMetric(mode: Mode, time: Time): CostMetric = travelCosts.matrixAt(mode, time).asMetric()

    override fun distanceMetric(mode: Mode): DistanceMetric = distances.asMetric()

    override fun durationMetric(mode: Mode, time: Time): DurationMetric = travelTimes.matrixAt(mode, time).asMetric()
}

private class MatrixMetric<R>(
    private val matrix: Matrix<ZoneId, R>,
) : ZoneLevelMetric<R> {

    override fun evaluate(origin: ZoneLocation, destination: ZoneLocation): R {
        return matrix[origin.zone.id, destination.zone.id]
    }

    override fun mapPosition(position: Position): ZoneLocation {
        error("Position locations not expected in theis simulation configuration!")
    }

    override fun mapRoadPosition(roadPosition: RoadPosition): ZoneLocation {
        error("RoadPosition locations not expected in theis simulation configuration!")
    }
}

private fun <M, O> M.asMetric(): MatrixMetric<O> where M : Matrix<ZoneId, O> = MatrixMetric(this)
