package usecases.steps

import domain.data.Person
import domain.data.PersonId
import domain.events.InitPersonEvent
import modeling.events.ParallelSimulator
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import modeling.steps.Repository
import modeling.steps.SimulationContext
import modeling.validation.Warning

fun <S, C> S.simulate() where S : ModelExecution<C>, C : RunSimContext {
    this.addStep(SimulateStep(context))
}

interface RunSimContext : SimulationContext {
    val personRepository: Repository<Person, PersonId>
}

class SimulateStep(
    private val context: RunSimContext
) : ModelStep {
    override val name: String = "Simulate agents"

    override fun execute() {
        val sim = ParallelSimulator(timeStep = context.timeStep)

        sim.addAgents(context.personRepository) { person ->
            InitPersonEvent(person, context.behavior.value)
        }
        sim.run(context.simulationStart, context.simulationEnd)
    }

    override fun verifyInput(): Warning? {
        // TODO("Not yet implemented")
        return null
    }

    override fun mockBehavior(): Warning? {
        // TODO("Not yet implemented")
        return null
    }
}
