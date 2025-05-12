package modeling.discreteChoice

import modeling.discreteChoice.structure.CrossNestedStructure
import modeling.discreteChoice.utility.crossNestedLogit
import modeling.models.ChoiceAlternative
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

private class Alternative(override val choice: Int) : ChoiceAlternative<Int>() {
    companion object {
        fun fromInt(int: Int): Alternative = Alternative(int)
    }
}

private data class Parameters(val base: Double = 1.0)

class CrossNestedLogitTest {

    private fun DiscreteChoiceModel<Int, ChoiceAlternative<Int>, Parameters>.castProbabilities(
        vararg targets: Int
    ): Map<Int, Double> {
        return probabilities(
            targets.map { Alternative.fromInt(it) }.toSet(),
        ).mapKeys { it.key.choice }
    }

    @Test
    fun simpleCrossNested() {
        val crossNestedModel = CrossNestedStructure<Int, ChoiceAlternative<Int>, Parameters> {
            option(1)
            option(2)
        }.withUtility {
            option(1) {
                0.0
            }
            option(2) {
                0.0
            }
        }.crossNestedLogit("SimpleCrossNestedModelForTesting").build(Parameters()).model

        crossNestedModel.castProbabilities(1).let {
            assertEquals(it.values.sum(), 1.0)
            assertEquals(it[1], 1.0)
        }

        crossNestedModel.castProbabilities(1, 2).let {
            assertEquals(it.values.sum(), 1.0)
            assertEquals(it[1], 0.5)
            assertEquals(it[2], 0.5)
        }

        assertThrows<IllegalArgumentException> {
            crossNestedModel.castProbabilities(3)
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

        val crossNestedModel = CrossNestedStructure<Int, ChoiceAlternative<Int>, Parameters> {
            nest("A", lambda = lambda1) {
                option(1, alpha = alpha1)
                option(2)
            }
            nest("B", lambda = lambda2) {
                option(1, alpha = alpha2)
                option(3)
            }
        }.withUtility {
            option(1) {
                u1
            }
            option(2) {
                u2
            }
            option(3) {
                u3
            }
        }.crossNestedLogit("CrossNestedModelForTesting").build(Parameters()).model

        crossNestedModel.castProbabilities(1, 2, 3).let {
            assertEquals(it.values.sum(), 1.0)
            assertEquals(it[1]!!, 0.333333333, 0.0001)
            assertEquals(it[2]!!, 0.333333333, 0.0001)
            assertEquals(it[3]!!, 0.333333333, 0.0001)
        }

        alpha1 = 0.2
        alpha2 = 0.8
        crossNestedModel.castProbabilities(1, 2, 3).let {
            assertEquals(it.values.sum(), 1.0)
            assertEquals(it[1]!!, 0.333333333, 0.0001)
            assertEquals(it[2]!!, 0.333333333, 0.0001)
            assertEquals(it[3]!!, 0.333333333, 0.0001)
        }
    }

    @Test
    fun crossnestedWithLambdaIsValid() {
        var lambda1 = 1.0
        val crossNestedModel = CrossNestedStructure<Int, ChoiceAlternative<Int>, Parameters> {
            nest(name = "Nest 1", lambda = lambda1) {
                option(1, alpha = { 0.8 })
                option(2)
            }

            nest(name = "Nest 2", lambda = 0.000001) {
                option(1, alpha = { 0.2 })
                option(3)
            }
        }.withUtility {
            option(1) {
                0.0
            }
            option(2) {
                0.0
            }
        }.crossNestedLogit("CrossNestedModelForTestingLambda").build(Parameters()).model

        crossNestedModel.castProbabilities(1, 2).let {
            assertEquals(it.values.sum(), 1.0)
        }
    }
}
