@file: Suppress("MagicNumber", "ConstructorParameterNaming", "MatchingDeclarationName")

package domain.simulation.behavior

import domain.shared.enums.LegacyMode
import domain.shared.enums.Mode
import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import edu.kit.ifv.mobitopp.actitoppNG.utils.D
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.CurrencyUnit
import kotlin.time.Duration

@Suppress("MagicNumber", "ConstructorParameterNaming")
data class ModeChoiceParameters(
    val asc_ped: Double = 1.2,
    val asc_bike: Double = -0.7,
    val asc_car_d: Double = 0.0,
    val asc_car_p: Double = -2.4,
    val asc_put: Double = -2.1,
    val age_0_17_on_asc_car_d: Double = -4.4,
    val age_18_29_on_asc_car_d: Double = 0.3,
    val age_50_59_on_asc_car_d: Double = 0.5,
    val age_60_69_on_asc_car_d: Double = 0.8,
    val age_70_100_on_asc_car_d: Double = 1.0,
    val female_on_asc_car_d: Double = -0.5,
    val beruft_on_asc_car_d: Double = 0.3,
    val inc_high_on_asc_car_d: Double = 1.1,
    val zk_on_asc_car_d: Double = -1.2,
    val age_0_17_on_asc_put: Double = 1.4,
    val age_18_29_on_asc_put: Double = 0.8,
    val age_50_59_on_asc_put: Double = 0.3,
    val age_60_69_on_asc_put: Double = 0.4,
    val age_70_100_on_asc_put: Double = 0.9,
    val female_on_asc_put: Double = 0.2,
    val beruft_on_asc_put: Double = 0.0,
    val inc_high_on_asc_put: Double = 0.5,
    val zk_on_asc_put: Double = 1.7,
    val age_0_17_on_asc_bike: Double = -0.3,
    val age_18_29_on_asc_bike: Double = 0.5,
    val age_50_59_on_asc_bike: Double = 0.3,
    val age_60_69_on_asc_bike: Double = 0.0,
    val age_70_100_on_asc_bike: Double = 0.0,
    val female_on_asc_bike: Double = -0.1,
    val beruft_on_asc_bike: Double = 0.2,
    val inc_high_on_asc_bike: Double = 0.0,
    val zk_on_asc_bike: Double = -0.6,
    val age_0_17_on_asc_car_p: Double = 1.2,
    val age_18_29_on_asc_car_p: Double = 0.9,
    val age_50_59_on_asc_car_p: Double = 0.3,
    val age_60_69_on_asc_car_p: Double = 1.0,
    val age_70_100_on_asc_car_p: Double = 1.4,
    val female_on_asc_car_p: Double = 0.7,
    val beruft_on_asc_car_p: Double = 0.0,
    val inc_high_on_asc_car_p: Double = 1.0,
    val zk_on_asc_car_p: Double = -0.4,
    val b_tt_ped: Double = -0.1,
    val b_tt_bike: Double = -0.2,
    val b_tt_car_d: Double = -0.2,
    val b_tt_car_p: Double = -0.2,
    val b_tt_put: Double = -0.05,
    val b_acc_put: Double = -0.1,
    val b_u_put: Double = -0.6,
    val b_cost_car_d: Double = -0.2,
    val b_cost_put: Double = -0.5,
    val age_0_17_on_b_cost_car_d: Double = 0.0,
    val age_18_29_on_b_cost_car_d: Double = 0.0,
    val age_50_59_on_b_cost_car_d: Double = 0.0,
    val age_60_69_on_b_cost_car_d: Double = 0.0,
    val age_70_100_on_b_cost_car_d: Double = 0.0,
    val female_on_b_cost_car_d: Double = 0.0,
    val beruft_on_b_cost_car_d: Double = 0.06,
    val inc_high_on_b_cost_car_d: Double = 0.09,
    val zk_on_b_cost_car_d: Double = 0.0,
    val age_0_17_on_b_cost_put: Double = -0.2,
    val age_18_29_on_b_cost_put: Double = 0.0,
    val age_50_59_on_b_cost_put: Double = 0.0,
    val age_60_69_on_b_cost_put: Double = 0.0,
    val age_70_100_on_b_cost_put: Double = 0.0,
    val female_on_b_cost_put: Double = 0.0,
    val beruft_on_b_cost_put: Double = 0.0,
    val inc_high_on_b_cost_put: Double = 0.1,
    val zk_on_b_cost_put: Double = 0.0,
    val age_0_17_on_asc_ped: Double = 0.3,
    val age_18_29_on_asc_ped: Double = -0.1,
    val age_50_59_on_asc_ped: Double = 0.2,
    val age_60_69_on_asc_ped: Double = 0.2,
    val age_70_100_on_asc_ped: Double = -0.2,
    val beruft_on_asc_ped: Double = -0.1,
    val female_on_asc_ped: Double = 0.2,
    val inc_high_on_asc_ped: Double = -0.2,
    val zk_on_asc_ped: Double = 0.2,
)

