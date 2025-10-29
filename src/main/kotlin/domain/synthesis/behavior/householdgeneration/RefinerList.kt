package domain.synthesis.behavior.householdgeneration

class RefinerList(
    private val refiners: List<Refinement>
) : Refinement {
    constructor(vararg refinements: Refinement) : this(refinements.toList())
    override fun refine(partitions: List<Partition>) {
        refiners.forEach {
            it.refine(partitions)
        }
    }
}
