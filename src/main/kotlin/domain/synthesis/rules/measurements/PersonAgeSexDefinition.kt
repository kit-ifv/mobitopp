package domain.synthesis.rules.measurements

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
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

fun <T : MinimumPersonAttributes> BooleanMeasurementDefinition<SurveyPerson<T>>.asHouseholdDefinition(): NumericMeasurementDefinition<ISurveyHousehold<MinimumHouseholdAttributes, T>> {
    return object : NumericMeasurementDefinition<ISurveyHousehold<MinimumHouseholdAttributes, T>>() {
        /**
         * Important: The string returned by this method is used for equality checks for the [NamedMeasurement].
         * Please make sure that objects return the same description if and only if their evaluation behaviour is
         * identical.
         */
        override fun generateDescription(): String {
            return "On HH[${this@asHouseholdDefinition.generateDescription()}]"
        }

        override fun evaluation(element: ISurveyHousehold<MinimumHouseholdAttributes, T>): Int {
            return element.members.count { member ->
                this@asHouseholdDefinition.evaluation(member)
            }
        }
    }
}
