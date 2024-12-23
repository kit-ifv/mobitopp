package modeling.discreteChoice

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.awt.Choice

class NestedLogitTest {

    private val choiceModel = NestedLogit.build<
            Options, Situation, RedbusParameters>("Red bus Blue bus choice model") {
        option(Options.CAR) {
            0.0
        }
        nest({ lambda_bus }) {
            option(Options.RED_BUS) {
                0.0
            }
            option(Options.BLUE_BUS) {
                0.0
            }
        }
    }

    @Test
    fun redBusBlueBus() {
        val result = choiceModel.calculateProbabilities(Situation.ALL, IDENTICAL)
        assertEquals(result[Situation(Options.RED_BUS)], 0.25)
        assertEquals(result[Situation(Options.BLUE_BUS)], 0.25)
        assertEquals(result[Situation(Options.CAR)], 0.5)

    }

    @Test
    fun invariantRedBus() {
        val result = choiceModel.calculateProbabilities(Situation.ALL, DIFFERENT)
        assertEquals(result[Situation(Options.RED_BUS)], 1.0 / 3)
        assertEquals(result[Situation(Options.BLUE_BUS)], 1.0 / 3)
        assertEquals(result[Situation(Options.CAR)], 1.0 / 3)
    }

    @Test
    fun badCreationSchemesAreCaught() {

        assertThrows<IllegalArgumentException> {
            NestedLogit.build<
                    Options, Situation, RedbusParameters> {
                option(Options.CAR) {
                    1.0
                }
                option(Options.CAR) {
                    1.0
                }
            }
        }
    }

    @Test
    fun badCreationSchemesAreCaught2() {
        assertThrows<IllegalArgumentException> {
            NestedLogit.build<
                    Options, Situation, RedbusParameters> {
            }
        }
    }

    @Test
    fun badCreationSchemesAreCaught3() {
        assertThrows<IllegalArgumentException> {
            NestedLogit.build<
                    Options, Situation, RedbusParameters> {
                nest(1.0) {
                    nest(1.0) {
                        nest(1.0) {
                            nest(1.0) {

                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun unassociatedElementsGiveMessage() {
        assertThrows<NoSuchElementException> {
            choiceModel.calculateProbabilities(
                Situation.ALL + setOf(Situation(Options.OTHER_ILLEGAL_OPTION)),
                RedbusParameters(1.0))
        }

    }

    operator fun <X : Any> Map<ChoiceSituation<X>, Double>.get(x: X): Double {
        return entries.first { it.key == x }.value
    }

    private class RedbusParameters(val lambda_bus: Double) {
        val pedestrian = DifferentParameters.fromRedbusParameters(this)
    }

    private class DifferentParameters(val ped: Double) {
        companion object {

            fun fromRedbusParameters(redbusParameters: RedbusParameters): DifferentParameters {
                return redbusParameters.internalConverter()
            }

            private fun RedbusParameters.internalConverter(): DifferentParameters {
                return DifferentParameters(
                    ped = lambda_bus
                )
            }
        }
    }

    private val IDENTICAL = RedbusParameters(Double.MIN_VALUE)
    private val DIFFERENT = RedbusParameters(1.0)


    private enum class Options {
        RED_BUS, BLUE_BUS, CAR, OTHER_ILLEGAL_OPTION
    }

    private class Situation(override val choice: Options) : ChoiceSituation<Options>() {
        companion object {
            val ALL = Options.entries.map { Situation(it) }.toSet()
        }
    }
}
