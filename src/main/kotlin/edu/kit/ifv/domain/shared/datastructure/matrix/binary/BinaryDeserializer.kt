package edu.kit.ifv.domain.shared.datastructure.matrix.binary
import edu.kit.ifv.domain.shared.datastructure.matrix.StandardMatrix
import java.nio.file.Path

interface BinaryDeserializer {
    fun deserialize(path: Path): StandardMatrix
}
