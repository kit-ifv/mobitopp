package edu.kit.ifv.domain.synthesis.rules.measurements
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition

class PersonAgeDefinition(val acceptedRange: IntRange) : BooleanMeasurementDefinition<SurveyPerson<*>>() {
    override fun generateDescription(): String = "Person age in $acceptedRange"

    override fun evaluation(element: SurveyPerson<*>): Boolean = element.age in acceptedRange
}
