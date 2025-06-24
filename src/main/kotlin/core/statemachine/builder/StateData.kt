package core.statemachine.builder

import core.statemachine.Agent
import core.statemachine.Events
import core.statemachine.Message
import core.statemachine.State
import core.statemachine.Time
import kotlin.reflect.KClass

typealias AnyStateType = KClass<out StateData>
typealias StateType<D> = KClass<D>

interface StateData {
    val time: Time
    val agent: Agent<*>

    fun updateTime(time: Time)
}

abstract class BaseStateData(
    time: Time,
) : StateData {
    final override var time: Time = time
        private set

    override fun updateTime(time: Time) {
        this.time = time
    }
}

interface StateBehavior<D : StateData> {
    fun enter(data: D): Events
    fun processMessage(data: D, message: Message): Pair<Events, State?>

//    fun checkMessageTransition(data: D, message: Message): State?
    fun fallbackTransition(data: D): Pair<Events, State?>
    fun interrupt(data: D): Events
}
