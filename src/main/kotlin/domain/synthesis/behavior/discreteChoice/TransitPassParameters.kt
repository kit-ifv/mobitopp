package domain.synthesis.behavior.discreteChoice

import domain.synthesis.behavior.SurveyInfo
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.employment
import domain.synthesis.behavior.hasLicence
import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit
import units.`€`

val YesTransitPass = TransitPassParameters(
    base = -0.312173681653899,
    twoMembers = -0.3256945305058742,
    threeMembers = 0.591424576249088,
    fourMembers = 0.476000821405854,
    fiveMembers = 0.702434997336812,
    sixMembers = 1.2453115261173,
    sevenMembers = 2.2985846007181796,
    eightToTwentyMembers = 3.28480976565317,
    female = 0.2756731876254076,
    age0to9 = -3.25644379813947,
    age10to17 = -1.28889752460068,
    age30to39 = -0.706306055009702,
    age40to49 = -0.73068644042899,
    age50to59 = -0.7409165188089739,
    age60to69 = -0.7124906153761179,
    age70to79 = -0.12276385579751398,
    age80plus = -0.02099703295852401,
    hasLicence = 0.995492805381457,
    oneCar = -0.691970185373246,
    twoCars = -1.27273744779308,
    threeCars = -2.06584837619823,
    fourOrMoreCars = -1.96838830652269,
    studentPrimary = 1.44413587911005,
    studentSecondary = 1.44413587911005,
    studentTertiary = 1.8994142152866698,
    homeKeeper = -1.78872181887129,
    partTime = -0.210495104059506,
    incomeIn750to1499 = 0.13386078964763,
    incomeIn1500to2249 = 0.184411681953943,
    incomeIn2250to2999 = 0.555024064024888,
    incomeIn3000to3999 = 0.397233443164943,
    incomeAtLeast4000 = 0.457279239343281,
    numChildsAge0to5 = -0.291721944623581,
    numChildsAge6to17 = -0.305570335406822
)

data class TransitPassParameters(
    val base: Double,
    val twoMembers: Double,
    val threeMembers: Double,
    val fourMembers: Double,
    val fiveMembers: Double,
    val sixMembers: Double,
    val sevenMembers: Double,
    val eightToTwentyMembers: Double,
    val female: Double,
    val age0to9: Double,
    val age10to17: Double,
    val age30to39: Double,
    val age40to49: Double,
    val age50to59: Double,
    val age60to69: Double,
    val age70to79: Double,
    val age80plus: Double,
    val hasLicence: Double,
    val oneCar: Double,
    val twoCars: Double,
    val threeCars: Double,
    val fourOrMoreCars: Double,
    val studentPrimary: Double,
    val studentSecondary: Double,
    val studentTertiary: Double,
    val homeKeeper: Double,
    val partTime: Double,
    val incomeIn750to1499: Double,
    val incomeIn1500to2249: Double,
    val incomeIn2250to2999: Double,
    val incomeIn3000to3999: Double,
    val incomeAtLeast4000: Double,
    val numChildsAge0to5: Double,
    val numChildsAge6to17: Double
)

@Suppress("MagicNumber") // These magic numbers are ok
data class TicketCharacteristics(
    val household: SynthesisHousehold<out SurveyInfo>,
    val person: SynthesisPerson<out SurveyInfo>
) {
    val householdSize = household.members.size
    val gender = person.sex
    val age = person.age
    val hasDrivingLicence = person.hasLicence
    val householdNumCars = household.amountOfCars
    val employment = person.employment
    val income = household.income
    val numChildsAgeFiveOrLess = household.members.count { it.age in 0..5 }
    val numAgeInSixToSeventeen = household.members.count { it.age in 6..17 }
}

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

// @Suppress("MagicNumber") // In a utility function everything is magic, so complaining about age in 10..18 is irrelevant
// val transitPassChoiceModel = KnownDiscreteChoiceModel<Boolean, TicketAlternative, TransitPassParameters>(
//    AllocatedLogit.create {
//        option(false) {
//            0.0
//        }
//        option(true) {
//            base +
//                (it.householdSize == 2) * twoMembers +
//                (it.householdSize == 3) * threeMembers +
//                (it.householdSize == 4) * fourMembers +
//                (it.householdSize == 5) * fiveMembers +
//                (it.householdSize == 6) * sixMembers +
//                (it.householdSize == 7) * sevenMembers +
//                (it.householdSize in 8..20) * eightToTwentyMembers +
//
//                (it.gender == Sex.FEMALE) * female +
//
//                (it.age in 0..9) * age0to9 +
//                (it.age in 10..17) * age10to17 +
//                (it.age in 30..39) * age30to39 +
//                (it.age in 40..49) * age40to49 +
//                (it.age in 50..59) * age50to59 +
//                (it.age in 60..69) * age60to69 +
//                (it.age in 70..79) * age70to79 +
//                (it.age >= 80) * age80plus +
//
//                (it.hasDrivingLicence) * hasLicence +
//
//                (it.householdNumCars == 1) * oneCar +
//                (it.householdNumCars == 2) * twoCars +
//                (it.householdNumCars == 3) * threeCars +
//                (it.householdNumCars >= 4) * fourOrMoreCars +
//
//                (it.employment == Employment.STUDENT_PRIMARY) * studentPrimary +
//                (it.employment == Employment.STUDENT_SECONDARY) * studentSecondary +
//                (it.employment == Employment.STUDENT_TERTIARY) * studentTertiary +
//                (it.employment == Employment.HOMEKEEPER) * homeKeeper +
//                (it.employment == Employment.PARTTIME) * partTime +
//
//                (it.income in 750.`€`..<1500.`€`) * incomeIn750to1499 +
//                (it.income in 1500.`€`..<2250.`€`) * incomeIn1500to2249 +
//                (it.income in 2250.`€`..<3000.`€`) * incomeIn2250to2999 +
//                (it.income in 3000.`€`..<4000.`€`) * incomeIn3000to3999 +
//                (it.income >= 4000.`€`) * incomeAtLeast4000 +
//
//                (it.numChildsAgeFiveOrLess) * numChildsAge0to5 +
//                (it.numAgeInSixToSeventeen) * numChildsAge6to17
//        }
//    },
// )
