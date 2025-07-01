package domain.simulation.config

import core.modelsteps.LateInit
import domain.shared.config.SynthesisContext
import domain.simulation.events.PersonBehavior

interface DemandSimContext : SynthesisContext {
    val behavior: LateInit<PersonBehavior>
}
