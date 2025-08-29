package core.datastructure.matrix

import java.nio.file.Path

private val matrixFormatKeyRegex = Regex("^[A-Za-z_]+$")

interface MatrixParser<I> {
    fun getMatrix(path: Path): IndexedDoubleMatrix<I>
}

data class MatrixFormat<I>(
    val key: String,
    val parser: MatrixParser<I>,
) : MatrixParser<I> by parser {
    init {
        require(key.matches(matrixFormatKeyRegex)) { "Key may only contain letters and underscores: $key" }
        require(key.isNotEmpty()) { "Key must not be empty" }
    }
}

fun <I> Collection<MatrixFormat<I>>.fromString(value: String, path: String) =
    this.firstOrNull {
        it.key == value
    } ?: throw YamlMultiMatrixError("Unknown parser: $value", Path.of(path))

class ConstantMatrixParser<I> : MatrixParser<I> {

    override fun getMatrix(path: Path): IndexedDoubleMatrix<I> {
        val constant = path.toString().toDouble()
        return ConstantMatrix(constant)
    }
}

fun <I> constantMatrixFormat() = MatrixFormat<I>(
    key = "constant_matrix",
    parser = ConstantMatrixParser(),
)
