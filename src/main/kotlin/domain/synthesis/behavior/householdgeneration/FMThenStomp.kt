package domain.synthesis.behavior.householdgeneration

val FMThenStomp = RefinerList(
    FMRun(
        amountOfPasses = 100
    ) {
        1
    },

    AttributeStomper(),
    FMRun(
        amountOfPasses = 100
    ) {
        1
    }

)
