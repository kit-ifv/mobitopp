package modeling.discreteChoice

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.awt.Choice

class NestedLogitTest {

    private val choiceModel = NestedLogit.build<
            Options, Situation, RedbusParameters> {
                option(Options.CAR) {
                    0.0
                }
                nest({lambda_bus}) {
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
}

operator fun <X: Any> Map<ChoiceSituation<X>, Double>.get(x: X) : Double {
    return entries.first { it.key == x }.value
}

private class RedbusParameters(val lambda_bus: Double) {
    val pedestrian = DifferentParameters.fromRedbusParameters(this)
}
private class DifferentParameters(val ped: Double) {
    companion object {

        fun fromRedbusParameters(redbusParameters: RedbusParameters): DifferentParameters {
            return redbusParameters.fromRedbusParameters()
        }
        private fun RedbusParameters.fromRedbusParameters(): DifferentParameters {
            return DifferentParameters(
                ped = lambda_bus
            )
        }
    }
}
private val IDENTICAL = RedbusParameters(Double.MIN_VALUE)
private val DIFFERENT = RedbusParameters(1.0)


private enum class Options {
    RED_BUS, BLUE_BUS, CAR
}

private class Situation(override val choice: Options) : ChoiceSituation<Options>() {
    companion object {
        val ALL = Options.entries.map { Situation(it) }.toSet()
    }
}

