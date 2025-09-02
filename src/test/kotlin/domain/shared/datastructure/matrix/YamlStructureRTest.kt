package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.yaml.YamlMatrixLookupImpl
import domain.shared.enums.LegacyMode
import org.junit.jupiter.api.Assertions.*
import utils.units.sinceStart
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class YamlStructureRTest {
    @Test
    fun parsingOfYaml(){
        val yaml = YamlMatrixLookupImpl<LegacyMode>(
            yamlPath = Path("data/matrix/cost-matrix-configuration_transmove_turbo.yaml"),
            modeDecoder = LegacyMode.Companion,
        )
        val out = yaml[LegacyMode.PEDESTRIAN, 1.hours.sinceStart]
        val out2 = yaml[LegacyMode.PEDESTRIAN, 25.hours.sinceStart]

        assertEquals(out.expiration, Duration.INFINITE.sinceStart)
        assertEquals(out2.expiration, Duration.INFINITE.sinceStart)

    }


}