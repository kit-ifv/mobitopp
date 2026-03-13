package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.optimized.DoubleToCurrency
import domain.shared.datastructure.matrix.optimized.DoubleToDistance
import domain.shared.datastructure.matrix.optimized.DoubleToDuration
import domain.shared.datastructure.matrix.yaml.YamlInfo
import domain.shared.datastructure.matrix.yaml.YamlMatrixLookup
import domain.shared.enums.Mode
import domain.shared.location.CostMetric
import domain.shared.location.DistanceMetric
import domain.shared.location.DurationMetric
import domain.shared.location.HasZone
import domain.shared.location.Metrics
import domain.shared.location.ZoneId
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.kilometers
import edu.kit.ifv.units.toCurrency
import edu.kit.ifv.units.toDistance
import utils.Decodable
import utils.units.Time
import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Provides [Metrics] backed by zone-based OD matrices.
 *
 * - **Travel times**: mode- and time-dependent lookup.
 * - **Travel costs**: mode- and time-dependent lookup.
 * - **Travel distances**: fixed [ZoneIdMatrix].
 *
 * All matrices store raw doubles; a [UnitConverter] maps them to
 * [Duration], [Currency], or [Distance].
 *
 * This implementation is specialized for [ZoneId] indices to avoid boxing.
 */
class MatrixMetrics(
    private val travelTimes: ZoneMatrixLookup<Mode>,
    private val travelCosts: ZoneMatrixLookup<Mode>,
    private val travelDistance: ZoneIdMatrix,
    unitConverters: UnitConverter
) : Metrics {
    private val currencyConverter = unitConverters.currencyConverter
    private val timeConverter = unitConverters.timeConverter
    private val distanceConverter = unitConverters.distanceConverter

    override fun costMetric(mode: Mode, time: Time): CostMetric {
        return CostMetric { o: HasZone, d: HasZone ->
            currencyConverter.from(travelCosts[mode, time][o.zoneID, d.zoneID])
        }
    }

    override fun distanceMetric(mode: Mode): DistanceMetric {
        return DistanceMetric { o, d ->
            distanceConverter.from(travelDistance[o.zoneID, d.zoneID])
        }
    }

    override fun durationMetric(
        mode: Mode,
        time: Time,
    ): DurationMetric {
        return DurationMetric { o: HasZone, d: HasZone ->
            timeConverter.from(travelTimes[mode, time][o.zoneID, d.zoneID])
        }
    }

    fun cost(
        from: ZoneId,
        to: ZoneId,
        mode: Mode,
        time: Time,
    ): Currency {
        return currencyConverter.from(travelCosts[mode, time][from, to])
    }

    fun distance(
        from: ZoneId,
        to: ZoneId,
    ): Distance {
        return distanceConverter.from(travelDistance[from, to])
    }

    fun duration(
        from: ZoneId,
        to: ZoneId,
        mode: Mode,
        time: Time,
    ): Duration {
        return timeConverter.from(travelTimes[mode, time][from, to])
    }

    companion object {
        @Suppress("LongParameterList")
        fun loadFromPaths(
            travelTimeYamlPath: Path,
            travelCostsYamlPath: Path,
            travelDistanceMatrixPath: Path,
            decoder: Decodable<Mode>,
            matrixFactory: ZoneMatrixCreation = VisumMatrixCreator,
            converter: UnitConverter = UnitConverter(),
        ): MatrixMetrics {
            val travelTimeMultiMatrix = YamlMatrixLookup.default(
                travelTimeYamlPath,
                decoder,
            ).cached(matrixFactory)

            val travelCostMultiMatrix = YamlMatrixLookup.default(
                travelCostsYamlPath,
                decoder,
            ).cached(matrixFactory)

            val travelDistanceMatrix = matrixFactory.createMatrix(YamlInfo("visum_matrix", travelDistanceMatrixPath))
            return MatrixMetrics(
                travelTimeMultiMatrix,
                travelCostMultiMatrix,
                travelDistanceMatrix,
                converter,
            )
        }
    }
}

/**
 * Collects conversion functions for the 3 main types of matrices found in the simulation.
 *
 * Defaults:
 * - time in minutes
 * - distance in kilometers
 * - currency in euros
 */
data class UnitConverter(
    val timeConverter: DoubleToDuration = DoubleToDuration { it.minutes },
    val distanceConverter: DoubleToDistance = DoubleToDistance { it.kilometers },
    val currencyConverter: DoubleToCurrency = DoubleToCurrency { it.euros },

) {

    companion object {
        fun fromUnits(
            distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
            currencyUnit: CurrencyUnit = CurrencyUnit.EUROS,
            timeUnit: DurationUnit = DurationUnit.MINUTES,
        ): UnitConverter {
            return UnitConverter(
                { it.toDuration(timeUnit) },
                { it.toDistance(distanceUnit) },
                { it.toCurrency(currencyUnit) },
            )
        }
    }
}