val workingEmploymentTypes = listOf(Employment.FULLTIME, Employment.PARTTIME, Employment.MARGINAL)
val Currency.euros get() = this.toDouble(CurrencyUnit.EUROS)

val ModeChoiceCharacteristics.age: Int get() = person.age
val ModeChoiceCharacteristics.isEmployed: Double get() = (person.employment in workingEmploymentTypes).D
val ModeChoiceCharacteristics.isFemale: Double get() = (person.sex == Sex.FEMALE).D
val ModeChoiceCharacteristics.ecoStatus: Int get() = person.household.economicStatus.code
val ModeChoiceCharacteristics.hasCommuterTicket: Double get() = person.hasCommuterTicket.D
val ModeChoiceCharacteristics.isAgeIn0To17: Double get() = (age in 0..17).D
val ModeChoiceCharacteristics.isAgeIn18To29: Double get() = (age in 18..29).D
val ModeChoiceCharacteristics.isAgeIn50To59: Double get() = (age in 50..59).D
val ModeChoiceCharacteristics.isAgeIn60To69: Double get() = (age in 60..69).D
val ModeChoiceCharacteristics.isAgeIn70Plus: Double get() = (age in 70..100).D
val ModeChoiceCharacteristics.isEcoStatusHigh: Double get() = (ecoStatus in 4..5).D
fun ModeChoiceCharacteristics.travelTime(mode: Mode): Duration = impedance.duration(origin, destination, mode, time)
fun ModeChoiceCharacteristics.travelCost(mode: Mode): Currency = impedance.cost(origin, destination, mode, time)

