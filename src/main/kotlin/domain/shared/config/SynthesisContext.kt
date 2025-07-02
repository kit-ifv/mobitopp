package domain.shared.config

import core.modelsteps.Context
import core.modelsteps.LateInit
import domain.shared.enums.ActivityType
import domain.shared.enums.Mode
import domain.shared.location.Metrics
import utils.CodePlan

interface SynthesisContext : Context {
    val modes: CodePlan<Mode>
    val activityTypes: CodePlan<ActivityType>

    // TODO question: Is LateInit actually the minimal context, what if I already have an impedance at initialization?
    val impedance: LateInit<Metrics>
}
