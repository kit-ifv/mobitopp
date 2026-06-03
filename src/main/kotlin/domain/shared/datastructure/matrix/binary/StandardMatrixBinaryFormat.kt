package domain.shared.datastructure.matrix.binary

interface StandardMatrixBinaryFormat :
    BinaryStandardSerializer,
    BinaryStandardDeserializer {
    val fileExtension: String
}