package edu.kit.ifv.domain.synthesis.behavior.cars.amount
import edu.kit.ifv.domain.synthesis.attributes.household.MaximumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MaximumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.CarOwnershipAttributes
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.CarOwnershipFactors
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.carAmountChoiceModel
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.parameters.CarOwnershipParameters

/**
 * A standard assignment strategy that uses predefined choice models and parameter sets for different region types.
 * This strategy leverages the `AssignBySizebasedClassification` with default parameters for various region types.
 */
val standardAssignmentByRegionSize = AssignBySizebasedClassification.createUsingModel(carAmountChoiceModel) {
    converter = { it.toCarOwnershipAttributes() }
    cityParameters = CarOwnershipParameters.carOwnershipCityParameters
    smallTownParameters = CarOwnershipParameters.carOwnershipSmallCity
    urbanAreaParameters = CarOwnershipParameters.carOwnershipUrbanAreaParameters
    ruralAreaParameters = CarOwnershipParameters.carOwnershipRuralArea
}

fun MinimalistHousehold<MaximumHouseholdAttributes, MaximumPersonAttributes>.toCarOwnershipAttributes() =
    CarOwnershipAttributes(
        CarOwnershipFactors(
            this,
        ),
    )
