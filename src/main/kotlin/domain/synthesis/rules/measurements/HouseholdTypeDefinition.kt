package domain.synthesis.rules.measurements

import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.data.HouseholdType
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition

class HouseholdTypeDefinition(val targetType: HouseholdType) : BooleanMeasurementDefinition<ISurveyHousehold<*>>() {
    override fun generateDescription(): String {
        return "Household Type == $targetType"
    }

    override fun evaluation(element: ISurveyHousehold<*>): Boolean {
        return element.type == targetType
    }
}
