package modeling.models

import usecases.choicemodels.ModeFilter
import utils.random.StochasticActor
import utils.units.Time

internal fun <M, P> noFilter() = ModeFilter<M, P> { modes, _ -> modes }

interface ChoiceModel<A, R> where A : StochasticActor {
    val name: String
    val modeFilter: ModeFilter<R, A> get() = noFilter()
    fun choose(agent: A, time: Time): R {
        val choices = choices(agent, time)
        val filtered = filter(agent, choices, time).toSet()

        require(filtered.isNotEmpty()) {
            "Choice model $name cannot evaluate for agent $agent at $time du to empty filtered choice set!\n" +
                "  - choice set before filter: $choices"
        }

        return select(agent, filtered, time)
    }

    fun choices(agent: A, time: Time): Set<R>
    fun filter(agent: A, choices: Set<R>, time: Time) = modeFilter.filter(choices.toList(), agent)
    fun filter(agent: A, time: Time) = filter(agent, choices(agent, time), time)
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
