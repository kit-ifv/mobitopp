package utils.csv

import Builder
import ID
import utils.Identifiable
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
        get() = ID(rowIndex.toLong())
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

val expectedElements: List<TestEntity> = listOf(
    TestEntity(rowIndex = 0, string = "a"),
    TestEntity(rowIndex = 1, string = "Hello; World"),
    TestEntity(rowIndex = 2, string = "42"),
    TestEntity(rowIndex = 3, string = "exitProcess(1)"),
    TestEntity(rowIndex = 4, string = "test"),
    TestEntity(rowIndex = 5, string = "%&#)!?"),
    TestEntity(rowIndex = 6, string = "1+2*3"),
    TestEntity(rowIndex = 7, string = "mobiTopp"),
    TestEntity(rowIndex = 8, string = "IfV"),
    TestEntity(rowIndex = 9, string = "fin"),
)

val expectedElementsMappedStringLength: List<TestEntity> = listOf(
    TestEntity(rowIndex = 0, string = "a", int = 1),
    TestEntity(rowIndex = 1, string = "Hello; World", int = 12),
    TestEntity(rowIndex = 2, string = "42", int = 2),
    TestEntity(rowIndex = 3, string = "exitProcess(1)", int = 14),
    TestEntity(rowIndex = 4, string = "test", int = 4),
    TestEntity(rowIndex = 5, string = "%&#)!?", int = 6),
    TestEntity(rowIndex = 6, string = "1+2*3", int = 5),
    TestEntity(rowIndex = 7, string = "mobiTopp", int = 8),
    TestEntity(rowIndex = 8, string = "IfV", int = 3),
    TestEntity(rowIndex = 9, string = "fin", int = 3),
)

val expectedBuilders: List<TestBuilder> = listOf(
    TestBuilder(rowIndex = 0, string = "a"),
    TestBuilder(rowIndex = 1, string = "Hello; World"),
    TestBuilder(rowIndex = 2, string = "42"),
    TestBuilder(rowIndex = 3, string = "exitProcess(1)"),
    TestBuilder(rowIndex = 4, string = "test"),
    TestBuilder(rowIndex = 5, string = "%&#)!?"),
    TestBuilder(rowIndex = 6, string = "1+2*3"),
    TestBuilder(rowIndex = 7, string = "mobiTopp"),
    TestBuilder(rowIndex = 8, string = "IfV"),
    TestBuilder(rowIndex = 9, string = "fin"),
)

val expectedBuildersMappedStringLength: List<TestBuilder> = listOf(
    TestBuilder(rowIndex = 0, string = "a", int = 1),
    TestBuilder(rowIndex = 1, string = "Hello; World", int = 12),
    TestBuilder(rowIndex = 2, string = "42", int = 2),
    TestBuilder(rowIndex = 3, string = "exitProcess(1)", int = 14),
    TestBuilder(rowIndex = 4, string = "test", int = 4),
    TestBuilder(rowIndex = 5, string = "%&#)!?", int = 6),
    TestBuilder(rowIndex = 6, string = "1+2*3", int = 5),
    TestBuilder(rowIndex = 7, string = "mobiTopp", int = 8),
    TestBuilder(rowIndex = 8, string = "IfV", int = 3),
    TestBuilder(rowIndex = 9, string = "fin", int = 3),
)
