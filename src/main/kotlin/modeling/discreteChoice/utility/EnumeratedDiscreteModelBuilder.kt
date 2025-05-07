package modeling.discreteChoice.utility

import modeling.discreteChoice.DiscreteChoiceModel
import modeling.discreteChoice.EnumeratedDiscreteChoiceModel
import modeling.discreteChoice.distribution.CrossNestedLogit
import modeling.discreteChoice.distribution.CrossNestedStructureDataBuilder
import modeling.discreteChoice.distribution.MultinomialLogit
import modeling.discreteChoice.distribution.NestedLogit
import modeling.discreteChoice.distribution.NestedStructureDataBuilder
import modeling.discreteChoice.selection.RandomWeightedSelect
import modeling.discreteChoice.structure.UtilityAssignmentBuilder
import modeling.discreteChoice.structure.UtilityEnumerationBuilder
import modeling.models.ChoiceAlternative
import modeling.models.ChoiceFilter
import modeling.models.noFilter

fun interface DiscreteModelBuilder<R : Any, A, P> where A : ChoiceAlternative<R> {
    fun build(parameters: P): DiscreteChoiceModel<R, A, P>
}

fun interface EnumeratedDiscreteModelBuilder<R : Any, A, P> where A : ChoiceAlternative<R> {
    fun build(parameters: P): EnumeratedDiscreteChoiceModel<R, A, P>
}

fun <R : Any, A, P> UtilityAssignmentBuilder<R, A, P>.openMultinomialLogit(
    name: String,
    filter: ChoiceFilter<A> = noFilter(),
): DiscreteModelBuilder<R, A, P> where A : ChoiceAlternative<R> =
    DiscreteModelBuilder { parameters ->

        DiscreteChoiceModel<R, A, P>(
            name = name,
            choiceFilter = filter,
            utilityAssignment = this.build(),
            distributionFunction = MultinomialLogit<A, P>(),
            selectionFunction = RandomWeightedSelect(),
            parameters = parameters
        )
    }

fun <R : Any, A, P> UtilityAssignmentBuilder<R, A, P>.multinomialLogit(
    name: String,
    choices: Set<R>,
    filter: ChoiceFilter<A> = noFilter(),
): EnumeratedDiscreteModelBuilder<R, A, P> where A : ChoiceAlternative<R> =
    EnumeratedDiscreteModelBuilder { parameters ->

        DiscreteChoiceModel<R, A, P>(
            name = name,
            choiceFilter = filter,
            utilityAssignment = this.build(),
            distributionFunction = MultinomialLogit<A, P>(),
            selectionFunction = RandomWeightedSelect(),
            parameters = parameters
        ).with(choices)
    }

fun <R : Any, A, P> UtilityEnumerationBuilder<R, A, P>.multinomialLogit(
    name: String,
    filter: ChoiceFilter<A> = noFilter()
): EnumeratedDiscreteModelBuilder<R, A, P> where A : ChoiceAlternative<R> =
    this.multinomialLogit(name, this.build().options, filter)

fun <R : Any, A, P, B> B.nestedLogit(
    name: String,
    filter: ChoiceFilter<A> = noFilter(),
): EnumeratedDiscreteModelBuilder<R, A, P>
    where A : ChoiceAlternative<R>, B : UtilityEnumerationBuilder<R, A, P>, B : NestedStructureDataBuilder<R, A, P> =
    EnumeratedDiscreteModelBuilder { parameters ->

        val utility = this.build()
        val structure = this.buildStructure()

        DiscreteChoiceModel<R, A, P>(
            name = name,
            choiceFilter = filter,
            utilityAssignment = utility,
            distributionFunction = NestedLogit(structure),
            selectionFunction = RandomWeightedSelect(),
            parameters = parameters
        ).with(utility.options)
    }

fun <R : Any, A, P, B> B.crossNestedLogit(
    name: String,
    filter: ChoiceFilter<A> = noFilter(),
): EnumeratedDiscreteModelBuilder<R, A, P>
    where A : ChoiceAlternative<R>,
          B : UtilityEnumerationBuilder<R, A, P>,
          B : CrossNestedStructureDataBuilder<R, A, P> =
    EnumeratedDiscreteModelBuilder { parameters ->

        val utility = this.build()
        val structure = this.buildStructure()

        DiscreteChoiceModel<R, A, P>(
            name = name,
            choiceFilter = filter,
            utilityAssignment = utility,
            distributionFunction = CrossNestedLogit(structure),
            selectionFunction = RandomWeightedSelect(),
            parameters = parameters
        ).with(utility.options)
    }
