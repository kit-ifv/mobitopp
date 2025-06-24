package core.statemachine.builder

import core.statemachine.Agent
import core.statemachine.Events
import core.statemachine.Message
import core.statemachine.State
import core.statemachine.Time

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

interface StateBehavior<D: StateData> {
    fun enter(data: D): Events
    fun processMessage(data: D, message: Message): Events
    fun checkMessageTransition(data: D, message: Message): State?
    fun checkConditionTransition(data: D): State?
    fun interrupt(data: D): Events
}