package domain.models

interface ChoiceModel<A, R> {

    fun select(agent: A, choiceSet: Set<R>): R {
        return doSelect(agent, filter(agent, choiceSet))
    }

    fun doSelect(agent: A, choiceSet: Set<R>): R

    fun filter(agent: A, choiceSet: Set<R>): Set<R>

}

interface FixedChoiceSetModel<A, R>: ChoiceModel<A, R> {
    val choiceSet: Set<R>
    fun select(agent: A): R = select(agent, choiceSet)

}
