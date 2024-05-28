package codegeneration

import Buildable
import fakepackage.FakeClass
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration

class TestGeneration {

    @Test
    fun properGeneration() {
        val p = Path("build/generated/ksp/test/kotlin/codegeneration/Builders.kt")
        assertTrue(p.exists())
    }

    @Test
    fun building() {
        val b = DataBuilder()
        val d = b.build {
            i = 10
        }
        val d2 = b.build {
            i = 20
        }
        assertEquals(10, d.i)
        assertEquals(20, d2.i)
    }

    @Test
    fun collectionsAreCopies() {
        val b = ClassWithListBuilder()
        val d = b.build {
            text.add("A")
        }
        val d2 = b.build {
            text.add("B")
        }
        assertEquals("A", d.text[0])
        assertEquals("B", d2.text[0])
    }

    @Test
    fun missingAttributesCausesError() {
        val exception = assertFailsWith<IllegalArgumentException> {
            DataBuilder().build()
        }
        assertContains(exception.message!!, "i")
        assertContains(exception.message!!, "The following attributes")
    }

    @Test
    fun complexObjectsAreReferenced() {
        val b = ClassWithObjectInCollectionBuilder()
        val d = b.buildPreserving {
            t.add(SomeComplexObject(1))
        }
        val d2 = b.build {
            t.add(SomeComplexObject(2))
        }
        assertEquals(1, d.t[0].i)
        assertEquals(1, d2.t[0].i)
        assertEquals(2, d2.t[1].i)
        d2.t[0].changeTheAttribute()
        assertEquals(9001, d.t[0].i)
        assertEquals(9001, d2.t[0].i)
        assertEquals(2, d2.t[1].i)
    }

    @Test
    fun directObjectsAreNotReferenced() {
        val b = ClassWithObjectBuilder()
        val d = b.buildPreserving {
            o = SomeComplexObject(1)
        }
        val d2 = b.build {
        }
        val d3 = b.build {
            o = SomeComplexObject(2)
        }
        assertEquals(1, d.o.i)
        assertEquals(1, d2.o.i)
        assertEquals(2, d3.o.i)
        d.o.changeTheAttribute()
        assertEquals(9001, d2.o.i)
    }

    @Test
    fun interfacesDoNotHoldSharedState() {
        val b = InterfaceBuilder()
        b.apply { inti = 4 }
        val t1 = b.buildPreserving { }
        assertEquals(4, t1.inti)
        b.apply { inti = 5 }
        val t2 = b.build()
        assertEquals(4, t1.inti)
        assertEquals(5, t2.inti)
    }
    @Test
    fun interfaceIsDefaultable() {


        val test = listOf(true, true, true)

        val xe = when(test) {
            listOf(true, true, true) -> 1
            listOf(false, true, true) -> 2
            listOf(true, false, true) -> 3
            listOf(true, true, false) -> 4
            listOf(false, false, true) -> 5
            listOf(false, true, false) -> 6
            listOf(true, false, false) -> 7
            listOf(false, false, false) -> 8
            else -> -1

        }
        println(xe)
        val b = InterfaceWithDefaultBuilder()
        b.build {

        }
    }
}

data class TonsOfDefaults(
    val a: Int = 0,
    val b: Int = 0,
    val c: Int = 0,
    val d: Int = 0,
    val e: Int = 0,
    val f: Int = 0,
    val g: Int = 0,
    val h: Int = 0,
    val i: Int = 0,
)

@Buildable
data class IHaveADefault(val i: Int = 0)


@Buildable
data class IHaveDefaultAndNotDefault(val i: Int = 0, val j: Int)
@Buildable
class Generic<T : Number, S : CharSequence>(
    val t: T,
    val mapp: Map<T, S>,
    val id: Int = 0,
)

@Buildable
data class Data(val i: Int)

@Buildable
class ClassWithList(val text: List<String>)

@Buildable
class ClassWithMap(val text: Map<String, String>)

@Buildable
class ClassWithSet(val text: Set<String>)

@Buildable
class ClassWithMutableSet(val text: Set<String> = setOf("Nope"))

@Buildable
class ClassWithObject(val o: SomeComplexObject)

@Buildable
class ClassWithExternalRef(val o: FakeClass)

@Buildable
class ClassWithExternalR2ef(val o: FakeClass)

@Buildable
interface Interface {
    val inti: Int
}

@Buildable
interface Child : Interface

@Buildable
interface InterfaceWithAbstractFunctions {
    val inti: Int
    fun bruell(): String
    fun zuchini(): Boolean
}
@Buildable
interface InterfaceWithDefault {
    val int: Int
        get() = 0
}
@Buildable
abstract class AbstractClass(val text: String) {
    val secondaryAttribute: String
        get() = text.uppercase()

    fun yell(): String {
        return text
    }
    abstract fun abstractScream(): String
}

class SomeComplexObject(var i: Int) {
    fun changeTheAttribute() {
        i = 9001
    }
}

@Buildable
class ClassWithObjectInCollection(val t: List<SomeComplexObject>)

@Buildable
data class AllPrimitives(
    val byte: Byte,
    val short: Short,
    val i: Int,
    val l: Long,

    val f: Float,
    val d: Double,

    val ub: UByte,
    val us: UShort,
    val ui: UInt,
    val ul: ULong,

    val bool: Boolean,
    val c: Char,
    val str: String,

    val array: Array<SomeComplexObject>,
    val intArray: IntArray,
    val byteArray: ByteArray,
    val booleanArray: BooleanArray,
    val charArray: CharArray,
    val doubleArray: DoubleArray,
    val floatArray: FloatArray,
    val longArray: LongArray,
    val shortArray: ShortArray
)

@Buildable
class ValueHolder(
    val duration: Duration
)
