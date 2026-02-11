package application.steps.model

import core.events.ParallelSimulator
import core.events.SequentialSimulator
import core.events.Simulator
import core.modelsteps.ModelStep
import core.modelsteps.Repository
import core.modelsteps.Warning
import domain.simulation.agent.PersonAgent
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.PersonId
import kotlin.time.Duration

fun RunSimContext.simulate(simulator: (Duration) -> Simulator = parallel) = runStep {
    SimulateStep(this, simulator)
}

interface RunSimContext : DemandSimContext {
    val personAgents: Repository<PersonAgent, PersonId>
}

val sequential = { timeStep: Duration -> SequentialSimulator(timeStep = timeStep) }
val parallel = { timeStep: Duration -> ParallelSimulator(timeStep = timeStep) }

class SimulateStep(
    private val context: RunSimContext,
    private val simulator: (Duration) -> Simulator = parallel,
) : ModelStep {
    override val name: String = "Simulate agents"

    override fun execute() {
        val sim = simulator(context.timeStep)

        sim.addAgents(context.personAgents.elements.toList())
        sim.run(context.simulationStart, context.simulationEnd)
    }

    override fun verifyInput(): Warning? {
        return null
    }

    override fun mockBehavior(): Warning? {
        return null
    }
}
