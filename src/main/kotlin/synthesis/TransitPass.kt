package synthesis

import Situation
import Term
import domain.data.Employment
import domain.data.Person
import domain.data.Sex
import domain.data.Zone
import domain.location.LOCATIONUNKNOWN
import domain.location.Metrics
import modeling.discreteChoice.DiscreteChoiceModel
import modeling.discreteChoice.MultinomialLogit
import modeling.discreteChoice.ReadParameterFile
import modeling.discreteChoice.getOrWarn
import plus
import prop
import property
import times
import units.euros
import usecases.LegacyMode
import utils.collections.select
import utils.units.AbsoluteTime
import kotlin.random.Random

class TransitPassParameters(householdBuilder: SynthesisHouseholdBuilder, person: PersonInfo) {
    val size = householdBuilder.members.size
    val gender = person.sex
    val age = person.age
    val drivingLicence = person.driverLicence
    val numberOfCars = householdBuilder.amountOfCars
    val employment = person.employment
    val income = householdBuilder.income
    val numChildsAgeFiveOrLess = householdBuilder.members.count { it.age in 0..5 }
    val numAgeInSixToSeventeen = householdBuilder.members.count { it.age in 6..17 }
}

interface ITransitPassParameters {
    val base: Double
    fun calculate(parameters: TransitPassParameters): Double {
        return base + calculateSize(parameters) +
                calculateGender(parameters) +
                calculateAge(parameters) +
                calculateLicence(parameters) +
                calculateNumCars(parameters) +
                calculateEmployment(parameters) +
                calculateIncome(parameters) +
                calculateSmallChilds(parameters) +
                calculateMidChilds(parameters)
    }

    fun calculateSize(parameters: TransitPassParameters): Double = 0.0
    fun calculateGender(parameters: TransitPassParameters): Double = 0.0
    fun calculateAge(parameters: TransitPassParameters): Double = 0.0
    fun calculateLicence(parameters: TransitPassParameters): Double = 0.0
    fun calculateNumCars(parameters: TransitPassParameters): Double = 0.0
    fun calculateEmployment(parameters: TransitPassParameters): Double = 0.0
    fun calculateIncome(parameters: TransitPassParameters): Double = 0.0
    fun calculateSmallChilds(parameters: TransitPassParameters): Double = 0.0
    fun calculateMidChilds(parameters: TransitPassParameters): Double = 0.0
}

interface Size : ITransitPassParameters {
    val twoMembers: Double
    val threeMembers: Double
    val fourMembers: Double
    val fiveMembers: Double
    val sixMembers: Double
    val sevenMembers: Double
    val eightToTwentyMembers: Double

    override fun calculateSize(parameters: TransitPassParameters): Double {
        return when (parameters.size) {
            2 -> twoMembers
            3 -> threeMembers
            4 -> fourMembers
            5 -> fiveMembers
            6 -> sixMembers
            7 -> sevenMembers
            in 8..20 -> eightToTwentyMembers
            else -> 0.0
        }
    }
}

interface Gender : ITransitPassParameters {
    val female: Double
    override fun calculateGender(parameters: TransitPassParameters): Double {
        return when (parameters.gender) {
            Sex.FEMALE -> female
            else -> 0.0
        }
    }
}

interface Age : ITransitPassParameters {
    val age0to9: Double
    val age10to17: Double
    val age30to39: Double
    val age40to49: Double
    val age50to59: Double
    val age60to69: Double
    val age70to79: Double
    val age80plus: Double

    override fun calculateAge(parameters: TransitPassParameters): Double {
        return when (parameters.age) {
            in 0..9 -> age0to9
            in 10..17 -> age10to17
            in 30..39 -> age30to39
            in 40..49 -> age40to49
            in 50..59 -> age50to59
            in 60..69 -> age60to69
            in 70..79 -> age70to79
            in 80..Int.MAX_VALUE -> age80plus
            else -> 0.0
        }
    }
}

