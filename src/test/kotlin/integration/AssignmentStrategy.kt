package integration

import domain.synthesis.AssignmentStep
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.EnumeratedDiscreteModelBuilder
import kotlin.random.Random

@Suppress("SpacingAroundColon") // Seems to be a detekt version thing
class AssignmentStrategy<I, C, O>(val model: FixedChoiceModel<O, C>, private val situation: (I) -> C) :
    AssignmentStep<I, O> {

    context(random: Random)
    override fun assign(input: I): O = context(situation(input)) {
        model.select()
    }

    companion object {
        fun <I, C, O> viaChoiceModel(model: FixedChoiceModel<O, C>, situation: (I) -> C): AssignmentStrategy<I, C, O> =
            AssignmentStrategy(model, situation)

        fun <I, C, O, P> viaChoiceModel(
            modelStructure: EnumeratedDiscreteModelBuilder<O, C, P>,
            parameters: P,
            situation: (I) -> C,
        ) = viaChoiceModel(modelStructure.build(parameters), situation)
    }
}
