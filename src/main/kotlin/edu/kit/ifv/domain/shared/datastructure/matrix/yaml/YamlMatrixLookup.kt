package edu.kit.ifv.domain.shared.datastructure.matrix.yaml
import edu.kit.ifv.domain.shared.datastructure.matrix.CachedMatrixLookup
import edu.kit.ifv.domain.shared.datastructure.matrix.ZoneMatrixCreation
import edu.kit.ifv.utils.ExpiringLookup
import edu.kit.ifv.utils.codes.Decodable
import edu.kit.ifv.utils.codes.Encodable
import java.nio.file.Path

interface YamlMatrixLookup<M: Encodable> : ExpiringLookup<M, YamlInfo> {
    val codeRange: IntRange
    fun cached(matrixCreator: ZoneMatrixCreation): CachedMatrixLookup<M> = CachedMatrixLookup(this, matrixCreator)
    companion object {
        fun <M : Encodable> default(
            yamlPath: Path,
            modeDecoder: Decodable<M>,
            yamlParsingLogic: YamlParsingLogic = YamlParsingLogic.default(yamlPath),
        ): YamlMatrixLookup<M> = YamlMatrixLookupImpl(yamlPath, modeDecoder, yamlParsingLogic)
    }
}