interface DrivingLicence : ITransitPassParameters {
    val hasLicence: Double
    override fun calculateLicence(parameters: TransitPassParameters): Double {
        return if (parameters.drivingLicence) hasLicence else 0.0
    }
}

interface NumCars : ITransitPassParameters {
    val oneCar: Double
    val twoCars: Double
    val threeCars: Double
    val fourOrMoreCars: Double

    override fun calculateNumCars(parameters: TransitPassParameters): Double {

        return when (parameters.numberOfCars) {
            1 -> oneCar
            2 -> twoCars
            3 -> threeCars
            in 4..Int.MAX_VALUE -> fourOrMoreCars
            else -> 0.0
        }
    }
}

private interface Employment : ITransitPassParameters {
    val studentPrimary: Double
    val studentSecondary: Double
    val studentTertiary: Double
    val homeKeeper: Double
    val partTime: Double

    override fun calculateEmployment(parameters: TransitPassParameters): Double {
        return when (parameters.employment) {
            Employment.STUDENT_PRIMARY -> studentPrimary
            Employment.STUDENT_SECONDARY -> studentSecondary
            Employment.STUDENT_TERTIARY -> studentTertiary
            Employment.HOMEKEEPER -> homeKeeper
            Employment.PARTTIME -> partTime
            else -> 0.0
        }
    }


}

interface Income : ITransitPassParameters {
    val incomeIn750to1499: Double
    val incomeIn1500to2249: Double
    val incomeIn2250to2999: Double
    val incomeIn3000to3999: Double
    val incomeAtLeast4000: Double

    override fun calculateIncome(parameters: TransitPassParameters): Double {
        return when (parameters.income) {
            in 750.euros..<1500.euros -> incomeIn750to1499
            in 1500.euros..<2500.euros -> incomeIn1500to2249
            in 2250.euros..<3000.euros -> incomeIn2250to2999
            in 3000.euros..<4000.euros -> incomeIn3000to3999
            in 4000.euros..Int.MAX_VALUE.euros -> incomeAtLeast4000
            else -> 0.0
        }
    }
}

interface SmallChilds : ITransitPassParameters {
    val numChildsAge0to5: Double
    override fun calculateSmallChilds(parameters: TransitPassParameters): Double {
        return parameters.numChildsAgeFiveOrLess * numChildsAge0to5
    }
}

interface MediumChilds : ITransitPassParameters {
    val numChildsAge6to17: Double
    override fun calculateMidChilds(parameters: TransitPassParameters): Double {
        return parameters.numAgeInSixToSeventeen * numChildsAge6to17
    }
}


