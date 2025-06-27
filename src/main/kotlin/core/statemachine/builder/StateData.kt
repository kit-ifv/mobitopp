package core.statemachine.builder

import core.statemachine.Agent
import core.statemachine.Events
import core.statemachine.Message
import core.statemachine.State
import utils.units.AbsoluteTime
import kotlin.reflect.KClass

typealias AnyStateType = KClass<out StateData>
typealias StateType<D> = KClass<out D>

interface StateData {
    val time: AbsoluteTime
    val agent: Agent<*>

    fun updateTime(time: AbsoluteTime)
}

abstract class BaseStateData(
    time: AbsoluteTime,
) : StateData {
    final override var time: AbsoluteTime = time
        private set

    override fun updateTime(time: AbsoluteTime) {
        this.time = time
    }
}

interface StateBehavior<D : StateData> {
    fun enter(data: D): Events
    fun processMessage(data: D, message: Message): Pair<Events, State?>
    fun fallbackTransition(data: D): Pair<Events, State?>
    fun interrupt(data: D): Events
}
