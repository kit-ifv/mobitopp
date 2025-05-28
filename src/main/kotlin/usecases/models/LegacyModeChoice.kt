@file: Suppress("MagicNumber", "ConstructorParameterNaming", "MatchingDeclarationName")

package usecases.models

import discreteChoice.models.fixed
import discreteChoice.structure.D
import discreteChoice.structure.DiscreteStructure
import discreteChoice.utility.multinomialLogit
import domain.data.Employment
import domain.data.Sex
import domain.enums.Mode
import units.Currency
import units.CurrencyUnit
import usecases.LegacyMode
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

val ModeChoiceAlternative.age: Int get() = person.age
val ModeChoiceAlternative.isEmployed: Double get() = (person.employment in workingEmploymentTypes).D
val ModeChoiceAlternative.isFemale: Double get() = (person.sex == Sex.FEMALE).D
val ModeChoiceAlternative.ecoStatus: Int get() = person.household.economicStatus.code
val ModeChoiceAlternative.hasCommuterTicket: Double get() = person.hasCommuterTicket.D
val ModeChoiceAlternative.isAgeIn0To17: Double get() = (age in 0..17).D
val ModeChoiceAlternative.isAgeIn18To29: Double get() = (age in 18..29).D
val ModeChoiceAlternative.isAgeIn50To59: Double get() = (age in 50..59).D
val ModeChoiceAlternative.isAgeIn60To69: Double get() = (age in 60..69).D
val ModeChoiceAlternative.isAgeIn70Plus: Double get() = (age in 70..100).D
val ModeChoiceAlternative.isEcoStatusHigh: Double get() = (ecoStatus in 4..5).D
val ModeChoiceAlternative.travelTime: Duration get() = impedance.duration(origin, destination, choice, time)
val ModeChoiceAlternative.travelCost: Currency get() = impedance.cost(origin, destination, choice, time)

