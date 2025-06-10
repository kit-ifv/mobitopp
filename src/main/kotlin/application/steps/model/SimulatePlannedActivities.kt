package application.steps.model

import core.events.ParallelSimulator
import core.modelsteps.ModelStep
import core.modelsteps.Repository
import core.modelsteps.SimulationContext
import core.modelsteps.Warning
import domain.simulation.agent.PersonAgent
import domain.simulation.events.InitPersonEvent
import domain.synthesis.data.PersonId

fun RunSimContext.simulate() = runStep {
    SimulateStep(this)
}

interface RunSimContext : SimulationContext {
    val personAgents: Repository<PersonAgent, PersonId>
}

class SimulateStep(
    private val context: RunSimContext
) : ModelStep {
    override val name: String = "Simulate agents"

    override fun execute() {
        val sim = ParallelSimulator(timeStep = context.timeStep)

        sim.addAgents(context.personAgents) { person ->
            InitPersonEvent(person, context.behavior.value)
        }
        sim.run(context.simulationStart, context.simulationEnd)
    }

    override fun verifyInput(): Warning? {
        return null
    }

    override fun mockBehavior(): Warning? {
        return null
    }
}
