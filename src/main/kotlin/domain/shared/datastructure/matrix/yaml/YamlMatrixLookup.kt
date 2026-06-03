package domain.shared.datastructure.matrix.yaml

import domain.shared.datastructure.matrix.CachedMatrixLookup
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import utils.ExpiringLookup
import utils.codes.Decodable
import utils.codes.Encodable
import java.nio.file.Path

fun interface YamlMatrixLookup<M> : ExpiringLookup<M, YamlInfo> {

    fun cached(matrixCreator: ZoneMatrixCreation): CachedMatrixLookup<M> = CachedMatrixLookup(this, matrixCreator)

    companion object {
        fun <M : Encodable> default(
            yamlPath: Path,
            modeDecoder: Decodable<M>,
            yamlParsingLogic: YamlParsingLogic = YamlParsingLogic.default(yamlPath),
        ): YamlMatrixLookup<M> = YamlMatrixLookupImpl(yamlPath, modeDecoder, yamlParsingLogic)
    }
}
