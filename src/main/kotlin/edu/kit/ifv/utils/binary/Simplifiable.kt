package edu.kit.ifv.utils.binary
interface Simplifiable<TO : BinaryWritable> {
    fun simplify(): TO
}
