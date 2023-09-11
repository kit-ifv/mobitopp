package domain.agents

typealias EventList = List<Event<out Any, out Any, out Any, out Any>>

abstract class Agent<P, B, S, M>(
    private val mutableState: M
) where M: S {
    abstract fun id(): Int
    abstract fun properties(): P
    abstract fun behavior(): B
    fun state(): S = mutableState

    fun accept(event: Event<P, B, S, M>): EventList {
        return event.visit(properties(), behavior(), mutableState)
    }

}

interface Event<P, B, S, M> where M : S {
    fun receiver(): Agent<P, B, S, M>
    fun execute(): EventList {
        return receiver().accept(this)
    }
    fun visit(properties: P, behavior: B, mutableState: M): EventList

}