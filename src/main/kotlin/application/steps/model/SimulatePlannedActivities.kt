package application.steps.model

import application.steps.HasPersonAgentRepo
import application.steps.SimulationConfig
import core.events.ParallelSimulator
import core.events.SequentialSimulator
import core.events.Simulator
import core.modelsteps.steps.modelStep
import domain.simulation.agent.PersonAgent
import kotlin.time.Duration

/**
 * Runs the simulation for all person agents.
 *
 * This step initializes a [Simulator], adds all agents from the [personAgentRepository],
 * and runs the simulation from the configured start time to the end time.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonAgentRepo] for [PersonAgent].
 * @param config The simulation configuration. Provided via context. Must implement [SimulationConfig].
 * @param simulator A function that creates a [Simulator] given a time step duration.
 *                  Defaults to [parallel].
 */
context(config: SimulationConfig)
fun <C> C.simulate(simulator: (Duration) -> Simulator = parallel) where C: HasPersonAgentRepo<*, PersonAgent> =
    modelStep("simulate agents") {
        val sim = simulator(config.timeStep)
        sim.addAgents(personAgentRepository.elements.toList())
        sim.run(config.simulationStart, config.simulationEnd)
    }

val sequential = { timeStep: Duration -> SequentialSimulator(timeStep = timeStep) }
val parallel = { timeStep: Duration -> ParallelSimulator(timeStep = timeStep) }
//
//fun RunSimContext.simulate(simulator: (Duration) -> Simulator = parallel) = runStep {
//    SimulateStep(this, simulator)
//}
//
//interface RunSimContext : DemandSimContext {
//    val personAgents: Repository<PersonAgent, PersonId>
//}
//
//class SimulateStep(
//    private val context: RunSimContext,
//    private val simulator: (Duration) -> Simulator = parallel,
//) : ModelStep {
//    override val name: String = "Simulate agents"
//
//    override fun execute() {
//        val sim = simulator(context.timeStep)
//
//        sim.addAgents(context.personAgents.elements.toList())
//        sim.run(context.simulationStart, context.simulationEnd)
//    }
//
//    override fun verifyInput(): Warning? {
//        return null
//    }
//
//    override fun mockBehavior(): Warning? {
//        return null
//    }
//}
