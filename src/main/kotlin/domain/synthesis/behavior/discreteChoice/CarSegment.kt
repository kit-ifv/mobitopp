package domain.synthesis.behavior.discreteChoice

import domain.synthesis.behavior.CommuteDistance
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.data.CarSegment
import domain.synthesis.data.Sex
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.kilometers
import kotlin.random.Random

data class CarSegmentSituation(
    val person: SynthesisPerson<out CommuteDistance>,
    val household: SynthesisHousehold<out CommuteDistance>
) {
    // TODO delegate to household once merged with default household dataclass
    val random: Random = Random(System.currentTimeMillis())
    fun with(choice: CarSegment) = choice.toAlternative(person, household)
}

data class CarSegmentChoice(

    val commuterDistance: Distance,
    val householdSize: Int,
    val householdIncome: Currency,
    val numberOfCars: Int,
    val sex: Sex,
    val isCommuting: Boolean
) {
    constructor(
        person: SynthesisPerson<out CommuteDistance>,
        household: SynthesisHousehold<out CommuteDistance>
    ) : this(
        person.information.distanceWork,
        household.size,
        household.income,
        household.amountOfCars,
        person.sex,
        false
    )
}

fun CarSegment.toAlternative(
    person: SynthesisPerson<out CommuteDistance>,
    household: SynthesisHousehold<out CommuteDistance>
): CarSegmentChoice {
    return CarSegmentChoice(
        person.information.distanceWork,
        household.size,
        household.income,
        household.amountOfCars,
        person.sex,
        false // TODO extract the infomration that the person is commuting
    )
}

@Suppress(
    "MagicNumber",
    "ConstructorParameterNaming"
) // The parameters are magic numbers, but there is nothing we can do about that
data class CarSegmentParameters(
    val MIDSIZE_CONSTANT: Double = 2.016,
    val MIDSIZE_DIST_COMM_0_TO_9: Double = -0.5936,
    val MIDSIZE_DIST_COMM_10_TO_19: Double = -0.4313,
    val MIDSIZE_DIST_COMM_20_TO_29: Double = -0.1506,
    val MIDSIZE_DIST_COMM_30_TO_39: Double = -0.5102,
    val MIDSIZE_DIST_COMM_40_TO_49: Double = -0.2889,
    val MIDSIZE_DIST_COMM_50_PLUS: Double = -0.1182,
    val MIDSIZE_HHGRO_1: Double = -0.5424,
    val MIDSIZE_HHGRO_2: Double = 0.0,
    val MIDSIZE_HHGRO_3_PLUS: Double = 0.1616,
    val MIDSIZE_HHINCOME_0_TO_500: Double = -1.3838,
    val MIDSIZE_HHINCOME_500_TO_1000: Double = -0.1282,
    val MIDSIZE_HHINCOME_1000_TO_1500: Double = -0.3799,
    val MIDSIZE_HHINCOME_1500_TO_2000: Double = 0.0,
    val MIDSIZE_HHINCOME_2000_TO_2500: Double = 0.3546,
    val MIDSIZE_HHINCOME_2500_TO_3000: Double = 0.3912,
    val MIDSIZE_HHINCOME_3000_TO_3500: Double = 0.3744,
    val MIDSIZE_HHINCOME_3500_PLUS: Double = 0.4758,
    val MIDSIZE_HHINCOME_NA: Double = 0.2651,
    val MIDSIZE_PKWHH_2_PLUS: Double = -0.8820,
    val MIDSIZE_FEMALE: Double = -0.9454,
    val LARGE_CONSTANT: Double = -0.97,
    val LARGE_DIST_COMM_0_TO_9: Double = -0.6613,
    val LARGE_DIST_COMM_10_TO_19: Double = -0.6745,
    val LARGE_DIST_COMM_20_TO_29: Double = -0.5051,
    val LARGE_DIST_COMM_30_TO_39: Double = -0.2768,
    val LARGE_DIST_COMM_40_TO_49: Double = -0.6331,
    val LARGE_DIST_COMM_50_PLUS: Double = -0.7373,
    val LARGE_HHGRO_1: Double = -0.2880,
    val LARGE_HHGRO_2: Double = 0.0,
    val LARGE_HHGRO_3_PLUS: Double = 0.2906,
    val LARGE_HHINCOME_0_TO_500: Double = -11.7820,
    val LARGE_HHINCOME_500_TO_1000: Double = -1.3468,
    val LARGE_HHINCOME_1000_TO_1500: Double = -0.5860,
    val LARGE_HHINCOME_1500_TO_2000: Double = 0.0,
    val LARGE_HHINCOME_2000_TO_2500: Double = 0.5887,
    val LARGE_HHINCOME_2500_TO_3000: Double = 0.9113,
    val LARGE_HHINCOME_3000_TO_3500: Double = 0.9346,
    val LARGE_HHINCOME_3500_PLUS: Double = 1.6761,
    val LARGE_HHINCOME_NA: Double = 1.2955,
    val LARGE_PKWHH_2_PLUS: Double = -0.5291,
    val LARGE_FEMALE: Double = -1.2392,
) {
    fun toMidsizeParameterSet(): SimplifiedParameters {
        return SimplifiedParameters(
            MIDSIZE_CONSTANT,
            MIDSIZE_DIST_COMM_0_TO_9,
            MIDSIZE_DIST_COMM_10_TO_19,
            MIDSIZE_DIST_COMM_20_TO_29,
            MIDSIZE_DIST_COMM_30_TO_39,
            MIDSIZE_DIST_COMM_40_TO_49,
            MIDSIZE_DIST_COMM_50_PLUS,
            MIDSIZE_HHGRO_1,
            MIDSIZE_HHGRO_2,
            MIDSIZE_HHGRO_3_PLUS,
            MIDSIZE_HHINCOME_0_TO_500,
            MIDSIZE_HHINCOME_500_TO_1000,
            MIDSIZE_HHINCOME_1000_TO_1500,
            MIDSIZE_HHINCOME_1500_TO_2000,
            MIDSIZE_HHINCOME_2000_TO_2500,
            MIDSIZE_HHINCOME_2500_TO_3000,
            MIDSIZE_HHINCOME_3000_TO_3500,
            MIDSIZE_HHINCOME_3500_PLUS,
            MIDSIZE_HHINCOME_NA,
            MIDSIZE_PKWHH_2_PLUS,
            MIDSIZE_FEMALE,
        )
    }

    fun toLargeParameterSet(): SimplifiedParameters {
        return SimplifiedParameters(
            LARGE_CONSTANT,
            LARGE_DIST_COMM_0_TO_9,
            LARGE_DIST_COMM_10_TO_19,
            LARGE_DIST_COMM_20_TO_29,
            LARGE_DIST_COMM_30_TO_39,
            LARGE_DIST_COMM_40_TO_49,
            LARGE_DIST_COMM_50_PLUS,
            LARGE_HHGRO_1,
            LARGE_HHGRO_2,
            LARGE_HHGRO_3_PLUS,
            LARGE_HHINCOME_0_TO_500,
            LARGE_HHINCOME_500_TO_1000,
            LARGE_HHINCOME_1000_TO_1500,
            LARGE_HHINCOME_1500_TO_2000,
            LARGE_HHINCOME_2000_TO_2500,
            LARGE_HHINCOME_2500_TO_3000,
            LARGE_HHINCOME_3000_TO_3500,
            LARGE_HHINCOME_3500_PLUS,
            LARGE_HHINCOME_NA,
            LARGE_PKWHH_2_PLUS,
            LARGE_FEMALE,
        )
    }
}

