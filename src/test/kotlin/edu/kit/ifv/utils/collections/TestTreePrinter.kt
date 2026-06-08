package edu.kit.ifv.utils.collections
import edu.kit.ifv.utils.ConsoleCaptor
import kotlin.test.assertEquals

class TestTreePrinter {

    private fun tree(): Element {
        val f = Element("F", mutableListOf())
        val g = Element("G", mutableListOf())

        val c = Element("C", mutableListOf(f, g))
        val d = Element("D", mutableListOf())
        val e = Element("E", mutableListOf())

        val a = Element("A", mutableListOf(c))
        val b = Element("B", mutableListOf(d, e))

        return Element("root", mutableListOf(a, b))
    }

    private val expectedTree: String = """
    |root
    |├─ A
    |│ └─ C
    |│   ├─ F
    |│   └─ G
    |└─ B
    |  ├─ D
    |  └─ E
    """.trimMargin()

    // @Test
    fun printAsTree() {
        val captor = ConsoleCaptor()

        printAsTree(tree(), { it.name }) { it.children }

        val console = captor.getText()
        assertEquals(expectedTree, console)
    }
}

private data class Element(val name: String, val children: MutableList<Element>)

fun main() {
    val f = Element("F", mutableListOf())
    val g = Element("G", mutableListOf())

    val c = Element("C", mutableListOf(f, g))
    val d = Element("D", mutableListOf())
    val e = Element("E", mutableListOf())

    val a = Element("A", mutableListOf(c))
    val b = Element("B", mutableListOf(d, e))

    val root = Element("root", mutableListOf(a, b))

    printAsTree(root, { it.name }, false) { it.children }
}
