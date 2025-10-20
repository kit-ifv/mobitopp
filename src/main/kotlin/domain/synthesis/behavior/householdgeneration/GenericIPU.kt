package domain.synthesis.behavior.householdgeneration

class GenericIPU<I> {


    fun <AREA> calculate(
        seed: Collection<I>,
        condition: Map<AREA, Collection<Rule<I>>>,
    ): Map<AREA, Rule<I>> {

        val rukle = condition.entries.first().value.first()

        rukle.evaluate(seed.first())


        return TODO()
    }

}