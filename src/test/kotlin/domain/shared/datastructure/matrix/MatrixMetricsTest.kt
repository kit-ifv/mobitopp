package domain.shared.datastructure.matrix

import domain.shared.enums.LegacyMode
import domain.shared.location.ZoneId
import org.junit.jupiter.api.Test
import units.kilometers
import utils.units.sinceStart
import kotlin.io.path.Path
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class MatrixMetricsTest {

    @Test
    fun isMyReadingRight() {
        val matrixMetrics = MatrixMetrics.loadFromPaths(
            travelTimeYamlPath = Path("data/matrix/time-matrix-configuration_transmove_turbo.yaml"),
            travelCostsYamlPath = Path("data/matrix/cost-matrix-configuration_transmove_turbo.yaml"),
            travelDistanceMatrixPath = Path("data/matrix/other/AIR_DIS.mtx.bz2"),
            decoder = LegacyMode.Companion,
        )
        assertEquals(matrixMetrics.distance(ZoneId(6113L), ZoneId(6114L), mode = LegacyMode.CAR), 0.362.kilometers)
        assertEquals(matrixMetrics.duration(ZoneId(6113L), ZoneId(6113L), mode = LegacyMode.CAR, 0.minutes.sinceStart), 2.48.minutes)
    }


}