package domain.shared.datastructure.matrix

import domain.shared.enums.LegacyMode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class BigNewHolderTest {

    @Test
    fun isMyReadingRight() {
        val bigBoi = BigNewHolder.fromPaths(
            travelTimeYamlPath = Path("data/matrix/time-matrix-configuration_transmove_turbo.yaml"),
            travelCostsYamlPath = Path("data/matrix/cost-matrix-configuration_transmove_turbo.yaml"),
            travelDistanceMatrixPath = Path("data/matrix/other/AIR_DIS.mtx.bz2"),
            decoder = LegacyMode.Companion,
        )

        println(bigBoi)
    }





}