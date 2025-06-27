package core.statemachine

interface Agent<M : Message> {

    fun init(): Events

    fun processEvent(event: Event<out M>): Events
}

interface StateBasedAgent<M : Message> : Agent<M> {
    val stateMachine: StateMachine

    override fun init(): Events = stateMachine.start()
    override fun processEvent(event: Event<out M>): Events = stateMachine.process(event)
}
