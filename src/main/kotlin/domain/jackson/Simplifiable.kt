package domain.jackson

interface Simplifiable<TO : BinaryWritable> {
    fun simplify(): TO
}
