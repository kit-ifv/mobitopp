package utils.csv

import kotlinx.serialization.Serializable
import utils.Identifiable
import kotlin.time.Duration

const val INDEX_COL = "index"
const val BOOL_COL = "bool"
const val INT_COL = "int"
const val FLOAT_COL = "float"
const val STR_COL = "str"

val COLUMNS = listOf(INDEX_COL, BOOL_COL, INT_COL, FLOAT_COL, STR_COL)

@Serializable
@JvmInline
value class TestId(val value: Long) : Comparable<TestId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: TestId): Int {
        return value.compareTo(other.value)
    }

    /**
     * @return the next higher id.
     */
    fun next(): TestId {
        return TestId(value + 1)
    }
}

interface ImmutableEntity : Identifiable<TestId> {
    val rowIndex: Int
    val csvIndex: Int
    val string: String
    val byte: Byte
    val short: Short
    val int: Int
    val long: Long
    val float: Float
    val double: Double
    val bool: Boolean
    val duration: Duration?
}

data class TestEntity(
    override var rowIndex: Int,
    override var csvIndex: Int = -1,
    override var string: String = "",
    override var byte: Byte = 0,
    override var short: Short = 0,
    override var int: Int = 0,
    override var long: Long = 0,
    override var float: Float = 0.0f,
    override var double: Double = 0.0,
    override var bool: Boolean = false,
    override var duration: Duration? = null,
) : Identifiable<TestId>, ImmutableEntity {
    override val id: TestId
        get() = TestId(rowIndex.toLong())
}

val expectedElements: List<TestEntity>
    get() = expectedElementSource.map { it.copy() }

private val expectedElementSource = listOf(
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

val expectedElementsMappedStringLength: List<TestEntity>
    get() = expectedElementsMappedStringLengthSource.map { it.copy() }

private val expectedElementsMappedStringLengthSource = listOf(
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
