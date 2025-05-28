package usecases.steps

import domain.agent.PersonAgent
import domain.data.PersonId
import domain.events.InitPersonEvent
import modeling.events.ParallelSimulator
import modeling.steps.ModelStep
import modeling.steps.Repository
import modeling.steps.SimulationContext
import modeling.validation.Warning

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