val NoTransitPass = object : ITransitPassParameters {
    override val base: Double = 0.0

}
val YesTransitPass = TransitPass(
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

data class TransitPass(
    override val base: Double,
    override val twoMembers: Double,
    override val threeMembers: Double,
    override val fourMembers: Double,
    override val fiveMembers: Double,
    override val sixMembers: Double,
    override val sevenMembers: Double,
    override val eightToTwentyMembers: Double,
    override val female: Double,
    override val age0to9: Double,
    override val age10to17: Double,
    override val age30to39: Double,
    override val age40to49: Double,
    override val age50to59: Double,
    override val age60to69: Double,
    override val age70to79: Double,
    override val age80plus: Double,
    override val hasLicence: Double,
    override val oneCar: Double,
    override val twoCars: Double,
    override val threeCars: Double,
    override val fourOrMoreCars: Double,
    override val studentPrimary: Double,
    override val studentSecondary: Double,
    override val studentTertiary: Double,
    override val homeKeeper: Double,
    override val partTime: Double,
    override val incomeIn750to1499: Double,
    override val incomeIn1500to2249: Double,
    override val incomeIn2250to2999: Double,
    override val incomeIn3000to3999: Double,
    override val incomeAtLeast4000: Double,
    override val numChildsAge0to5: Double,
    override val numChildsAge6to17: Double
) : ITransitPassParameters, Size, Gender, Age, DrivingLicence, NumCars, synthesis.Employment, Income, SmallChilds,
    MediumChilds {
    companion object {
        fun parse(values: Map<String, Double>): TransitPass {
            return TransitPass(
                base = values.getOrWarn("asc_Ticket"),
                twoMembers = values.getOrWarn("b_hhgro_2"),
                threeMembers = values.getOrWarn("b_hhgro_3"),
                fourMembers = values.getOrWarn("b_hhgro_4"),
                fiveMembers = values.getOrWarn("b_hhgro_5"),
                sixMembers = values.getOrWarn("b_hhgro_6"),
                sevenMembers = values.getOrWarn("b_hhgro_7"),
                eightToTwentyMembers = values.getOrWarn("b_hhgro_8"),
                female = values.getOrWarn("b_weibl"),
                age0to9 = values.getOrWarn("b_alter1a"),
                age10to17 = values.getOrWarn("b_alter1b"),
                age30to39 = values.getOrWarn("b_alter3"),
                age40to49 = values.getOrWarn("b_alter4"),
                age50to59 = values.getOrWarn("b_alter5"),
                age60to69 = values.getOrWarn("b_alter6"),
                age70to79 = values.getOrWarn("b_alter7"),
                age80plus = values.getOrWarn("b_alter8"),
                hasLicence = values.getOrWarn("b_no_fspkw"),
                oneCar = values.getOrWarn("b_pkwhh1"),
                twoCars = values.getOrWarn("b_pkwhh2"),
                threeCars = values.getOrWarn("b_pkwhh3"),
                fourOrMoreCars = values.getOrWarn("b_pkwhh4"),
                studentPrimary = values.getOrWarn("b_schueler"),
                studentSecondary = values.getOrWarn("b_schueler"),
                studentTertiary = values.getOrWarn("b_student"),
                homeKeeper = values.getOrWarn("b_home"),
                partTime = values.getOrWarn("b_parttime"),
                incomeIn750to1499 = values.getOrWarn("b_EINKO2"),
                incomeIn1500to2249 = values.getOrWarn("b_EINKO3"),
                incomeIn2250to2999 = values.getOrWarn("b_EINKO4"),
                incomeIn3000to3999 = values.getOrWarn("b_EINKO5"),
                incomeAtLeast4000 = values.getOrWarn("b_EINKO6"),
                numChildsAge0to5 = values.getOrWarn("b_p05"),
                numChildsAge6to17 = values.getOrWarn("b_p617"),
            )
        }

        fun parse(string: String): TransitPass {
            return parse(ReadParameterFile.read(string))
        }
    }
}
data class TicketSituation(
    override val choice: Boolean,
    val expert: Double = 0.0,
//    val household: SurveyHousehold,
): Situation<Boolean> {
    val KUNGFU = property<Boolean> { 10.0 }


}

val nope = object: Situation<Boolean> {
    override val choice: Boolean = false
    val interesting: List<Int> = emptyList()

}
class ESituation(override val choice: Int): Situation<Int>
val yesTicket = TicketSituation(true)
val sit = ESituation(1)
val selectionModel = MultinomialLogit.build<Boolean, TransitPassParameters> {
    option(false) { _, p -> NoTransitPass.calculate(p) }
//    option(true) { _, p -> YesTransitPass.calculate(p) }
    optionE(yesTicket) {
        expert * KUNGFU + KUNGFU
    }
    constant(nope) {
        1.0
    }
}

val transitPassDiscreteChoiceModel = DiscreteChoiceModel(selectionModel)

fun DiscreteChoiceModel<Boolean, TransitPassParameters>.select(
    householdBuilder: SynthesisHouseholdBuilder,
    person: SynthesisPerson
): Boolean {
    return select(TransitPassParameters(householdBuilder, person))
}