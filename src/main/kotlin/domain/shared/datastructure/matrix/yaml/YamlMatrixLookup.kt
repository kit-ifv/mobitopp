package domain.shared.datastructure.matrix.yaml

import domain.shared.datastructure.matrix.CachedMatrixLookup
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import utils.Decodable
import utils.Encodable
import utils.ExpiringLookup
import java.nio.file.Path

fun interface YamlMatrixLookup<M> : ExpiringLookup<M, YamlInfo> {


    fun cached(matrixCreator: ZoneMatrixCreation): CachedMatrixLookup<M> {
        return CachedMatrixLookup(this, matrixCreator)
    }

    companion object {
        fun <M : Encodable> default(
            yamlPath: Path,
            modeDecoder: Decodable<M>,
            yamlParsingLogic: YamlParsingLogic = YamlParsingLogic.default(yamlPath),
        ): YamlMatrixLookup<M> {


            return YamlMatrixLookupImpl(yamlPath, modeDecoder, yamlParsingLogic)
        }
    }
}
