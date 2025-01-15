package synthesis

import synthesis.fixedDestinations.CommuterMatrix
import kotlin.test.Test

class CommuterMatrixTest {

    @Test
    fun commuterRelationParse() {
        val commuterMatrix = CommuterMatrix.parse(zoneMapping = emptyMap())
    }
}
