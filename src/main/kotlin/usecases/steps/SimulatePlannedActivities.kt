package usecases.steps

import domain.events.InitPersonEvent
import modeling.events.Simulator
import modeling.steps.CustomStep
import modeling.steps.ModelExecution
import modeling.steps.SimulationContext

fun <S, C> S.simulate() where S : ModelExecution<C>, C : PersonContext, C : SimulationContext {
    val simStep = CustomStep(
        name = "simulate planned activities",
        validation = { true }
    ) {
        val sim = Simulator(timeStep = context.timeStep)

        sim.addAgents(context.personRepository) { person ->
            InitPersonEvent(person, context.behavior.value)
        }
        sim.run(context.simulationStart, context.simulationEnd)
    }

    this.addStep(simStep)
}
