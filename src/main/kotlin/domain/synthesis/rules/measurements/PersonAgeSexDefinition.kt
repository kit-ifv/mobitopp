package domain.synthesis.rules.measurements

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.data.person.Sex
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
import edu.kit.ifv.populationsynthesis.rules.measurement.NumericMeasurementDefinition

open class PersonAgeSexDefinition(open val acceptedAgeRange: IntRange, val acceptedSex: Sex) :
    NumericMeasurementDefinition<ISurveyHousehold<*, *>>() {
    override fun generateDescription(): String = "Person age in $acceptedAgeRange && sex == $acceptedSex"

    override fun evaluation(element: ISurveyHousehold<*, *>): Int = element.count {
        it.age in acceptedAgeRange && it.sex == acceptedSex
    }

    fun toMutableDefinition() = MutablePersonAgeSexDefinition(acceptedAgeRange, acceptedSex)
}

class MutablePersonAgeSexDefinition(override var acceptedAgeRange: IntRange, sex: Sex) :
    PersonAgeSexDefinition(
        acceptedAgeRange,
        sex,
    ) {
    override fun toString(): String =
        "MutablePersonAgeSexDefinition(acceptedAgeRange=$acceptedAgeRange, sex=$acceptedSex)"
}

class HouseholdPersonAgeSexDefinition(val acceptedAgeRange: IntRange, val acceptedSex: Sex) :
    NumericMeasurementDefinition<ISurveyHousehold<*, *>>() {
    override fun generateDescription(): String = "Person age in $acceptedAgeRange && sex == $acceptedSex"

    override fun evaluation(element: ISurveyHousehold<*, *>): Int = element.count {
        it.age in acceptedAgeRange && it.sex == acceptedSex
    }
}

fun <T : MinimumPersonAttributes> BooleanMeasurementDefinition<SurveyPerson<T>>.asHouseholdDefinition():
    NumericMeasurementDefinition<ISurveyHousehold<MinimumHouseholdAttributes, T>> =
    object : NumericMeasurementDefinition<ISurveyHousehold<MinimumHouseholdAttributes, T>>() {
        /**
         * Important: The string returned by this method is used for equality checks for the [NamedMeasurement].
         * Please make sure that objects return the same description if and only if their evaluation behaviour is
         * identical.
         */
        override fun generateDescription(): String = "On HH[${this@asHouseholdDefinition.generateDescription()}]"

        override fun evaluation(element: ISurveyHousehold<MinimumHouseholdAttributes, T>): Int =
            element.members.count { member ->
                this@asHouseholdDefinition.evaluation(member)
            }
    }
