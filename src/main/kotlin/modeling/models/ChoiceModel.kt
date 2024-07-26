package modeling.models

import utils.random.StochasticActor
import utils.units.Time

internal fun <R> noFilter(choices: Set<R>) = choices

interface ChoiceModel<A, R> where A : StochasticActor {
    val name: String

    fun choose(agent: A, time: Time): R {
        val choices = choices(agent, time)
        val filtered = filter(choices, time)

        require(filtered.isNotEmpty()) {
            "Choice model $name cannot evaluate for agent $agent at $time du to empty filtered choice set!\n" +
                "  - choice set before filter: $choices"
        }

        return select(agent, filtered, time)
    }

    fun choices(agent: A, time: Time): Set<R>
    fun filter(choices: Set<R>, time: Time) = noFilter(choices)
    fun select(agent: A, choices: Set<R>, time: Time): R
}

abstract class FixedChoicesModel<A, R>(
    val choices: Set<R>
) : ChoiceModel<A, R> where A : StochasticActor {
    override fun choices(agent: A, time: Time) = choices
}

class RandomChoiceModel<A, R>(
    override val name: String,
    choices: Set<R>
) : FixedChoicesModel<A, R>(choices) where A : StochasticActor {

    override fun select(agent: A, choices: Set<R>, time: Time): R {
        return choices.random(agent.random)
    }
}
