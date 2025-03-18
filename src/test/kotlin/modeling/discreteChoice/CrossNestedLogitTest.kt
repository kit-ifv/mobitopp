package modeling.discreteChoice

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
private class Situation(override val choice: Int): ChoiceSituation<Int>() {
    companion object {
        fun fromInt(int: Int): Situation = Situation(int)
    }
}

private data class Parameters(val base: Double = 1.0)
class CrossNestedLogitTest {
    private fun CrossNestedLogit<Int, ChoiceSituation<Int>, Parameters>.calculateQuickly(vararg targets: Int): Map<Int, Double> {
        return calculateProbabilities(targets.map { Situation.fromInt(it) }.toSet(), Parameters()).mapKeys { it.key.choice }
    }


    @Test
    fun simpleCrossNested() {
        val distributionFunction = CrossNestedLogit.build<Int, ChoiceSituation<Int>, Parameters> {
            structure {
                option(1)
                option(2)
            }
        }
        distributionFunction.setUtilityFunctions {
            option(1) {
                0.0
            }
            option(2) {
                0.0
            }
        }
        distributionFunction.calculateQuickly(1).let {
            assertEquals(it.values.sum(), 1.0)
            assertEquals(it[1], 1.0)
        }
        distributionFunction.calculateQuickly(1, 2).let {
            assertEquals(it.values.sum(), 1.0)
            assertEquals(it[1], 0.5)
            assertEquals(it[2], 0.5)
        }
        assertThrows<NoSuchElementException> {
            distributionFunction.calculateQuickly(3)
        }




    }
    @Test
    fun simpleCrossNestedWithAlpha() {
        var alpha1 = 0.5
        var alpha2 = 0.5
        var u1 = 0.0
        var u2 = 0.0
        var u3 = 0.0
        var lambda1 = 1.0
        var lambda2 = 1.0
        val distributionFunction = CrossNestedLogit.build<Int, ChoiceSituation<Int>, Parameters> {
            structure {
                nest(lambda = {lambda1}) {
                    option(1, alpha = {alpha1})
                    option(2)
                }
                nest(lambda = {lambda2}) {
                    option(1, alpha = {alpha2})
                    option(3)
                }

            }


        }
        distributionFunction.setUtilityFunctions {
            option(1) {
                u1
            }
            option(2) {
                u2
            }
            option(3) {
                u3
            }
        }
        distributionFunction.calculateQuickly(1, 2, 3).let {
            assertEquals(it.values.sum(), 1.0)
            assertEquals(it[1]!!, 0.333333333, 0.0001)
            assertEquals(it[2]!!, 0.333333333, 0.0001)
            assertEquals(it[3]!!, 0.333333333, 0.0001)
        }
        alpha1 = 0.2
        alpha2 = 0.8
        distributionFunction.calculateQuickly(1, 2, 3).let {
            assertEquals(it.values.sum(), 1.0)
            assertEquals(it[1]!!, 0.333333333, 0.0001)
            assertEquals(it[2]!!, 0.333333333, 0.0001)
            assertEquals(it[3]!!, 0.333333333, 0.0001)
        }
        lambda1 = 0.0001
        distributionFunction.calculateQuickly(1, 2, 3).let {
            assertEquals(it.values.sum(), 1.0)
            assertEquals(it[1]!!, 0.333333333, 0.0001)
            assertEquals(it[2]!!, 0.333333333, 0.0001)
            assertEquals(it[3]!!, 0.333333333, 0.0001)
        }
    }
    @Test
    fun crossnestedWithLambdaIsValid() {
        var lambda1 = 1.0
        val distributionFunction = CrossNestedLogit.build<Int, ChoiceSituation<Int>, Parameters> {
            structure {
                nest(name = "Nest 1", lambda = {lambda1}) {
                    option(1, alpha = {0.8})
                    option(2)
                }

                nest(name = "Nest 2", lambda = {0.000001}) {
                    option(1, alpha = {0.2})
                    option(3)
                }



            }


        }
        distributionFunction.setUtilityFunctions {
            option(1) {
                0.0
            }
            option(2) {
                0.0
            }
        }

        distributionFunction.calculateQuickly(1, 2).let {
            assertEquals(it.values.sum(), 1.0)
        }
    }
}