package edu.kit.ifv.domain.shared.datastructure.matrix
import edu.kit.ifv.domain.shared.datastructure.matrix.yaml.YamlInfo
import edu.kit.ifv.domain.shared.datastructure.matrix.yaml.YamlMatrixLookup
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.CostMetric
import edu.kit.ifv.domain.shared.location.DistanceMetric
import edu.kit.ifv.domain.shared.location.DurationMetric
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.IndexAddressableImpedance
import edu.kit.ifv.domain.shared.location.zone.CostZoneMetric
import edu.kit.ifv.domain.shared.location.zone.DistanceZoneMetric
import edu.kit.ifv.domain.shared.location.zone.DurationZoneMetric
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import edu.kit.ifv.utils.codes.Decodable
import edu.kit.ifv.utils.units.Time
import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Provides [Impedance] backed by zone-based OD matrices.
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
data class MatrixImpedance(
    private val travelTimes: ZoneMatrixLookup<Mode>,
    private val travelCosts: ZoneMatrixLookup<Mode>,
    private val travelDistance: ZoneIdMatrix,
    private val unitConverters: UnitConverter,
) : IndexAddressableImpedance {
    private val currencyConverter = unitConverters.currencyConverter
    private val timeConverter = unitConverters.timeConverter
    private val distanceConverter = unitConverters.distanceConverter

    override fun costMetric(mode: Mode, time: Time): CostMetric = CostZoneMetric { o, d ->
        currencyConverter.from(travelCosts[mode, time][o, d])
    }

    override fun distanceMetric(mode: Mode): DistanceMetric = DistanceZoneMetric { o, d ->
        distanceConverter.from(travelDistance[o, d])
    }

    override fun durationMetric(mode: Mode, time: Time): DurationMetric = DurationZoneMetric { o, d ->
        val matrix: ZoneIdMatrix = travelTimes[mode, time]
        val matrixAccess: Double = matrix[o, d]
        matrixAccess.minutes
    }

    override fun costIndexed(
        fromIndex: Int,
        toIndex: Int,
        mode: Mode,
        time: Time,
    ): Double {
        return travelCosts[mode, time].getIndexed(fromIndex, toIndex)
    }

    override fun distanceIndexed(
        fromIndex: Int,
        toIndex: Int,
        mode: Mode,
    ): Double {
        return travelDistance.getIndexed(fromIndex, toIndex)
    }

    override fun durationIndexed(
        fromIndex: Int,
        toIndex: Int,
        mode: Mode,
        time: Time,
    ): Double {
        return travelTimes[mode, time].getIndexed(fromIndex, toIndex)
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
        ): MatrixImpedance {
            val travelTimeMultiMatrix = YamlMatrixLookup.default(
                travelTimeYamlPath,
                decoder,
            ).cached(matrixFactory)

            val travelCostMultiMatrix = YamlMatrixLookup.default(
                travelCostsYamlPath,
                decoder,
            ).cached(matrixFactory)

            val travelDistanceMatrix = matrixFactory.createMatrix(
                YamlInfo("visum_matrix", travelDistanceMatrixPath),
            )

            return MatrixImpedance(
                travelTimeMultiMatrix,
                travelCostMultiMatrix,
                travelDistanceMatrix,
                converter,
            )
        }
    }
}
