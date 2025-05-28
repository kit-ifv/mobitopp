package modeling.discreteChoice

import discreteChoice.structure.NestedStructure
import discreteChoice.utility.nestedLogit
import modeling.models.ChoiceAlternative
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class NestedLogitTest {

    private val choiceModel = NestedStructure<Options, Alternative, RedbusParameters> {
        option(Options.CAR) {
            0.0
        }
        nest("Bus", { lambdaBus }) {
            option(Options.RED_BUS) {
                0.0
            }
            option(Options.BLUE_BUS) {
                0.0
            }
        }
    }.nestedLogit("Red bus Blue bus choice model")

    @Test
    fun redBusBlueBus() {
        val model = choiceModel.build(identical).model
        val result = model.probabilities(Alternative.ALL_VALID)
        assertEquals(result[Alternative(Options.RED_BUS)], 0.25)
        assertEquals(result[Alternative(Options.BLUE_BUS)], 0.25)
        assertEquals(result[Alternative(Options.CAR)], 0.5)
    }

    @Test
    fun invariantRedBus() {
        val model = choiceModel.build(different).model
        val result = model.probabilities(Alternative.ALL_VALID)
        assertEquals(result[Alternative(Options.RED_BUS)], 1.0 / 3)
        assertEquals(result[Alternative(Options.BLUE_BUS)], 1.0 / 3)
        assertEquals(result[Alternative(Options.CAR)], 1.0 / 3)
    }

    @Test
    fun duplicateOptionsAreCaught() {
        assertThrows<IllegalArgumentException> {
            NestedStructure<Options, Alternative, RedbusParameters> {
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
    fun emptyStructuresAreCaught() {
        assertThrows<IllegalArgumentException> {
            NestedStructure<Options, Alternative, RedbusParameters> { }
        }
    }

    @Test
    fun emptyNestsAreCaught() {
        assertThrows<IllegalArgumentException> {
            NestedStructure<Options, Alternative, RedbusParameters> {
                nest("A") {
                    nest("B") {
                        nest("C") {
                            nest("D") {}
                        }
                    }
                }
            }
        }
    }

    @Test
    fun unassociatedElementsGiveMessage() {
        assertThrows<IllegalArgumentException> {
            choiceModel.build(RedbusParameters(1.0)).model.probabilities(
                Alternative.ALL_VALID + setOf(Alternative(Options.OTHER_ILLEGAL_OPTION))
            )
        }
    }

    operator fun <X : Any> Map<ChoiceAlternative<X>, Double>.get(x: X): Double {
        return entries.first { it.key == x }.value
    }

    private class RedbusParameters(val lambdaBus: Double) {
        val pedestrian = DifferentParameters.fromRedbusParameters(this)
    }

    private class DifferentParameters(val ped: Double) {
        companion object {

            fun fromRedbusParameters(redbusParameters: RedbusParameters): DifferentParameters {
                return redbusParameters.internalConverter()
            }

            private fun RedbusParameters.internalConverter(): DifferentParameters {
                return DifferentParameters(
                    ped = lambdaBus
                )
            }
        }
    }

    private val identical = RedbusParameters(Double.MIN_VALUE)
    private val different = RedbusParameters(1.0)

    private enum class Options {
        RED_BUS, BLUE_BUS, CAR, OTHER_ILLEGAL_OPTION
    }

    private class Alternative(override val choice: Options) : ChoiceAlternative<Options>() {
        companion object {
            val ALL_VALID = setOf(Alternative(Options.RED_BUS), Alternative(Options.BLUE_BUS), Alternative(Options.CAR))
        }
    }
}
