package utils.csv

import Builder
import ID
import Identifiable
import kotlin.time.Duration

const val INDEX_COL = "index"
const val BOOL_COL = "bool"
const val INT_COL = "int"
const val FLOAT_COL = "float"
const val STR_COL = "str"

val COLUMNS = listOf(INDEX_COL, BOOL_COL, INT_COL, FLOAT_COL, STR_COL)

data class TestEntity(
    var rowIndex: Int,
    var csvIndex: Int = -1,
    var string: String = "",
    var byte: Byte = 0,
    var short: Short = 0,
    var int: Int = 0,
    var long: Long = 0,
    var float: Float = 0.0f,
    var double: Double = 0.0,
    var bool: Boolean = false,
    var duration: Duration? = null,
): Identifiable<TestEntity> {
    override val id: ID<TestEntity>
        get() = ID(rowIndex.toULong())
}

data class TestBuilder(
    var rowIndex: Int,
    var csvIndex: Int = -1,
    var string: String = "",
    var byte: Byte = 0,
    var short: Short = 0,
    var int: Int = 0,
    var long: Long = 0,
    var float: Float = 0.0f,
    var double: Double = 0.0,
    var bool: Boolean = false,
    var duration: Duration? = null,
): Builder<TestEntity> {
    override fun build() = TestEntity(
        rowIndex, csvIndex, string, byte, short, int, long, float, double, bool, duration
    )
}
