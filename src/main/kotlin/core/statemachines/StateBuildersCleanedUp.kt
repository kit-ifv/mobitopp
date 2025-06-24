package states_cleaned



@JvmInline
value class StateType<D> private constructor(private val id:ULong) {
    constructor(): this(idCounter++)

    companion object {
        private var idCounter = 0UL
    }
}

typealias AnyStateType = StateType<out StateData>

interface StateData {
    val type: AnyStateType
    val time: Time
    val agent: Agent<*>

    fun advance(time: Time)
}

abstract class BaseStateData(
    override val type: AnyStateType,
    time: Time,
): StateData {
    final override var time: Time = time
        private set

    override fun advance(time: Time) {
        this.time = time
    }
}


interface StateMachineFactory<A: Agent<out Message>> {
    fun create(agent: A) : StateMachine
}

interface StateMachineFactoryBuilder {

    fun <A> initialState(initializer: (A) -> StateData): StateMachineFactory<A> where A: Agent<out Message>

}

typealias OnEnter<D> = D.(Send) -> Unit
typealias OnMessage<D, M> = D.(M, Send) -> Unit
typealias TransitionOnMessage<D, M> = D.(M) -> StateData
typealias TransitionOnCheck<D> = D.() -> StateData?
typealias TransitionOnNext<D> = D.() -> StateData

interface StateMachineBuilder {

    fun <D> state(state: StateType<D>, onEnter: OnEnter<D>? = null): MessageResponseBuilder<D> where D : StateData

    fun <D> transState(state: StateType<D>, onEnter: (OnEnter<D>)? = null): TransitoryStateBuilder<D> where D : StateData

    fun <D> finState(state: StateType<D>, onEnter: OnEnter<D>? = null) where D: StateData

}

interface StateExitBuilder<D> where D : StateData {

    fun <T> transitionOn(message: MessageType<T>, onExit: TransitionOnMessage<D, T>): StateExitBuilder<D> where T: Message

    fun checkTransition(onCheck: TransitionOnCheck<D>): StateExitBuilder<D>

    fun transitionIf(condition: D.() -> Boolean, nextState: D.() -> StateData,): StateExitBuilder<D>

}

interface MessageResponseBuilder<D>: StateExitBuilder<D> where D: StateData {

    fun <T> on(message: MessageType<T>, onMessage: OnMessage<D, T>): MessageResponseBuilder<D> where T: Message

}

interface TransitoryStateBuilder<D> where D: StateData {

    fun next(onTransition: TransitionOnNext<D>)

}