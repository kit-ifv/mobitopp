package domain.synthesis.behavior.householdgeneration.refinement.algorithms

import domain.synthesis.behavior.householdgeneration.AttributeStomper

val FMThenStomp = _root_ide_package_.domain.synthesis.behavior.householdgeneration.refinement.RefinerList(
    _root_ide_package_.domain.synthesis.behavior.householdgeneration.refinement.FMRun(
        amountOfPasses = 100
    ) {
        1
    },

    AttributeStomper(),
    _root_ide_package_.domain.synthesis.behavior.householdgeneration.refinement.FMRun(
        amountOfPasses = 100
    ) {
        1
    }

)
