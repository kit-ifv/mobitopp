package utils.binary

interface Simplifiable<TO : BinaryWritable> {
    fun simplify(): TO
}
