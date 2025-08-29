package domain.shared.datastructure.matrix

import core.datastructure.matrix.ZoneMatrixLookup
import core.datastructure.matrix.DEFAULT_PARSE
import core.datastructure.matrix.ZoneIdMatrix
import domain.shared.datastructure.matrix.visum.VisumMatrix
import domain.shared.enums.Mode
import domain.shared.location.CostMetric
import domain.shared.location.DistanceMetric
import domain.shared.location.DurationMetric
import domain.shared.location.Location
import domain.shared.location.Metrics
import units.euros
import units.kilometers
import utils.Decodable
import utils.units.Time
import java.nio.file.Path
import kotlin.time.Duration.Companion.minutes

class BigNewHolder(
    private val travelTimes: ZoneMatrixLookup<Mode>,
    private val travelCosts: ZoneMatrixLookup<Mode>,
    private val travelDistance: ZoneIdMatrix,
) : Metrics {


    override fun costMetric(mode: Mode, time: Time): CostMetric {
        return CostMetric { o: Location, d: Location ->
            travelCosts[mode, time][o, d].euros
        }
    }

    override fun distanceMetric(mode: Mode): DistanceMetric {
        return DistanceMetric { o, d ->
            travelDistance[o, d].kilometers
        }
    }

    override fun durationMetric(
        mode: Mode,
        time: Time,
    ): DurationMetric {

        return DurationMetric { o: Location, d: Location ->
            travelTimes[mode, time][o, d].minutes
        }
    }

    companion object {
        fun fromPaths(
            travelTimeYamlPath: Path,
            travelCostsYamlPath: Path,
            travelDistanceMatrixPath: Path,
            decoder: Decodable<Mode>,
        ): BigNewHolder {
            val travelTimeMultiMatrix = CachedMatrixLookup(
                yaml = YamlMatrixLookup(
                    travelTimeYamlPath,
                    decoder,
                ),
                matrixCreator = DEFAULT_PARSE
            )

            val travelCostMultiMatrix = CachedMatrixLookup(
                yaml = YamlMatrixLookup(
                    travelCostsYamlPath,
                    decoder,
                ),
                matrixCreator = DEFAULT_PARSE
            )
            val travelDistanceMatrix = VisumMatrix(travelDistanceMatrixPath)
            return BigNewHolder(
                travelTimeMultiMatrix,
                travelCostMultiMatrix,
                travelDistanceMatrix
            )
        }
    }
}