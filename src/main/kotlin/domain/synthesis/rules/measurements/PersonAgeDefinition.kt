package domain.synthesis.rules.measurements

import domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition

class PersonAgeDefinition(val acceptedRange: IntRange) : BooleanMeasurementDefinition<SurveyPerson<*>>() {
    override fun generateDescription(): String = "Person age in $acceptedRange"

    override fun evaluation(element: SurveyPerson<*>): Boolean = element.age in acceptedRange
}
