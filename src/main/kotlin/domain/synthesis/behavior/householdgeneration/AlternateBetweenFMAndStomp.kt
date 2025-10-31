package domain.synthesis.behavior.householdgeneration

import kotlin.math.min

class AlternateBetweenFMAndStomp(
    private val passes: Int = 10
) : Refinement {

    val fmStep = FMRun(
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
    override fun refine(partitions: List<Partition>) {
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
