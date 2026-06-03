package domain.synthesis.behavior.cars.choicemodels

import domain.shared.enums.areatype.SizebasedRegiostarClassification
import domain.shared.enums.person.Employment
import domain.shared.enums.person.Sex
import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.behavior.MinimalistHousehold
import edu.kit.ifv.units.Distance

data class EngineAlternative(
    val workDistance: Distance,
    val educationDistance: Distance,
    val sex: Sex,
    val employment: Employment,
    val age: Int,
    val householdNumberOfCars: Int,
    val householdSize: Int,
    val regionType: SizebasedRegiostarClassification,
    val isWorking: Boolean,
    val isParttime: Boolean,
    val isHomekeeper: Boolean,
    val isStudentTertiary: Boolean,
    val isStudentSecondary: Boolean,
    val isEducationEmployment: Boolean,
    val isUnemployed: Boolean,
    val isRetired: Boolean,

) {

    companion object {
        fun <X> fromHousehold(
            person: MaximumPersonAttributes,
            household: MinimalistHousehold<X, MaximumPersonAttributes>,
        ): EngineAlternative where X : MinimumHouseholdAttributes, X : HasNumberOfCars {
            val employment: Employment = person.employment
            return EngineAlternative(
                workDistance = person.distanceWork,
                educationDistance = person.distanceEducation,
                sex = person.sex,
                employment = employment,
                age = person.age,
                householdNumberOfCars = household.attributes.amountOfCars,
                householdSize = household.size,
                regionType = household.attributes.location.attributes.sizebasedRegiostarClassification,
                isWorking = employment == Employment.FULLTIME,
                isParttime = employment == Employment.PARTTIME,
                isHomekeeper = employment == Employment.HOMEKEEPER,
                isStudentTertiary = employment == Employment.STUDENT_TERTIARY,
                isStudentSecondary = employment == Employment.STUDENT_SECONDARY,
                isEducationEmployment = employment == Employment.EDUCATION,
                isUnemployed = employment == Employment.UNEMPLOYED,
                isRetired = employment == Employment.RETIRED,
            )
        }
    }
}
