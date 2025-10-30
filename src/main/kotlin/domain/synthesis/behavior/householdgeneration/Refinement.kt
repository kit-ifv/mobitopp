package domain.synthesis.behavior.householdgeneration

fun interface Refinement {
    fun refine(partitions: List<Partition>)

    companion object {
        val NONE = NoRefinement

        val FMRun = FMRun { 1 }
        val FMThenStomp =
            domain.synthesis.behavior.householdgeneration.FMThenStomp
    }
}
