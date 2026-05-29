package domain.shared.datastructure.matrix

import utils.Decodable
import utils.Encodable

internal enum class TestModes(override val code: Int, override val description: String) : Encodable {
    BIKESHARING(0, "bikesharing"),
    CAR(1, "car"),
    CARSHARING(2, "carsharing_free_floating"),
    ;

    companion object : Decodable<TestModes> {
        override fun values(): Set<TestModes> = entries.toSet()
    }
}
