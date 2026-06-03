package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.yaml.YamlInfo
import domain.shared.datastructure.matrix.yaml.YamlMatrixLookup
import domain.shared.enums.Mode
import domain.shared.location.CostMetric
import domain.shared.location.DistanceMetric
import domain.shared.location.DurationMetric
import domain.shared.location.Impedance
import domain.shared.location.zone.CostZoneMetric
import domain.shared.location.zone.DistanceZoneMetric
import domain.shared.location.zone.DurationZoneMetric
import domain.shared.location.zone.ZoneId
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import utils.codes.Decodable
import utils.units.Time
import java.nio.file.Path
import kotlin.time.Duration

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
) : Impedance {
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
        timeConverter.from(travelTimes[mode, time][o, d])
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

