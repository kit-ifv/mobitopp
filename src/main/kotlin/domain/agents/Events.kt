package domain.agents

import Identifiable

interface SimulationEvent<P, B, S, M>: Event<P, B, S, M> where P: Identifiable, M: S {
    val instant: Boolean

    override fun execute(): EventList {
        return super.execute()
    }
}