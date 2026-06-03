package domain.synthesis.behavior.cars.choicemodels

import domain.shared.car.CarSegment
import domain.shared.enums.person.Sex
import domain.synthesis.attributes.household.HasIncome
import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.HasBiologicalSex
import domain.synthesis.attributes.person.HasCommuteDistance
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.MinimalistPerson
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

fun <S, T> CarSegment.toAlternative(
    person: MinimalistPerson<T>,
    household: MinimalistHousehold<S, T>,
): CarSegmentChoice where S : HasNumberOfCars, S : HasIncome, T : HasBiologicalSex, T : HasCommuteDistance =
    CarSegmentChoice(
        person.attributes.distanceWork,
        household.size,
        household.attributes.income,
        household.attributes.amountOfCars,
        person.attributes.sex,
        false, // TODO extract the infomration that the person is commuting
    )
