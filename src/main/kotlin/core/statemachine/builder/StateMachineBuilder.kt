package core.statemachine.builder

import core.statemachine.Message
import core.statemachine.MessageType
import core.statemachine.Send

typealias OnEnter<D> = D.(Send) -> Unit
typealias OnMessage<D, M> = D.(M, Send) -> Unit
typealias TransitionOnMessage<D, M> = D.(M, Send) -> StateData?
typealias OnOtherwiseTransition<D> = D.(Send) -> StateData?
typealias TransitionOnNext<D> = D.() -> StateData

interface StateMachineBuilder {

    fun <D> state(state: StateType<D>, onEnter: OnEnter<D>? = null): MessageResponseBuilder<D> where D : StateData

    fun <D> transState(state: StateType<D>, onEnter: (OnEnter<D>)? = null): MandatoryTransitionBuilder<D> where D : StateData

    fun <D> finState(state: StateType<D>, onEnter: OnEnter<D>? = null) where D : StateData
}

interface FallbackTransitionBuilder<D> where D : StateData {

    fun checkTransition(onCheck: OnOtherwiseTransition<D>): FallbackTransitionBuilder<D>

    fun transitionIf(condition: D.() -> Boolean, nextState: D.(Send) -> StateData): FallbackTransitionBuilder<D>
}

interface MessageResponseBuilder<D> : FallbackTransitionBuilder<D> where D : StateData {

    fun <T> transitionOn(message: MessageType<T>, onMessage: TransitionOnMessage<D, T>): MessageResponseBuilder<D> where T : Message
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

    fun next(onTransition: TransitionOnNext<D>)
}
