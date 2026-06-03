package domain.synthesis.behavior.transitpass

import domain.synthesis.data.person.Employment
import domain.synthesis.data.person.Sex
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit
import edu.kit.ifv.units.`€`

@Suppress("MagicNumber") // In a utility function everything is magic, so complaining about age in 10..18 is irrelevant
val transitPassChoiceModel = DiscreteStructure<Boolean, TicketCharacteristics, TransitPassParameters> {
    option(false) {
        0.0
    }
    option(true) { _, characteristics ->
        base +
            (characteristics.householdSize == 2) * twoMembers +
            (characteristics.householdSize == 3) * threeMembers +
            (characteristics.householdSize == 4) * fourMembers +
            (characteristics.householdSize == 5) * fiveMembers +
            (characteristics.householdSize == 6) * sixMembers +
            (characteristics.householdSize == 7) * sevenMembers +
            (characteristics.householdSize in 8..20) * eightToTwentyMembers +

            (characteristics.gender == Sex.FEMALE) * female +

            (characteristics.age in 0..9) * age0to9 +
            (characteristics.age in 10..17) * age10to17 +
            (characteristics.age in 30..39) * age30to39 +
            (characteristics.age in 40..49) * age40to49 +
            (characteristics.age in 50..59) * age50to59 +
            (characteristics.age in 60..69) * age60to69 +
            (characteristics.age in 70..79) * age70to79 +
            (characteristics.age >= 80) * age80plus +

            (characteristics.hasDrivingLicence) * hasLicence +

            (characteristics.householdNumCars == 1) * oneCar +
            (characteristics.householdNumCars == 2) * twoCars +
            (characteristics.householdNumCars == 3) * threeCars +
            (characteristics.householdNumCars >= 4) * fourOrMoreCars +

            (characteristics.employment == Employment.STUDENT_PRIMARY) * studentPrimary +
            (characteristics.employment == Employment.STUDENT_SECONDARY) * studentSecondary +
            (characteristics.employment == Employment.STUDENT_TERTIARY) * studentTertiary +
            (characteristics.employment == Employment.HOMEKEEPER) * homeKeeper +
            (characteristics.employment == Employment.PARTTIME) * partTime +

            (characteristics.income in 750.`€`..<1500.`€`) * incomeIn750to1499 +
            (characteristics.income in 1500.`€`..<2250.`€`) * incomeIn1500to2249 +
            (characteristics.income in 2250.`€`..<3000.`€`) * incomeIn2250to2999 +
            (characteristics.income in 3000.`€`..<4000.`€`) * incomeIn3000to3999 +
            (characteristics.income >= 4000.`€`) * incomeAtLeast4000 +

            (characteristics.numChildsAgeFiveOrLess) * numChildsAge0to5 +
            (characteristics.numAgeInSixToSeventeen) * numChildsAge6to17
    }
}.multinomialLogit("ExampleTransitPassMNL")
