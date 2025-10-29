package domain.synthesis.behavior.householdgeneration.refinement.algorithms

val FMThenStomp = _root_ide_package_.domain.synthesis.behavior.householdgeneration.refinement.RefinerList(
    _root_ide_package_.domain.synthesis.behavior.householdgeneration.refinement.FMRun(
        amountOfPasses = 100
    ) {
        1
    },

    _root_ide_package_.domain.synthesis.behavior.householdgeneration.refinement.AttributeStomper(),
    _root_ide_package_.domain.synthesis.behavior.householdgeneration.refinement.FMRun(
        amountOfPasses = 100
    ) {
        1
    }

)
