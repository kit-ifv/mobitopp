package domain.shared.datastructure.matrix.binary

import domain.shared.datastructure.matrix.StandardMatrix
import utils.files.PathChecksum
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path

interface BinaryStandardSerializer : BinarySerializer {
    override fun serialize(checksum: PathChecksum, matrix: StandardMatrix, path: Path) {
        Files.newOutputStream(path).use { fileStream ->
            BufferedOutputStream(fileStream).use { bufferedStream ->
                bufferedStream.writeOutput(checksum, matrix)
            }
        }
    }

    private fun BufferedOutputStream.writeOutput(checksum: PathChecksum, matrix: StandardMatrix) {
        DataOutputStream(this).use { outputStream ->
            // Write the hash code used to identify the original source.
            outputStream.writeLong(checksum.value)
            // Write the size as an Int
            outputStream.writeInt(matrix.size)

            // Write all zoneIds (their corresponding Int values) from the translation map
            matrix.keys.forEach { idInt ->
                outputStream.writeInt(idInt.value.toInt())
            }
            // Write all values from the array
            writeContentArray(outputStream, matrix.values().toDoubleArray())
        }
    }
    fun writeContentArray(output: DataOutputStream, values: DoubleArray)
}