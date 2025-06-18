package domain.shared.datastructure.matrix

//
// enum class MatrixImpl {
//    VisumMatrix,
//    ConstMatrix,
//    FloatMatrixInternal;
//
//    fun <I, O> getMatrix(
//        path: Path,
//        converter: (Double) -> O,
//        betterFormatFolder: InternalMatrixLookup? = null
//    ): Matrix<I, O> where I : ID<*> {
//        val outputPath = betterFormatFolder?.let {
//            Path(
//                path.toString().replace(it.originalDirectory.toString(), it.internalDirectory.toString())
//                    .removeSuffix(path.extension) + "bin"
//            )
//        }
//
//        outputPath?.let {
//            val file = it.toFile()
//            if (file.exists()) {
//                @Suppress("UNCHECKED_CAST")
//                return (FloatMatrix.fromPath(file.toPath(), converter) as? Matrix<I, O>)
//                    ?: throw YamlMultiMatrixError("Bad Matrix", path)
//            }
//        }
//
//        return when (this) {
//            VisumMatrix -> {
//                VisumMatrix(path, converter)
//            }
//
//            ConstMatrix -> {
//                val constant: O = converter(path.toString().toDouble())
//                ConstantMatrix(constant)
//            }
//
//            FloatMatrixInternal -> {
//                FloatMatrix.fromPath(path, converter)
//            }
//        }.let {
//            // The following suppresses the unchecked cast warning because we are unable to verify it at compile time due to the dynamic nature of this cast.
//            // Unfortunately, Kotlin does not provide a more elegant solution for this scenario.
//            @Suppress("UNCHECKED_CAST")
//            (it as? Matrix<I, O>) ?: throw YamlMultiMatrixError("Invalid type for VisumMatrix", path)
//        }
//    }
//
//    companion object {
//        fun fromString(value: String, path: String): MatrixImpl {
//            return when (value.lowercase(Locale.getDefault())) {
//                "visum_matrix" -> VisumMatrix
//                "constant_matrix" -> ConstMatrix
//                "float_matrix" -> FloatMatrixInternal
//                else -> throw YamlMultiMatrixError("Unknown parser: $value", Path.of(path))
//            }
//        }
//    }
// }
