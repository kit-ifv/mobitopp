package domain.shared.datastructure.matrix.yaml

import java.nio.file.Path

/**
 * The [YamlInfo] holds the information found in the yaml file, which splits into two components.
 * The [parserDescription] which is the key found in the file.
 * The [path] where the matrix resides on file.
 */
data class YamlInfo(
    val parserDescription: String,
    val path: Path,
)
