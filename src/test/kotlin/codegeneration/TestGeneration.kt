package codegeneration

import Mutable
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


class TestGeneration {


    @Test
    fun properGeneration() {
        val p = Path("build/generated/ksp/test/kotlin/codegeneration/Builders.kt")
        assertTrue(p.exists())

    }
    @Test
    fun building() {
        val b = MutableData()
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
        val b = MutableClassWithList()
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
        assertFailsWith<NullPointerException> {
            MutableData().build()
        }
    }
    @Test
    fun complexObjectsAreReferenced() {
        val b = MutableClassWithObjectInCollection()
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
        val b = MutableClassWithObject()
        val d = b.buildPreserving {
            o = SomeComplexObject(1)

        }
        val d2 = b.build {

        }
        val d3 = b.build {
            o  = SomeComplexObject(2)
        }
        assertEquals(1, d.o.i)
        assertEquals(1, d2.o.i)
        assertEquals(2, d3.o.i)
        d.o.changeTheAttribute()
        assertEquals(9001, d2.o.i)

    }
}
@Mutable
data class Data(val i: Int)
@Mutable
class ClassWithList(val text: List<String>)
@Mutable
class ClassWithMap(val text: Map<String, String>)
@Mutable
class ClassWithSet(val text: Set<String>)

@Mutable
class ClassWithMutableSet(val text: Set<String>)

@Mutable
class ClassWithObject(val o: SomeComplexObject)

class SomeComplexObject(var i: Int) {
    fun changeTheAttribute() {
        i = 9001
    }
}
@Mutable
class ClassWithObjectInCollection(val t: List<SomeComplexObject>)


