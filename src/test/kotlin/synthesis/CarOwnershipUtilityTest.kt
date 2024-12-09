package synthesis

import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.Sex
import modeling.discreteChoice.CarOwnershipParameters
import modeling.discreteChoice.NoCarParameters
import modeling.discreteChoice.UtilityFunction
import modeling.discreteChoice.carChoiceModel
import modeling.discreteChoice.carSelectionFunction
import kotlin.test.Test

class CarOwnershipUtilityTest {
    @Test
    fun valuesOfCarChoiceModel() {
        // TODO either refactor this into an actual test instead of a print test or remove it entirely
        val nest = carSelectionFunction.nest

        val choiceModel = carChoiceModel
        val synthesisHouseholdBuilder = SynthesisHouseholdBuilder()
        val person = SurveyPerson.create(Sex.MALE, age = 19, employment = Employment.FULLTIME, true)
        val otherPerson = SurveyPerson.create(Sex.MALE, age = 19, employment = Employment.FULLTIME, false)
        val parameters = CarOwnershipParameters(SynthesisHouseholdBuilder(1).apply {
            economicStatus = EconomicStatus.MIDDLE
            members = mutableListOf(SynthesisPerson(synthesisHouseholdBuilder, person))
        }, { 0.0 })
        println(choiceModel.selectVerbose(parameters))

        val fue = UtilityFunction<Int, CarOwnershipParameters> { _, p ->
            NoCarParameters.calculate(p)

        }
        println(fue.calculateUtility(0, parameters))
        val result = nest.calculateProbabilities(parameters)
        val result2 = nest.calculateProbabilities(
            CarOwnershipParameters(SynthesisHouseholdBuilder(1).apply {
                economicStatus = EconomicStatus.MIDDLE
                members = mutableListOf(SynthesisPerson(synthesisHouseholdBuilder, person))
            }, { 0.0 })
        )
        println(result)
        println(result2)
    }
}
