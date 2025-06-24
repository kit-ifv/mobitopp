package states_cleaned

typealias Time = ULong

interface Agent<M: Message>



interface StateMachine {
    val name: String
    fun start(): Events
    fun process(message: Message): Events
}


interface State {
    val name: String

    fun enter(): Events
    fun processMessage(message: Message): Events
    fun checkMessageTransition(message: Message): State?
    fun checkConditionTransition(): State?
    fun interrupt(): Events
}