data class SimplifiedParameters(
    val constant: Double,
    val commuterDistance0to10kilometers: Double,
    val commuterDistance10to20kilometers: Double,
    val commuterDistance20to30kilometers: Double,
    val commuterDistance30to40kilometers: Double,
    val commuterDistance40to50kilometers: Double,
    val commuterDistanceAtLeast50kilometers: Double,
    val householdSize1: Double,
    val householdSize2: Double,
    val householdSize3orMore: Double,
    val incomeIn0to499: Double,
    val incomeIn500to999: Double,
    val incomeIn1000to1499: Double,
    val incomeIn1500to1999: Double,
    val incomeIn2000to2499: Double,
    val incomeIn2500to2999: Double,
    val incomeIn3000to3499: Double,
    val incomeAtLeast3500: Double,
    val incomeNegative: Double,
    val atLeast2Cars: Double,
    val female: Double
)

val carSegmentChoiceModel = DiscreteStructure<CarSegment, CarSegmentChoice, CarSegmentParameters> {
    option(CarSegment.SMALL) {
        0.0
    }
    option(CarSegment.MIDSIZE, parameters = { toMidsizeParameterSet() }) { _, characteristics ->
        defaultUtilityFunction(this, characteristics)
    }
    option(CarSegment.LARGE, parameters = { toLargeParameterSet() }) { _, characteristics ->
        defaultUtilityFunction(this, characteristics)
    }
}.multinomialLogit("ExampleTransitPassModel")

@Suppress("MagicNumber")
private val defaultUtilityFunction: SimplifiedParameters.(CarSegmentChoice) -> Double =
    {
        constant +
            if (it.isCommuting) {
                (it.commuterDistance in 0.kilometers..<10.kilometers) * commuterDistance0to10kilometers +
                    (it.commuterDistance in 10.kilometers..<20.kilometers) * commuterDistance10to20kilometers +
                    (it.commuterDistance in 20.kilometers..<30.kilometers) * commuterDistance20to30kilometers +
                    (it.commuterDistance in 30.kilometers..<40.kilometers) * commuterDistance30to40kilometers +
                    (it.commuterDistance in 40.kilometers..<50.kilometers) * commuterDistance40to50kilometers +
                    (it.commuterDistance >= 50.kilometers) * commuterDistanceAtLeast50kilometers
            } else {
                0.0
            } +

            (it.householdSize == 1) * householdSize1 +
            (it.householdSize == 2) * householdSize2 +
            (it.householdSize >= 3) * householdSize3orMore +

            (it.householdIncome in 0.euros..<500.euros) * incomeIn0to499 +
            (it.householdIncome in 500.euros..<1000.euros) * incomeIn500to999 +
            (it.householdIncome in 1000.euros..<1500.euros) * incomeIn1000to1499 +
            (it.householdIncome in 1500.euros..<2000.euros) * incomeIn1500to1999 +
            (it.householdIncome in 2000.euros..<2500.euros) * incomeIn2000to2499 +
            (it.householdIncome in 2500.euros..<3000.euros) * incomeIn2500to2999 +
            (it.householdIncome in 3000.euros..<3500.euros) * incomeIn3000to3499 +
            (it.householdIncome >= 3500.euros) * incomeAtLeast3500 +
            (it.householdIncome <= 0.euros) * incomeNegative +

            (it.numberOfCars >= 2) * atLeast2Cars +
            (it.sex == Sex.FEMALE) * female
    }
