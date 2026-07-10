@file:Suppress("MagicNumber", "ConstructorParameterNaming", "MatchingDeclarationName")

package edu.kit.ifv.domain.simulation.behavior

import edu.kit.ifv.application.steps.HasImpedance
import edu.kit.ifv.core.optionOf
import edu.kit.ifv.domain.shared.enums.LegacyMode
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.enums.person.Employment
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.mobitopp.actitoppNG.utils.D
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.EnumeratedDiscreteModelBuilder
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

context(c: ModeChoiceCharacteristics)
val age: Int get() = c.person.age

context(c: ModeChoiceCharacteristics)
val isEmployed: Double get() = c.run { (person.employment in workingEmploymentTypes).D }

context(c: ModeChoiceCharacteristics)
val isFemale: Double get() = c.run { (person.sex == Sex.FEMALE).D }

context(c: ModeChoiceCharacteristics)
val ecoStatus: Int get() = c.run { person.household.economicStatus.code }

context(c: ModeChoiceCharacteristics)
val hasCommuterTicket: Double get() = c.run { person.hasCommuterTicket.D }

context(c: ModeChoiceCharacteristics)
val isAgeIn0To17: Double get() = (age in 0..17).D

context(c: ModeChoiceCharacteristics)
val isAgeIn18To29: Double get() = (age in 18..29).D

context(c: ModeChoiceCharacteristics)
val isAgeIn50To59: Double get() = (age in 50..59).D

context(c: ModeChoiceCharacteristics)
val isAgeIn60To69: Double get() = (age in 60..69).D

context(c: ModeChoiceCharacteristics)
val isAgeIn70Plus: Double get() = (age in 70..100).D

context(c: ModeChoiceCharacteristics)
val isEcoStatusHigh: Double get() = (ecoStatus in 4..5).D

context(c: ModeChoiceCharacteristics, mode: Mode)
val Impedance.travelTime: Duration get() = duration(
    c.origin,
    c.destination,
    mode,
    c.time,
)

context(c: ModeChoiceCharacteristics, mode: Mode)
val Impedance.travelCost: Currency get() = cost(
    c.origin,
    c.destination,
    mode,
    c.time,
)

@Suppress("MagicNumber")
val <C> C.legacyModeChoiceBuilder: EnumeratedDiscreteModelBuilder<Mode, ModeChoiceCharacteristics, ModeChoiceParameters>
    where C : HasImpedance
    get() = createLegacyModeChoiceBuilder(impedance)

