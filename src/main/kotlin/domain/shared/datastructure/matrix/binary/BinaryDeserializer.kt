package domain.shared.datastructure.matrix.binary

import domain.shared.datastructure.matrix.StandardMatrix
import java.nio.file.Path

interface BinaryDeserializer {
    fun deserialize(path: Path): StandardMatrix
}