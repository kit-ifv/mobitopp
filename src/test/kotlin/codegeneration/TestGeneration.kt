package codegeneration

import Mutable
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


class TestGeneration {


    @Test
    fun properGeneration() {
        val p = Path("build/generated/ksp/test/kotlin/codegeneration/Builders.kt")
        val text = p.readText()
        assertTrue(p.exists())
        assertEquals(TARGET, text)
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

const val TARGET = "package codegeneration\n" +
        "class MutableData() {\n" +
        "    var i : Int? = null\n" +
        "    fun buildPreserving(lambda : MutableData.() -> Unit) : Data {\n" +
        "        this.apply(lambda)\n" +
        "        return build()\n" +
        "    }\n" +
        "    fun build(lambda: MutableData.() -> Unit) : Data {\n" +
        "        val result = buildPreserving(lambda)\n" +
        "        reset()\n" +
        "        return result\n" +
        "    }\n" +
        "    fun reset() {\n" +
        "        i = null\n" +
        "    }\n" +
        "    fun build(): Data {\n" +
        "        return Data(i!!)\n" +
        "    }\n" +
        "}\n" +
        "class MutableClassWithList() {\n" +
        "    val text : MutableList<String> = mutableListOf()\n" +
        "    fun buildPreserving(lambda : MutableClassWithList.() -> Unit) : ClassWithList {\n" +
        "        this.apply(lambda)\n" +
        "        return build()\n" +
        "    }\n" +
        "    fun build(lambda: MutableClassWithList.() -> Unit) : ClassWithList {\n" +
        "        val result = buildPreserving(lambda)\n" +
        "        reset()\n" +
        "        return result\n" +
        "    }\n" +
        "    fun reset() {\n" +
        "        text.clear()\n" +
        "    }\n" +
        "    fun build(): ClassWithList {\n" +
        "        return ClassWithList(text.toList())\n" +
        "    }\n" +
        "}\n" +
        "class MutableClassWithMap() {\n" +
        "    val text : MutableMap<String, String> = mutableMapOf()\n" +
        "    fun buildPreserving(lambda : MutableClassWithMap.() -> Unit) : ClassWithMap {\n" +
        "        this.apply(lambda)\n" +
        "        return build()\n" +
        "    }\n" +
        "    fun build(lambda: MutableClassWithMap.() -> Unit) : ClassWithMap {\n" +
        "        val result = buildPreserving(lambda)\n" +
        "        reset()\n" +
        "        return result\n" +
        "    }\n" +
        "    fun reset() {\n" +
        "        text.clear()\n" +
        "    }\n" +
        "    fun build(): ClassWithMap {\n" +
        "        return ClassWithMap(text.toMap())\n" +
        "    }\n" +
        "}\n" +
        "class MutableClassWithSet() {\n" +
        "    val text : MutableSet<String> = mutableSetOf()\n" +
        "    fun buildPreserving(lambda : MutableClassWithSet.() -> Unit) : ClassWithSet {\n" +
        "        this.apply(lambda)\n" +
        "        return build()\n" +
        "    }\n" +
        "    fun build(lambda: MutableClassWithSet.() -> Unit) : ClassWithSet {\n" +
        "        val result = buildPreserving(lambda)\n" +
        "        reset()\n" +
        "        return result\n" +
        "    }\n" +
        "    fun reset() {\n" +
        "        text.clear()\n" +
        "    }\n" +
        "    fun build(): ClassWithSet {\n" +
        "        return ClassWithSet(text.toSet())\n" +
        "    }\n" +
        "}\n" +
        "class MutableClassWithMutableSet() {\n" +
        "    val text : MutableSet<String> = mutableSetOf()\n" +
        "    fun buildPreserving(lambda : MutableClassWithMutableSet.() -> Unit) : ClassWithMutableSet {\n" +
        "        this.apply(lambda)\n" +
        "        return build()\n" +
        "    }\n" +
        "    fun build(lambda: MutableClassWithMutableSet.() -> Unit) : ClassWithMutableSet {\n" +
        "        val result = buildPreserving(lambda)\n" +
        "        reset()\n" +
        "        return result\n" +
        "    }\n" +
        "    fun reset() {\n" +
        "        text.clear()\n" +
        "    }\n" +
        "    fun build(): ClassWithMutableSet {\n" +
        "        return ClassWithMutableSet(text.toSet())\n" +
        "    }\n" +
        "}\n" +
        "class MutableClassWithObject() {\n" +
        "    var o : SomeComplexObject? = null\n" +
        "    fun buildPreserving(lambda : MutableClassWithObject.() -> Unit) : ClassWithObject {\n" +
        "        this.apply(lambda)\n" +
        "        return build()\n" +
        "    }\n" +
        "    fun build(lambda: MutableClassWithObject.() -> Unit) : ClassWithObject {\n" +
        "        val result = buildPreserving(lambda)\n" +
        "        reset()\n" +
        "        return result\n" +
        "    }\n" +
        "    fun reset() {\n" +
        "        o = null\n" +
        "    }\n" +
        "    fun build(): ClassWithObject {\n" +
        "        return ClassWithObject(o!!)\n" +
        "    }\n" +
        "}\n" +
        "class MutableClassWithObjectInCollection() {\n" +
        "    val t : MutableList<SomeComplexObject> = mutableListOf()\n" +
        "    fun buildPreserving(lambda : MutableClassWithObjectInCollection.() -> Unit) : ClassWithObjectInCollection"+
        "{\n" +
        "        this.apply(lambda)\n" +
        "        return build()\n" +
        "    }\n" +
        "    fun build(lambda: MutableClassWithObjectInCollection.() -> Unit) : ClassWithObjectInCollection {\n" +
        "        val result = buildPreserving(lambda)\n" +
        "        reset()\n" +
        "        return result\n" +
        "    }\n" +
        "    fun reset() {\n" +
        "        t.clear()\n" +
        "    }\n" +
        "    fun build(): ClassWithObjectInCollection {\n" +
        "        return ClassWithObjectInCollection(t.toList())\n" +
        "    }\n" +
        "}\n"