@Suppress("MagicNumber")
val legacyModeChoice = DiscreteStructure<Mode, ModeChoiceAlternative, ModeChoiceParameters> {
    option(LegacyMode.PEDESTRIAN) {
        asc_ped +
            age_0_17_on_asc_ped * it.isAgeIn0To17 +
            age_18_29_on_asc_ped * it.isAgeIn18To29 +
            age_50_59_on_asc_ped * it.isAgeIn50To59 +
            age_60_69_on_asc_ped * it.isAgeIn60To69 +
            age_70_100_on_asc_ped * it.isAgeIn70Plus +
            beruft_on_asc_ped * it.isEmployed +
            female_on_asc_ped * it.isFemale +
            inc_high_on_asc_ped * it.isEcoStatusHigh +
            zk_on_asc_ped * it.hasCommuterTicket +
            b_tt_ped * (it.travelTime.inWholeMinutes)
    }

    option(LegacyMode.BIKE) {
        asc_bike +
            age_0_17_on_asc_bike * it.isAgeIn0To17 +
            age_18_29_on_asc_bike * it.isAgeIn18To29 +
            age_50_59_on_asc_bike * it.isAgeIn50To59 +
            age_60_69_on_asc_bike * it.isAgeIn60To69 +
            age_70_100_on_asc_bike * it.isAgeIn70Plus +
            beruft_on_asc_bike * it.isEmployed +
            female_on_asc_bike * it.isFemale +
            inc_high_on_asc_bike * it.isEcoStatusHigh +
            zk_on_asc_bike * it.hasCommuterTicket +
            b_tt_bike * (it.travelTime.inWholeMinutes)
    }

    option(LegacyMode.CAR) {
        asc_car_d +
            age_0_17_on_asc_car_d * it.isAgeIn0To17 +
            age_18_29_on_asc_car_d * it.isAgeIn18To29 +
            age_50_59_on_asc_car_d * it.isAgeIn50To59 +
            age_60_69_on_asc_car_d * it.isAgeIn60To69 +
            age_70_100_on_asc_car_d * it.isAgeIn70Plus +
            beruft_on_asc_car_d * it.isEmployed +
            female_on_asc_car_d * it.isFemale +
            inc_high_on_asc_car_d * it.isEcoStatusHigh +
            zk_on_asc_car_d * it.hasCommuterTicket +
            b_tt_car_d * (it.travelTime.inWholeMinutes) +
            (
                b_cost_car_d +
                    age_0_17_on_b_cost_car_d * it.isAgeIn0To17 +
                    age_18_29_on_b_cost_car_d * it.isAgeIn18To29 +
                    age_50_59_on_b_cost_car_d * it.isAgeIn50To59 +
                    age_60_69_on_b_cost_car_d * it.isAgeIn60To69 +
                    age_70_100_on_b_cost_car_d * it.isAgeIn70Plus +
                    beruft_on_b_cost_car_d * it.isEmployed +
                    female_on_b_cost_car_d * it.isFemale +
                    inc_high_on_b_cost_car_d * it.isEcoStatusHigh +
                    zk_on_b_cost_car_d * it.hasCommuterTicket
                ) * it.travelCost.euros
    }

    option(LegacyMode.PASSENGER) {
        asc_car_p +
            age_0_17_on_asc_car_p * it.isAgeIn0To17 +
            age_18_29_on_asc_car_p * it.isAgeIn18To29 +
            age_50_59_on_asc_car_p * it.isAgeIn50To59 +
            age_60_69_on_asc_car_p * it.isAgeIn60To69 +
            age_70_100_on_asc_car_p * it.isAgeIn70Plus +
            beruft_on_asc_car_p * it.isEmployed +
            female_on_asc_car_p * it.isFemale +
            inc_high_on_asc_car_p * it.isEcoStatusHigh +
            zk_on_asc_car_p * it.hasCommuterTicket +
            b_tt_car_p * (it.travelTime.inWholeMinutes)
    }

    option(LegacyMode.PUBLICTRANSPORT) {
        asc_put +
            age_0_17_on_asc_put * it.isAgeIn0To17 +
            age_18_29_on_asc_put * it.isAgeIn18To29 +
            age_50_59_on_asc_put * it.isAgeIn50To59 +
            age_60_69_on_asc_put * it.isAgeIn60To69 +
            age_70_100_on_asc_put * it.isAgeIn70Plus +
            beruft_on_asc_put * it.isEmployed +
            female_on_asc_put * it.isFemale +
            inc_high_on_asc_put * it.isEcoStatusHigh +
            zk_on_asc_put * it.hasCommuterTicket +
            b_tt_put * (it.travelTime.inWholeMinutes) +
            // b_acc_put
            // b_u_put
            (
                b_cost_put +
                    age_0_17_on_b_cost_put * it.isAgeIn0To17 +
                    age_18_29_on_b_cost_put * it.isAgeIn18To29 +
                    age_50_59_on_b_cost_put * it.isAgeIn50To59 +
                    age_60_69_on_b_cost_put * it.isAgeIn60To69 +
                    age_70_100_on_b_cost_put * it.isAgeIn70Plus +
                    beruft_on_b_cost_put * it.isEmployed +
                    female_on_b_cost_put * it.isFemale +
                    inc_high_on_b_cost_put * it.isEcoStatusHigh +
                    zk_on_b_cost_put * it.hasCommuterTicket
                ) * it.travelCost.euros * (1 - it.hasCommuterTicket) // NO PUT COST IF OWNS TICKET
    }
}.multinomialLogit(
    name = "LegacyModeChoiceModel"
).build(
    parameters = ModeChoiceParameters()
).fixed(
    setOf(LegacyMode.PEDESTRIAN, LegacyMode.CAR, LegacyMode.BIKE, LegacyMode.PASSENGER, LegacyMode.PUBLICTRANSPORT)
)