@Suppress("LongMethod")
fun createLegacyModeChoiceBuilder(
    impedance: Impedance,
): EnumeratedDiscreteModelBuilder<Mode, ModeChoiceCharacteristics, ModeChoiceParameters> =
    DiscreteStructure<Mode, ModeChoiceCharacteristics, ModeChoiceParameters> {
        optionOf(LegacyMode.PEDESTRIAN) {
            asc_ped +
                age_0_17_on_asc_ped * isAgeIn0To17 +
                age_18_29_on_asc_ped * isAgeIn18To29 +
                age_50_59_on_asc_ped * isAgeIn50To59 +
                age_60_69_on_asc_ped * isAgeIn60To69 +
                age_70_100_on_asc_ped * isAgeIn70Plus +
                beruft_on_asc_ped * isEmployed +
                female_on_asc_ped * isFemale +
                inc_high_on_asc_ped * isEcoStatusHigh +
                zk_on_asc_ped * hasCommuterTicket +
                b_tt_ped * (impedance.travelTime.inWholeMinutes)
        }

        optionOf(LegacyMode.BIKE) {
            asc_bike +
                age_0_17_on_asc_bike * isAgeIn0To17 +
                age_18_29_on_asc_bike * isAgeIn18To29 +
                age_50_59_on_asc_bike * isAgeIn50To59 +
                age_60_69_on_asc_bike * isAgeIn60To69 +
                age_70_100_on_asc_bike * isAgeIn70Plus +
                beruft_on_asc_bike * isEmployed +
                female_on_asc_bike * isFemale +
                inc_high_on_asc_bike * isEcoStatusHigh +
                zk_on_asc_bike * hasCommuterTicket +
                b_tt_bike * (impedance.travelTime.inWholeMinutes)
        }

        optionOf(LegacyMode.CAR) {
            asc_car_d +
                age_0_17_on_asc_car_d * isAgeIn0To17 +
                age_18_29_on_asc_car_d * isAgeIn18To29 +
                age_50_59_on_asc_car_d * isAgeIn50To59 +
                age_60_69_on_asc_car_d * isAgeIn60To69 +
                age_70_100_on_asc_car_d * isAgeIn70Plus +
                beruft_on_asc_car_d * isEmployed +
                female_on_asc_car_d * isFemale +
                inc_high_on_asc_car_d * isEcoStatusHigh +
                zk_on_asc_car_d * hasCommuterTicket +
                b_tt_car_d * (impedance.travelTime.inWholeMinutes) +
                (
                    b_cost_car_d +
                        age_0_17_on_b_cost_car_d * isAgeIn0To17 +
                        age_18_29_on_b_cost_car_d * isAgeIn18To29 +
                        age_50_59_on_b_cost_car_d * isAgeIn50To59 +
                        age_60_69_on_b_cost_car_d * isAgeIn60To69 +
                        age_70_100_on_b_cost_car_d * isAgeIn70Plus +
                        beruft_on_b_cost_car_d * isEmployed +
                        female_on_b_cost_car_d * isFemale +
                        inc_high_on_b_cost_car_d * isEcoStatusHigh +
                        zk_on_b_cost_car_d * hasCommuterTicket
                    ) * impedance.travelCost.euros
        }

        optionOf(LegacyMode.PASSENGER) {
            asc_car_p +
                age_0_17_on_asc_car_p * isAgeIn0To17 +
                age_18_29_on_asc_car_p * isAgeIn18To29 +
                age_50_59_on_asc_car_p * isAgeIn50To59 +
                age_60_69_on_asc_car_p * isAgeIn60To69 +
                age_70_100_on_asc_car_p * isAgeIn70Plus +
                beruft_on_asc_car_p * isEmployed +
                female_on_asc_car_p * isFemale +
                inc_high_on_asc_car_p * isEcoStatusHigh +
                zk_on_asc_car_p * hasCommuterTicket +
                b_tt_car_p * (impedance.travelTime.inWholeMinutes)
        }

        optionOf(LegacyMode.PUBLICTRANSPORT) {
            asc_put +
                age_0_17_on_asc_put * isAgeIn0To17 +
                age_18_29_on_asc_put * isAgeIn18To29 +
                age_50_59_on_asc_put * isAgeIn50To59 +
                age_60_69_on_asc_put * isAgeIn60To69 +
                age_70_100_on_asc_put * isAgeIn70Plus +
                beruft_on_asc_put * isEmployed +
                female_on_asc_put * isFemale +
                inc_high_on_asc_put * isEcoStatusHigh +
                zk_on_asc_put * hasCommuterTicket +
                b_tt_put * (impedance.travelTime.inWholeMinutes) +
                // b_acc_put
                // b_u_put
                (
                    b_cost_put +
                        age_0_17_on_b_cost_put * isAgeIn0To17 +
                        age_18_29_on_b_cost_put * isAgeIn18To29 +
                        age_50_59_on_b_cost_put * isAgeIn50To59 +
                        age_60_69_on_b_cost_put * isAgeIn60To69 +
                        age_70_100_on_b_cost_put * isAgeIn70Plus +
                        beruft_on_b_cost_put * isEmployed +
                        female_on_b_cost_put * isFemale +
                        inc_high_on_b_cost_put * isEcoStatusHigh +
                        zk_on_b_cost_put * hasCommuterTicket
                    ) * impedance.travelCost.euros * (1 - hasCommuterTicket)
            // NO PUT COST IF OWNS TICKET
        }
    }.multinomialLogit(
        name = "LegacyModeChoiceModel",
    )

val <C> C.legacyModeChoice: FixedChoiceModel<Mode, ModeChoiceCharacteristics> where C : HasImpedance
    get() = legacyModeChoiceBuilder.build(
        parameters = ModeChoiceParameters(),
    )

fun createLegacyModeChoice(impedance: Impedance): FixedChoiceModel<Mode, ModeChoiceCharacteristics> =
    createLegacyModeChoiceBuilder(impedance).build(
        parameters = ModeChoiceParameters(),
    )
