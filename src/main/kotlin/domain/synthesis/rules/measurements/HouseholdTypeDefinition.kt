package domain.synthesis.rules.measurements

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.data.HouseholdType
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition

class HouseholdTypeDefinition(val targetType: HouseholdType) :
    BooleanMeasurementDefinition<ISurveyHousehold<MinimumHouseholdAttributes, *>>() {
    override fun generateDescription(): String = "Household Type == $targetType"

    override fun evaluation(element: ISurveyHousehold<MinimumHouseholdAttributes, *>): Boolean =
        element.type == targetType
}
