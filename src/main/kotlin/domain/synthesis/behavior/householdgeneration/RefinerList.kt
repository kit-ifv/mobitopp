package domain.synthesis.behavior.householdgeneration.refinement

class RefinerList(
    private val refiners: List<Refinement>
) : Refinement {
    constructor(vararg refinements: Refinement) : this(refinements.toList())
    override fun refine(partitions: List<domain.synthesis.behavior.householdgeneration.Partition>) {
        refiners.forEach {
            it.refine(partitions)
        }
    }
}
