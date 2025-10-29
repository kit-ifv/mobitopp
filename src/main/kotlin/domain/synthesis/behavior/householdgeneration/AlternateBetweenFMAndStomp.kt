package domain.synthesis.behavior.householdgeneration.refinement.algorithms

import domain.synthesis.behavior.householdgeneration.AttributeStomper
import domain.synthesis.behavior.householdgeneration.averagePercentError
import kotlin.math.min

class AlternateBetweenFMAndStomp(
    private val passes: Int = 10
) : domain.synthesis.behavior.householdgeneration.refinement.Refinement {

    val fmStep = _root_ide_package_.domain.synthesis.behavior.householdgeneration.refinement.FMRun(
        amountOfPasses = 10
    ) {

        min(
            it.from.amount(it.signatureIndex),
            min(
                it.from.untilFlagChange(it.signatureIndex.index, -1),
                it.to.untilFlagChange(it.signatureIndex.index, 1)
            )
        )
    }

    val stompStep =
        AttributeStomper(repetitions = 10)
    override fun refine(partitions: List<domain.synthesis.behavior.householdgeneration.Partition>) {
        println("InitialFM")
        fmStep.refine(partitions)
        println(partitions.averagePercentError())
        repeat(passes) {
            println("Stomping")
            stompStep.refine(partitions)
            println(partitions.averagePercentError())
            println("FMIng")
            fmStep.refine(partitions)
            println(partitions.averagePercentError())
        }
    }
}
