package datastructure

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import utils.collections.orderedPermutations
import utils.collections.permutations
import kotlin.test.Test
import kotlin.test.assertEquals

typealias LabeledRunnable = Pair<String, DualSetList<A, B, C>.() -> Unit>

open class A(protected val int: Int) : Comparable<A> {

    override fun compareTo(other: A): Int {
        return int.compareTo(other.int)
    }
}

class B(int: Int) : A(int) {
    override fun toString(): String {
        return "B($int)"
    }
}

class C(int: Int) : A(int) {
    override fun toString(): String {
        return "C($int)"
    }
}

class DualSetListTest {

    @Test
    fun individualStepsMaintainDataStructure() {
        val dualSetList = DualSetList<A, B, C>()

        dualSetList.addRight(C(5))
        dualSetList.assertEquals {
            container {
                s[5]
            }
        }
        dualSetList.addRight(C(7))
        dualSetList.assertEquals {
            container {
                s[5, 7]
            }
        }
        dualSetList.addLeft(B(2))
        dualSetList.assertEquals {
            container {
                f[2]
                s[5, 7]
            }
        }
        dualSetList.addLeft(B(4))
        dualSetList.assertEquals {
            container {
                f[2, 4]
                s[5, 7]
            }
        }

        dualSetList.assertEquals {
            container {
                f[2, 4]
                s[5, 7]
            }
        }

        dualSetList.addRight(C(3))
        dualSetList.assertEquals {
            container {
                f[2]
                s[3]
            }
            container {
                f[4]
                s[5, 7]
            }
        }
    }

    @TestFactory
    fun everyCombinationOfSixAdditionsAndOneIrrelevantRemoval(): List<DynamicTest> {
        val actions: List<LabeledRunnable> = listOf(
            "+B1" to { addLeft(B(1)) },
            "+B3" to { addLeft(B(3)) },
            "+B5" to { addLeft(B(5)) },
            "+C2" to { addRight(C(2)) },
            "+C4" to { addRight(C(4)) },
            "+C6" to { addRight(C(6)) },
            "-C~" to { removeRight(C(7)) }
        )
        return actions.permutations().toList().map { test ->
            DynamicTest.dynamicTest(test.map { it.first }.toString()) {
                val dualSetList = DualSetList<A, B, C>()
                test.forEach { dualSetList.apply(it.second).also { assertTrue(dualSetList.isConsistent()) } }
                dualSetList.assertEquals {
                    container {
                        f[1]
                        s[2]
                    }
                    container {
                        f[3]
                        s[4]
                    }
                    container {
                        f[5]
                        s[6]
                    }
                }
            }
        }
    }

    @TestFactory
    fun everyCombinationOfAddingAndRemovingFourElements(): List<DynamicTest> {
        val actions: List<Pair<LabeledRunnable, LabeledRunnable>> = listOf(
            Pair("+B1" to { addLeft(B(1)) }, "-B1" to { removeLeft(B(1)) }),
            Pair("+B3" to { addLeft(B(3)) }, "-B3" to { removeLeft(B(3)) }),
            Pair("+C2" to { addRight(C(2)) }, "-C2" to { removeRight(C(2)) }),
            Pair("+C4" to { addRight(C(4)) }, "-C4" to { removeRight(C(4)) }),
        )

        return actions.orderedPermutations().toList().map { test ->
            DynamicTest.dynamicTest(test.map { it.first }.toString()) {
                val dualSetList = DualSetList<A, B, C>()
                test.forEach {
                    dualSetList.apply(it.second)
                        .also { assertTrue(dualSetList.isConsistent()) { dualSetList.toString() } }
                }
                dualSetList.assertEquals {
                }
            }
        }
    }

    @TestFactory
    fun everyCombinationOfImpactfulAddRemovePairs(): List<DynamicTest> {
        val actions: List<Pair<LabeledRunnable, LabeledRunnable>> = listOf(
            Pair("+B4" to { addLeft(B(4)) }, "-B4" to { removeLeft(B(4)) }),
            Pair("+C2" to { addRight(C(2)) }, "-C2" to { removeRight(C(2)) }),
            Pair("+C5" to { addRight(C(5)) }, "-C5" to { removeRight(C(5)) }),
            Pair("-C~" to { removeRight(C(3)) }, "-B~" to { removeLeft(B(3)) })

        )

        return actions.orderedPermutations().toList().map { test ->
            DynamicTest.dynamicTest(test.map { it.first }.toString()) {
                val dualSetList = DualSetList<A, B, C>()
                test.forEach {
                    dualSetList.apply(it.second)
                        .also { assertTrue(dualSetList.isConsistent()) { dualSetList.toString() } }
                }
                dualSetList.assertEquals {
                }
            }
        }
    }

    @TestFactory
    fun everyCombinationOfSomeDoubleAddedElements(): List<DynamicTest> {
        val actions: List<Pair<LabeledRunnable, LabeledRunnable>> = listOf(
            Pair("+B1" to { addLeft(B(1)) }, "+C5" to { addRight(C(5)) }),
            Pair("+B4" to { addLeft(B(4)) }, "-B4" to { removeLeft(B(4)) }),
            Pair("+C2" to { addRight(C(2)) }, "-C2" to { removeRight(C(2)) }),
            Pair("+B1'" to { addLeft(B(1)) }, "+C5'" to { addRight(C(5)) }),
        )

        return actions.orderedPermutations().toList().map { test ->
            DynamicTest.dynamicTest(test.map { it.first }.toString()) {
                val dualSetList = DualSetList<A, B, C>()
                test.forEach {
                    dualSetList.apply(it.second)
                        .also { assertTrue(dualSetList.isConsistent()) { dualSetList.toString() } }
                }
                dualSetList.assertEquals {
                    container {
                        f[1]
                        s[5]
                    }
                }
            }
        }
    }

    private fun DualSetList<A, B, C>.assertEquals(functor: SetListBuilder.() -> Unit) {
        val b = SetListBuilder()
        b.apply(functor)
        assertEquals(b.elements, entries)
        assertEquals(persistentSet, b.elements.flatMap { it.left + it.right }.toSet())
    }
}
