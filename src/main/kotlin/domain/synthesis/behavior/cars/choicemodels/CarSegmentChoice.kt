package domain.synthesis.behavior.cars.choicemodels

import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.HasBiologicalSex
import domain.synthesis.attributes.person.HasCommuteDistance
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.MinimalistPerson
import domain.synthesis.data.person.Sex
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance

data class CarSegmentChoice(
    val commuterDistance: Distance,
    val householdSize: Int,
    val householdIncome: Currency,
    val numberOfCars: Int,
    val sex: Sex,
    val isCommuting: Boolean,
) {

    companion object {
        fun <S, T> create(
            person: MinimalistPerson<T>,
            household: MinimalistHousehold<S, T>,
        ): CarSegmentChoice
            where S : HasNumberOfCars,
                  S : MinimumHouseholdAttributes,
                  T : HasCommuteDistance,
                  T : HasBiologicalSex =
            CarSegmentChoice(
                person.attributes.distanceWork,
                household.size,
                household.attributes.income,
                household.attributes.amountOfCars,
                person.attributes.sex,
                false,
            )
    }
}
