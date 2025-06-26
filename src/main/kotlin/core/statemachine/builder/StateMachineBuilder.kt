package core.statemachine.builder

import core.statemachine.Agent
import core.statemachine.Message
import core.statemachine.MessageType
import core.statemachine.Send
import core.statemachine.Time

typealias OnEnter<D> = D.(Send) -> Unit
typealias OnMessage<D, M> = D.(M, Send) -> Unit
typealias TransitionOnMessage<D, M> = D.(M, Send) -> StateData?
typealias FallbackTransition<D> = D.(Send) -> StateData?
typealias TransitionToNext<D> = D.() -> StateData

// class StartState<A : Agent<*>>(time: Time, override val agent: A) : BaseStateData(time) {
//    val self: A
//        get() = agent
// }
// class SubStartState<A : Agent<*>, D>(
//    time: Time,
//    override val agent: A,
//    val contextData: D
// ) : BaseStateData(time) {
//    val self: A
//        get() = agent
// }

interface StateMachineBuilder<A> where A : Agent<*> {

    fun <D : StateData> start(
        state: StateType<D>,
        initialize: (Time, A) -> D,
        onEnter: OnEnter<D>? = null
    ): MessageResponseBuilder<D>

//    fun <D> subStart(onEnter: OnEnter<SubStartState<A, D>>? = null): MessageResponseBuilder<SubStartState<A, D>>

    fun <D> state(state: StateType<D>, onEnter: OnEnter<D>? = null): MessageResponseBuilder<D> where D : StateData

    fun <D> transState(
        state: StateType<D>,
        onEnter: OnEnter<D>? = null
    ): MandatoryTransitionBuilder<D> where D : StateData

    fun <D> finState(state: StateType<D>, onEnter: OnEnter<D>? = null) where D : StateData
}

interface FallbackTransitionBuilder<D> where D : StateData {

    fun checkTransition(onCheck: FallbackTransition<D>): FallbackTransitionBuilder<D>

    fun transitionIf(condition: D.() -> Boolean, nextState: D.(Send) -> StateData): FallbackTransitionBuilder<D>
}

interface MessageResponseBuilder<D> : FallbackTransitionBuilder<D> where D : StateData {

    fun <T> transitionOn(
        message: MessageType<T>,
        onMessage: TransitionOnMessage<D, T>
    ): MessageResponseBuilder<D> where T : Message
}

fun <D : StateData, T> MessageResponseBuilder<D>.on(
    message: MessageType<T>,
    onMessage: OnMessage<D, T>
): MessageResponseBuilder<D> where T : Message {
    return transitionOn(message) { message, send ->
        onMessage(message, send)
        null
    }
}

interface MandatoryTransitionBuilder<D> where D : StateData {

    fun next(onTransition: TransitionToNext<D>)
}
