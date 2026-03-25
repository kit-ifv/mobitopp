package domain.synthesis.behavior.cars.amount

import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.discreteChoice.CarOwnershipAttributes
import domain.synthesis.behavior.discreteChoice.CarOwnershipFactors
import domain.synthesis.behavior.discreteChoice.carChoiceUtility
import domain.synthesis.behavior.discreteChoice.carOwnershipCityParameters
import domain.synthesis.behavior.discreteChoice.carOwnershipRuralArea
import domain.synthesis.behavior.discreteChoice.carOwnershipSmallCity
import domain.synthesis.behavior.discreteChoice.carOwnershipUrbanAreaParameters

/**
 * A standard assignment strategy that uses predefined choice models and parameter sets for different region types.
 * This strategy leverages the `AssignBySizebasedClassification` with default parameters for various region types.
 */
val standardAssignmentByRegionSize = AssignBySizebasedClassification.createUsingModel(carChoiceUtility) {
    converter = { it.toCarOwnershipAttributes() }
    cityParameters = carOwnershipCityParameters
    smallTownParameters = carOwnershipSmallCity
    urbanAreaParameters = carOwnershipUrbanAreaParameters
    ruralAreaParameters = carOwnershipRuralArea
}

fun MinimalistHousehold<MaximumHouseholdAttributes, MaximumPersonAttributes>.toCarOwnershipAttributes(): CarOwnershipAttributes {
    return CarOwnershipAttributes(
        CarOwnershipFactors(
            this,
        )
    )
}
