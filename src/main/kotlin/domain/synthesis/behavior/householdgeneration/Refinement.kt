package domain.synthesis.behavior.householdgeneration.refinement

fun interface Refinement {
    fun refine(partitions: List<domain.synthesis.behavior.householdgeneration.Partition>)

    companion object {
        val NONE = _root_ide_package_.domain.synthesis.behavior.householdgeneration.refinement.algorithms.NoRefinement

        val FMRun = FMRun { 1 }
        val FMThenStomp =
            _root_ide_package_.domain.synthesis.behavior.householdgeneration.refinement.algorithms.FMThenStomp
    }
}
