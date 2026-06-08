package edu.kit.ifv.application.steps.model
import edu.kit.ifv.application.steps.HasPersonAgentRepo
import edu.kit.ifv.application.steps.SimulationConfig
import edu.kit.ifv.core.events.ParallelSimulator
import edu.kit.ifv.core.events.SequentialSimulator
import edu.kit.ifv.core.events.Simulator
import edu.kit.ifv.core.modelsteps.steps.modelStep
import edu.kit.ifv.domain.simulation.agent.PersonAgent
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
fun <C> C.simulate(simulator: (Duration) -> Simulator = parallel) where C : HasPersonAgentRepo<*, PersonAgent> =
    modelStep("simulate agents") {
        val sim = simulator(config.timeStep)
        sim.addAgents(personAgentRepository.elements.toList())
        sim.run(config.simulationStart, config.simulationEnd)
    }

val sequential = { timeStep: Duration -> SequentialSimulator(timeStep = timeStep) }
val parallel = { timeStep: Duration -> ParallelSimulator(timeStep = timeStep) }
