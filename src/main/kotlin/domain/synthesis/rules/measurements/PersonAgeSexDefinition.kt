package domain.synthesis.rules.measurements

import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.data.Sex
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
import edu.kit.ifv.populationsynthesis.rules.measurement.NumericMeasurementDefinition

class PersonAgeSexDefinition(val acceptedAgeRange: IntRange, val acceptedSex: Sex) :
    BooleanMeasurementDefinition<SurveyPerson<*>>() {
    override fun generateDescription(): String {
        return "Person age in $acceptedAgeRange && sex == $acceptedSex"
    }

    override fun evaluation(element: SurveyPerson<*>): Boolean {
        return element.age in acceptedAgeRange && element.sex == acceptedSex
    }
}

fun <T> BooleanMeasurementDefinition<SurveyPerson<out T>>.asHouseholdDefinition(): NumericMeasurementDefinition<ISurveyHousehold<out T>> {
    return object : NumericMeasurementDefinition<ISurveyHousehold<out T>>() {
        /**
         * Important: The string returned by this method is used for equality checks for the [NamedMeasurement].
         * Please make sure that objects return the same description if and only if their evaluation behaviour is
         * identical.
         */
        override fun generateDescription(): String {
            return "On HH[${this@asHouseholdDefinition.generateDescription()}]"
        }

        override fun evaluation(element: ISurveyHousehold<out T>): Int {
            return element.members.count { member ->
                this@asHouseholdDefinition.evaluation(member)
            }
        }
    }
}