@Suppress("MagicNumber")
val legacyModeChoice = DiscreteStructure<Mode, ModeChoiceCharacteristics, ModeChoiceParameters> {
    option(LegacyMode.PEDESTRIAN) { mode, characteristics ->
        asc_ped +
            age_0_17_on_asc_ped * characteristics.isAgeIn0To17 +
            age_18_29_on_asc_ped * characteristics.isAgeIn18To29 +
            age_50_59_on_asc_ped * characteristics.isAgeIn50To59 +
            age_60_69_on_asc_ped * characteristics.isAgeIn60To69 +
            age_70_100_on_asc_ped * characteristics.isAgeIn70Plus +
            beruft_on_asc_ped * characteristics.isEmployed +
            female_on_asc_ped * characteristics.isFemale +
            inc_high_on_asc_ped * characteristics.isEcoStatusHigh +
            zk_on_asc_ped * characteristics.hasCommuterTicket +
            b_tt_ped * (characteristics.travelTime(mode).inWholeMinutes)
    }

    option(LegacyMode.BIKE) { mode, characteristics ->
        asc_bike +
            age_0_17_on_asc_bike * characteristics.isAgeIn0To17 +
            age_18_29_on_asc_bike * characteristics.isAgeIn18To29 +
            age_50_59_on_asc_bike * characteristics.isAgeIn50To59 +
            age_60_69_on_asc_bike * characteristics.isAgeIn60To69 +
            age_70_100_on_asc_bike * characteristics.isAgeIn70Plus +
            beruft_on_asc_bike * characteristics.isEmployed +
            female_on_asc_bike * characteristics.isFemale +
            inc_high_on_asc_bike * characteristics.isEcoStatusHigh +
            zk_on_asc_bike * characteristics.hasCommuterTicket +
            b_tt_bike * (characteristics.travelTime(mode).inWholeMinutes)
    }

    option(LegacyMode.CAR) { mode, characteristics ->
        asc_car_d +
            age_0_17_on_asc_car_d * characteristics.isAgeIn0To17 +
            age_18_29_on_asc_car_d * characteristics.isAgeIn18To29 +
            age_50_59_on_asc_car_d * characteristics.isAgeIn50To59 +
            age_60_69_on_asc_car_d * characteristics.isAgeIn60To69 +
            age_70_100_on_asc_car_d * characteristics.isAgeIn70Plus +
            beruft_on_asc_car_d * characteristics.isEmployed +
            female_on_asc_car_d * characteristics.isFemale +
            inc_high_on_asc_car_d * characteristics.isEcoStatusHigh +
            zk_on_asc_car_d * characteristics.hasCommuterTicket +
            b_tt_car_d * (characteristics.travelTime(mode).inWholeMinutes) +
            (
                b_cost_car_d +
                    age_0_17_on_b_cost_car_d * characteristics.isAgeIn0To17 +
                    age_18_29_on_b_cost_car_d * characteristics.isAgeIn18To29 +
                    age_50_59_on_b_cost_car_d * characteristics.isAgeIn50To59 +
                    age_60_69_on_b_cost_car_d * characteristics.isAgeIn60To69 +
                    age_70_100_on_b_cost_car_d * characteristics.isAgeIn70Plus +
                    beruft_on_b_cost_car_d * characteristics.isEmployed +
                    female_on_b_cost_car_d * characteristics.isFemale +
                    inc_high_on_b_cost_car_d * characteristics.isEcoStatusHigh +
                    zk_on_b_cost_car_d * characteristics.hasCommuterTicket
                ) * characteristics.travelCost(mode).euros
    }

    option(LegacyMode.PASSENGER) { mode, characteristics ->
        asc_car_p +
            age_0_17_on_asc_car_p * characteristics.isAgeIn0To17 +
            age_18_29_on_asc_car_p * characteristics.isAgeIn18To29 +
            age_50_59_on_asc_car_p * characteristics.isAgeIn50To59 +
            age_60_69_on_asc_car_p * characteristics.isAgeIn60To69 +
            age_70_100_on_asc_car_p * characteristics.isAgeIn70Plus +
            beruft_on_asc_car_p * characteristics.isEmployed +
            female_on_asc_car_p * characteristics.isFemale +
            inc_high_on_asc_car_p * characteristics.isEcoStatusHigh +
            zk_on_asc_car_p * characteristics.hasCommuterTicket +
            b_tt_car_p * (characteristics.travelTime(mode).inWholeMinutes)
    }

    option(LegacyMode.PUBLICTRANSPORT) { mode, characteristics ->
        asc_put +
            age_0_17_on_asc_put * characteristics.isAgeIn0To17 +
            age_18_29_on_asc_put * characteristics.isAgeIn18To29 +
            age_50_59_on_asc_put * characteristics.isAgeIn50To59 +
            age_60_69_on_asc_put * characteristics.isAgeIn60To69 +
            age_70_100_on_asc_put * characteristics.isAgeIn70Plus +
            beruft_on_asc_put * characteristics.isEmployed +
            female_on_asc_put * characteristics.isFemale +
            inc_high_on_asc_put * characteristics.isEcoStatusHigh +
            zk_on_asc_put * characteristics.hasCommuterTicket +
            b_tt_put * (characteristics.travelTime(mode).inWholeMinutes) +
            // b_acc_put
            // b_u_put
            (
                b_cost_put +
                    age_0_17_on_b_cost_put * characteristics.isAgeIn0To17 +
                    age_18_29_on_b_cost_put * characteristics.isAgeIn18To29 +
                    age_50_59_on_b_cost_put * characteristics.isAgeIn50To59 +
                    age_60_69_on_b_cost_put * characteristics.isAgeIn60To69 +
                    age_70_100_on_b_cost_put * characteristics.isAgeIn70Plus +
                    beruft_on_b_cost_put * characteristics.isEmployed +
                    female_on_b_cost_put * characteristics.isFemale +
                    inc_high_on_b_cost_put * characteristics.isEcoStatusHigh +
                    zk_on_b_cost_put * characteristics.hasCommuterTicket
                ) * characteristics.travelCost(mode).euros * (1 - characteristics.hasCommuterTicket) // NO PUT COST IF OWNS TICKET
    }
}.multinomialLogit(
    name = "LegacyModeChoiceModel"
).build(
    parameters = ModeChoiceParameters()
)
