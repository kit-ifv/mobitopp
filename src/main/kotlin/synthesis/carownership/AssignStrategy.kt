package synthesis.carownership

import domain.enums.SizebasedRegiostarClassification
import modeling.discreteChoice.KnownDiscreteChoiceModel
import synthesis.SynthesisHouseholdBuilder
import synthesis.discreteChoice.CarOwnershipAttributes
import synthesis.discreteChoice.CarOwnershipParameters
import synthesis.discreteChoice.carChoiceModel
import synthesis.discreteChoice.carOwnershipCityParameters
import synthesis.discreteChoice.carOwnershipRuralArea
import synthesis.discreteChoice.carOwnershipSmallCity
import synthesis.discreteChoice.carOwnershipUrbanAreaParameters
import synthesis.discreteChoice.select

fun interface CarOwnershipAssignStrategy {

    fun assignNumberOfCars(householdBuilder: SynthesisHouseholdBuilder<*>): Int
}

object TrivialAssignment : CarOwnershipAssignStrategy {
    override fun assignNumberOfCars(householdBuilder: SynthesisHouseholdBuilder<*>): Int {
        return 0
    }
}

class AssignViaRegionType(
    val model: KnownDiscreteChoiceModel<Int, CarOwnershipAttributes, CarOwnershipParameters> = carChoiceModel,
    val cityParameters: CarOwnershipParameters = carOwnershipCityParameters,
    val smallTownParameters: CarOwnershipParameters = carOwnershipSmallCity,
    val urbanAreaParameters: CarOwnershipParameters = carOwnershipUrbanAreaParameters,
    val ruralAreaParameters: CarOwnershipParameters = carOwnershipRuralArea,
) : CarOwnershipAssignStrategy {


    override fun assignNumberOfCars(householdBuilder: SynthesisHouseholdBuilder<*>): Int {


        val parameterSet =
            householdBuilder.location.zone?.regionType?.toRegiostar17()?.toSizebasedClassification()?.toParameters()
                ?: cityParameters

        return model.select(householdBuilder, parameterSet)
    }

    private fun SizebasedRegiostarClassification.toParameters(): CarOwnershipParameters {
        return when (this) {
            SizebasedRegiostarClassification.CITY -> cityParameters
            SizebasedRegiostarClassification.SMALL_TOWN -> smallTownParameters
            SizebasedRegiostarClassification.URBAN_AREA -> urbanAreaParameters
            SizebasedRegiostarClassification.RURAL_AREA -> ruralAreaParameters
        }
    }

    companion object {
        class AssignViaRegionTypeBuilder {
            lateinit var model: KnownDiscreteChoiceModel<Int, CarOwnershipAttributes, CarOwnershipParameters>
            lateinit var cityParameters: CarOwnershipParameters
            lateinit var smallTownParameters: CarOwnershipParameters
            lateinit var urbanAreaParameters: CarOwnershipParameters
            lateinit var ruralAreaParameters: CarOwnershipParameters

            fun build(): AssignViaRegionType {
                return AssignViaRegionType(model, cityParameters, smallTownParameters, urbanAreaParameters, ruralAreaParameters)
            }
        }
        fun create(lambda: AssignViaRegionTypeBuilder.() -> Unit): AssignViaRegionType {
            val builder = AssignViaRegionTypeBuilder()
            builder.apply(lambda)
            return builder.build()
        }

    }
}