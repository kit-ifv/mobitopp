package domain.shared.datastructure.matrix

import core.datastructure.matrix.FloatMatrix
import core.datastructure.matrix.Matrix
import core.datastructure.matrix.MatrixFormat
import core.datastructure.matrix.MatrixParser
import domain.shared.location.ZoneId
import utils.binary.operateOnMemoryFile
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension

@Suppress("MagicNumber") // 4 is not magic, it is the size of an int/float respectively
fun Path.getSize(): Triple<Int, Map<ZoneId, Int>, FloatArray> {
    return operateOnMemoryFile {
        val size = this.getInt(0)
        val mapper = HashMap<ZoneId, Int>()
        val translation = IntArray(size)
        for (i in 0 until size) {
            val fileContent = this.getInt((i + 1) * 4)
            mapper[ZoneId(fileContent.toLong())] = i
            translation[i] = this.getInt((i + 1) * 4)
        }
        val floatArray = FloatArray(size * size)
        for (i in floatArray.indices) {
            floatArray[i] = this.getFloat((i + 1 + size) * 4)
        }
        Triple(size, mapper, floatArray)
    }
}

val BinaryZoneFloatMatrixFormat = MatrixFormat(
    key = "float_matrix",
    parser = object : MatrixParser<ZoneId> {
        override fun <O> getMatrix(path: Path, converter: (Double) -> O): Matrix<ZoneId, O> {
            val (size, translation, floatArray) = path.getSize()
            return FloatMatrix(size, translation, floatArray, converter)
        }
    }
)

data class InternalMatrixLookup(
    val originalDirectory: Path,
    val internalDirectory: Path,
)

class CachedBinaryMatrixParser(
    private val delegate: MatrixParser<ZoneId>,
    private val betterFormatFolder: InternalMatrixLookup
) : MatrixParser<ZoneId> {

    override fun <O> getMatrix(
        path: Path,
        converter: (Double) -> O
    ): Matrix<ZoneId, O> {
        val outputPath = betterFormatFolder.let {
            Path(
                path.toString().replace(
                    it.originalDirectory.toString(),
                    it.internalDirectory.toString()
                ).removeSuffix(
                    path.extension
                ) + "bin"
            )
        }

        outputPath.let {
            val file = it.toFile()
            if (file.exists()) {
                return BinaryZoneFloatMatrixFormat.getMatrix(path, converter)
            }
        }

        return delegate.getMatrix(path, converter)
    }
}

fun <I> Collection<MatrixFormat<ZoneId>>.checkBinaryCache(
    betterFormatFolder: InternalMatrixLookup
) = this.map {
    MatrixFormat(it.key, CachedBinaryMatrixParser(it.parser, betterFormatFolder))
}

@Suppress("NestedBlockDepth")
fun <O> FloatMatrix<ZoneId, O>.writeToBinary(path: Path) {
    Files.newOutputStream(path).use { fileStream ->
        BufferedOutputStream(fileStream).use { bufferedStream ->
            DataOutputStream(bufferedStream).use { outputStream ->
                // Write the size as an Int
                outputStream.writeInt(size)

                // Write all zoneIds (their corresponding Int values) from the translation map
                translation.keys.forEach { idInt ->
                    outputStream.writeInt(idInt.value.toInt())
                }

                // Write all floats from the floatArray
                floatArray.forEach { floatValue ->
                    outputStream.writeFloat(floatValue)
                }
            }
        }
    }
}
