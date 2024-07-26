package modeling.models

import utils.random.Histogram
import utils.random.StochasticActor
import utils.units.Time
import kotlin.math.exp

abstract class LogitModel<A, R> : ChoiceModel<A, R> where A : StochasticActor {

    override fun select(agent: A, choices: Set<R>, time: Time): R {
        val utilities = choices.associateWith { utility(agent, it, time) }

        utilities.forEach {
            require(it.value.isFinite()) {
                "Choice model $name: utility for option ${it.key} evaluated to ${it.value}!"
            }
        }

        var expUtil = expUtility(utilities)
        var sum = expUtil.values.sum()

        if (sum == 0.0) {
            val maxUtility = utilities.values.max()
            expUtil = expUtility(utilities, beta = 1.0, offset = maxUtility)
            sum = expUtil.values.sum()
        }

        require(sum.isFinite()) { "Choice model $name: exp util sum evaluates to $sum!" }

        val probabilities = expUtil.mapValues { it.value / sum }
        val distribution = Histogram(
            name = "Choice probabilities of model $name for agent $agent at $time!",
            distribution = probabilities
        )

        return distribution.drawValue(agent.random)
    }

    private fun expUtility(utilities: Map<R, Double>, beta: Double = 1.0, offset: Double = 0.0): Map<R, Double> {
        val expUtil = utilities.mapValues { util ->
            exp(beta * (util.value + offset))
        }

        val sum = utilities.values.sum()

        return if (sum.isInfinite() or (sum == 0.0)) {
            expUtility(utilities, beta / 2.0, offset)
        } else {
            expUtil
        }
    }

    protected abstract fun utility(agent: A, choice: R, time: Time): Double
}
