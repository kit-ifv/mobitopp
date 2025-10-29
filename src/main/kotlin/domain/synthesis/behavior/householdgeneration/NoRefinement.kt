package domain.synthesis.behavior.householdgeneration.refinement.algorithms

import domain.synthesis.behavior.householdgeneration.Partition
import domain.synthesis.behavior.householdgeneration.refinement.Refinement


object NoRefinement : Refinement {
    override fun refine(partitions: List<Partition>) {
        return
    }
}
