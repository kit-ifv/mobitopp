@file:Suppress("MagicNumber", "ConstructorParameterNaming", "MatchingDeclarationName")

package edu.kit.ifv.domain.simulation.behavior.destinationchoice
import edu.kit.ifv.application.steps.HasAttractivenessModel
import edu.kit.ifv.application.steps.HasImpedance
import edu.kit.ifv.application.steps.HasModeAvailabilityModel
import edu.kit.ifv.core.ruleForAllOf
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.LegacyMode
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.enums.person.Employment
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.zone.attributes.HasZoneId
import edu.kit.ifv.domain.simulation.behavior.availability.ModeAvailabilityModel
import edu.kit.ifv.domain.simulation.behavior.modechoice.euros
import edu.kit.ifv.domain.simulation.data.person.isAdult
import edu.kit.ifv.mobitopp.actitoppNG.utils.D
import edu.kit.ifv.mobitopp.discretechoice.structure.RuleBasedStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.openMultinomialLogit
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit

@Suppress("MagicNumber")
data class DestinationChoiceParameters(
    val asc_car_d: Double = 12.1687,
    val asc_car_p: Double = 11.0360,
    val asc_put: Double = 56.3941 - 46.1231,
    val asc_ped: Double = 12.9224,
    val asc_bike: Double = 11.2774,
    val asc_cs_sb: Double = -1.9785,
    val asc_cs_ff: Double = -0.1539,
    val asc_taxi: Double = 5.7729,
    val asc_bs: Double = 0.0000,
    val asc_rp: Double = 9.7748,
    val b_tt_car_d: Double = -0.0688 - 0.05,
    val b_tt_car_p: Double = -0.0981 - 0.05,
    val b_tt_put: Double = -0.0306 - 0.05 - 0.01,
    val b_tt_ped: Double = -0.1149 - 0.05,
    val b_tt_bike: Double = -0.0983 - 0.03,
    val b_tt_cs: Double = -0.0005 - 0.05,
    val b_tt_taxi: Double = -0.0785,
    val b_tt_rp: Double = -0.0450,
    val b_cost: Double = -0.1283,
    val b_logsum_acc_put: Double = -46.1231,
    val elasticity_acc_put: Double = -0.0109,
    val b_logsum_acc_cs: Double = 2.1892,
    val b_tt_acc_put: Double = -0.1,

    val b_logsum_pt: Double = 0.0,
    val b_logsum_drive: Double = 0.101571780926651 - 0.05,
    val b_logsum_pt_fix: Double = 0.0223919993684166,
    val b_logsum_drive_fix: Double = 0.321408622664327,
    val b_attr: Double = 0.164790334596064 - 0.1,
    val b_0_1: Double = 4.01726106476279,
    val b_1_2: Double = -1.2807770160566 + 0.4,
    val b_intrazonal: Double = 0.0,

    val shift_b_0_1_on_attr: Double = -0.391440604191497,
    val shift_b_1_2_on_attr: Double = 0.165687668821311,

    val shift_intrazonal_on_attr: Double = 0.0,
    val shift_purp_on_logsum_pt: Double = 0.0,
    val shift_purp_on_logsum_pt_fix: Double = 0.0,
    val shift_purp_on_logsum_drive: Double = 0.0,
    val shift_purp_on_logsum_drive_fix: Double = 0.0,
    val shift_purp_on_logsum_attr: Double = 0.0,

    val shift_age_1_on_logsum_pt: Double = 0.0,
    val shift_age_1_on_logsum_drive: Double = 0.0,
    val shift_age_1_on_logsum_pt_fix: Double = 0.0,
    val shift_age_1_on_logsum_drive_fix: Double = 0.0,
    val shift_age_1_on_logsum_attr: Double = 0.0,
    val shift_age_56_on_logsum_pt: Double = 0.0,
    val shift_age_56_on_logsum_drive: Double = 0.0,
    val shift_age_56_on_logsum_pt_fix: Double = 0.0,
    val shift_age_56_on_logsum_drive_fix: Double = 0.0,
    val shift_age_56_on_logsum_attr: Double = 0.0,
    val shift_age_78_on_logsum_pt: Double = 0.0,
    val shift_age_78_on_logsum_drive: Double = 0.0,
    val shift_age_78_on_logsum_pt_fix: Double = 0.0,
    val shift_age_78_on_logsum_drive_fix: Double = 0.0,
    val shift_age_78_on_logsum_attr: Double = 0 + 0.1,

    val shift_educ_on_logsum_pt: Double = 0.0,
    val shift_educ_on_logsum_drive: Double = 0.0,
    val shift_educ_on_logsum_pt_fix: Double = 0.0,
    val shift_educ_on_logsum_drive_fix: Double = 0.0,
    val shift_educ_on_logsum_attr: Double = 0 - 0.05,
    val shift_home_on_logsum_pt: Double = 0.0,
    val shift_home_on_logsum_drive: Double = 0.0,
    val shift_home_on_logsum_pt_fix: Double = 0.0,
    val shift_home_on_logsum_drive_fix: Double = 0.0,
    val shift_home_on_logsum_attr: Double = 0.0,
    val shift_high_inc_on_logsum_pt: Double = 0.0,
    val shift_high_inc_on_logsum_drive: Double = 0.0,
    val shift_high_inc_on_logsum_pt_fix: Double = 0.0,
    val shift_high_inc_on_logsum_drive_fix: Double = 0.0,
    val shift_high_inc_on_logsum_attr: Double = 0.0,
    val shift_zk_on_logsum_pt: Double = 0.0,
    val shift_zk_on_logsum_drive: Double = 0.0,
    val shift_zk_on_logsum_pt_fix: Double = 0.0,
    val shift_zk_on_logsum_drive_fix: Double = 0.0,
    val shift_zk_on_logsum_attr: Double = 0.0,
    val shift_carav_on_logsum_pt: Double = 0.0,
    val shift_carav_on_logsum_drive: Double = 0.0,
    val shift_carav_on_logsum_pt_fix: Double = 0.0,
    val shift_carav_on_logsum_drive_fix: Double = 0.0,
    val shift_carav_on_logsum_attr: Double = 0.1,
    val shift_nocar_on_logsum_pt: Double = 0.0,
    val shift_nocar_on_logsum_drive: Double = 0.0,
    val shift_nocar_on_logsum_pt_fix: Double = 0.0,
    val shift_nocar_on_logsum_drive_fix: Double = 0.0,

    val shift_uml_on_logsum_pt: Double = 0.0640383675977011 + 0.1,
    val shift_uml_on_logsum_drive: Double = 0.0462160663655077 + 0.1,
    val shift_uml_on_logsum_pt_fix: Double = 0.0416507340946399 + 0.1,
    val shift_uml_on_logsum_drive_fix: Double = -0.0755006569431623 + 0.1,
    val shift_uml_on_logsum_attr: Double = 0.610291409419243 - 0.2,

    val max_attractivity: Double = 1000000.0,

    val shift_b_0_1_on_logsum_attr: Double = 0.0,
    val shift_b_1_2_on_logsum_attr: Double = 0.0,
    val b_logsum_pt_active: Double = 0.37913097643898 - 0.1,
    val shift_arb_on_logsum_drive: Double = 0.0,
)

context(c: DestinationChoiceCharacteristics)
val age: Int get() = c.person.age

context(c: DestinationChoiceCharacteristics)
val employment: Employment get() = c.person.employment

context(c: DestinationChoiceCharacteristics)
val hasCommuterTicket: Double get() = c.person.hasCommuterTicket.D

// Household properties
context(c: DestinationChoiceCharacteristics)
val householdEconomicalStatus: Int get() = c.person.household.economicStatus.code

context(c: DestinationChoiceCharacteristics)
val adultsInHousehold: Int get() = c.person.household.members.count { it.isAdult }

context(c: DestinationChoiceCharacteristics)
val householdCars: Int get() = c.person.household.cars.size

context(c: DestinationChoiceCharacteristics)
val carsPerAdult: Double get() = c.run { householdCars / max(adultsInHousehold, 1).toDouble() }

// Trip properties
context(c: DestinationChoiceCharacteristics)
val purpose: ActivityType
    get() = c.person.schedule.activities().first().type

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val AttractivenessModel.attractivity: Double
    get() =
        attractivenessFor(
            choice.zoneId,
            purpose,
        ).value

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.distance: Distance get() = distance(c.origin, choice, LegacyMode.CAR)

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val isIntrazonal: Double get() = (c.origin.zoneId == choice.zoneId).D

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelTimePed: Duration
    get() = duration(c.origin, choice, LegacyMode.PEDESTRIAN, c.time)

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelTimeBike: Duration get() = duration(c.origin, choice, LegacyMode.BIKE, c.time)

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelTimePut: Duration
    get() = duration(c.origin, choice, LegacyMode.PUBLICTRANSPORT, c.time)

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelTimeCar: Duration get() = duration(c.origin, choice, LegacyMode.CAR, c.time)

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelCostPut: Currency
    get() = cost(c.origin, choice, LegacyMode.PUBLICTRANSPORT, c.time)

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelCostCar: Currency get() = cost(c.origin, choice, LegacyMode.CAR, c.time)

// Next fixed destination properties
context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val nextFixedActivity get() = c.person.schedule.activities().find { it.location != StandardLocation.LOCATIONUNKNOWN }

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val nextFixedDestination: HasZoneId get() = nextFixedActivity?.location ?: c.person.household.location

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val nextFixedActivityEnd get() = nextFixedActivity?.endTime ?: c.time.plus(7.hours)

// TODO get next fixed!
context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelTimeFixedPed: Duration
    get() = duration(
        choice,
        nextFixedDestination,
        LegacyMode.PEDESTRIAN,
        nextFixedActivityEnd,
    )

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelTimeFixedBike: Duration
    get() = duration(
        choice,
        nextFixedDestination,
        LegacyMode.BIKE,
        nextFixedActivityEnd,
    )

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelTimeFixedPut: Duration
    get() = duration(
        choice,
        nextFixedDestination,
        LegacyMode.PUBLICTRANSPORT,
        nextFixedActivityEnd,
    )

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelTimeFixedCar: Duration
    get() = duration(
        choice,
        nextFixedDestination,
        LegacyMode.CAR,
        nextFixedActivityEnd,
    )

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelCostFixedPut: Currency
    get() = cost(
        choice,
        nextFixedDestination,
        LegacyMode.PUBLICTRANSPORT,
        nextFixedActivityEnd,
    )

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelCostFixedBikesharing: Currency
    get() = cost(
        choice,
        nextFixedDestination,
        LegacyMode.BIKESHARING,
        nextFixedActivityEnd,
    )

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val Impedance.travelCostFixedCar: Currency
    get() = cost(
        choice,
        nextFixedDestination,
        LegacyMode.CAR,
        nextFixedActivityEnd,
    )

// availability properties
context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
fun ModeAvailabilityModel.isAvailable(mode: Mode) = context(c.with(choice)) {
    asProviderAvailabilityFilter().filter(mode)
}

context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val ModeAvailabilityModel.isPedAvailable get() = isAvailable(LegacyMode.PEDESTRIAN)
context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val ModeAvailabilityModel.isBikeAvailable get() = isAvailable(LegacyMode.BIKE)
context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val ModeAvailabilityModel.isCarAvailable get() = isAvailable(LegacyMode.CAR)
context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val ModeAvailabilityModel.isPassengerAvailable get() = isAvailable(LegacyMode.PASSENGER)
context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val ModeAvailabilityModel.isPutAvailable get() = isAvailable(LegacyMode.PUBLICTRANSPORT)
context(c: DestinationChoiceCharacteristics, choice: StandardLocation)
val ModeAvailabilityModel.isBikesharingAvailable get() = isAvailable(LegacyMode.BIKESHARING)

val studentTypes = listOf(
    Employment.STUDENT,
    Employment.STUDENT_PRIMARY,
    Employment.STUDENT_SECONDARY,
    Employment.STUDENT_TERTIARY,
    Employment.EDUCATION,
)
val noWorkTypes = listOf(Employment.HOMEKEEPER, Employment.UNEMPLOYED)
val Distance.kilometers get() = this.toDouble(DistanceUnit.KILOMETERS)
val Duration.minutes get() = this.toDouble(DurationUnit.MINUTES)
inline operator fun Number.times(bool: Boolean) = this.toDouble() * bool.D
inline operator fun Boolean.times(number: Number) = this.D * number.toDouble()
inline operator fun Number.plus(bool: Boolean) = this.toDouble() + bool.D
inline operator fun Boolean.plus(number: Number) = this.D + number.toDouble()

@Deprecated("Dont use")
@Suppress("MagicNumber")
val <C> C.legacyDestinationChoiceBuilder
    where C : HasImpedance, C : HasAttractivenessModel, C : HasModeAvailabilityModel
    get() = createLegacyDestinationChoiceBuilder(impedance, attractiveness, modeAvailability)

@Suppress("LongMethod", "FunctionNameMaxLength")
fun createLegacyDestinationChoiceBuilder(
    impedance: Impedance,
    attractivenessModel: AttractivenessModel,
    availabilityModel: ModeAvailabilityModel,
) = RuleBasedStructure<StandardLocation, DestinationChoiceCharacteristics, DestinationChoiceParameters> {
    ruleForAllOf {
        (
            ( // b_attr
                b_attr +
                    shift_age_1_on_logsum_attr * (age in 0..17).D +
                    shift_age_56_on_logsum_attr * (age in 50..69).D +
                    shift_age_78_on_logsum_attr * (age in 70..120).D +
                    shift_educ_on_logsum_attr * (employment in studentTypes).D +
                    shift_home_on_logsum_attr * (employment in noWorkTypes).D +
                    shift_zk_on_logsum_attr * hasCommuterTicket +
                    shift_carav_on_logsum_attr * (carsPerAdult >= 1).D +
                    shift_high_inc_on_logsum_attr * (householdEconomicalStatus in 4..5).D +
                    shift_b_0_1_on_logsum_attr * (impedance.distance.kilometers in 0.0..1.0).D +
                    shift_b_1_2_on_logsum_attr *
                    (1.0 < impedance.distance.kilometers && impedance.distance.kilometers <= 2.0).D +
                    shift_intrazonal_on_attr * isIntrazonal
                )
            ) * ln(min(max_attractivity, attractivenessModel.attractivity)) +

            b_0_1 * (impedance.distance.kilometers in 0.0..1.0).D +
            b_1_2 * (1.0 < impedance.distance.kilometers && impedance.distance.kilometers <= 2.0).D +
            b_intrazonal * isIntrazonal +

            (
                ( // b_logsum_pt
                    b_logsum_pt_active +
                        shift_purp_on_logsum_pt +
                        shift_age_1_on_logsum_pt * (age in 0..17) +
                        shift_age_56_on_logsum_pt * (age in 50..69) +
                        shift_age_78_on_logsum_pt * (age in 70..120) +
                        shift_educ_on_logsum_pt * (employment in studentTypes).D +
                        shift_home_on_logsum_pt * (employment in noWorkTypes).D +
                        shift_zk_on_logsum_pt * hasCommuterTicket +
                        shift_carav_on_logsum_pt * (carsPerAdult >= 1).D +
                        shift_nocar_on_logsum_pt * (householdCars == 0).D +
                        shift_high_inc_on_logsum_pt * (householdEconomicalStatus in 4..5).D
                    ) * ( // LOGSUM_DEST_PT
                    ln(
                        exp( // utility_put
                            asc_put +
                                b_tt_put * min(999.0, impedance.travelTimePut.minutes) +
                                b_cost * min(999.0, impedance.travelCostPut.euros),
                            // + b_tt_acc_put * ( 1000 >
                            // ACCESS_TIME_PUBLICTRANSPORT & 1000 > EGRESS_TIME_PUBLICTRANSPORT ) ?
                            // ( ACCESS_TIME_PUBLICTRANSPORT + EGRESS_TIME_PUBLICTRANSPORT ) : ( 999 )
                            // + b_logsum_acc_put/2 * exp(elasticity_acc_put*(LOGSUM_ACC_PUT > (-50))
                            // ?(LOGSUM_ACC_PUT):(-50))
                            // + b_logsum_acc_put/2 * exp(elasticity_acc_put*(LOGSUM_EGR_PUT > (-50))
                            // ?(LOGSUM_EGR_PUT):(-50))
                        ) +
                            exp( // utility_ped
                                asc_ped +
                                    b_tt_ped * min(999.0, impedance.travelTimePed.minutes),
                            ) +
                            exp( // utility_bike
                                asc_bike +
                                    b_tt_bike * min(999.0, impedance.travelTimeBike.minutes),
                            ),
                    ) +
                        (
                            !availabilityModel.isPedAvailable && !availabilityModel.isBikeAvailable &&
                                !availabilityModel.isPutAvailable
                            ).D *
                        (-50)
                    )
                ) +

            (
                ( // b_logsum_drive
                    b_logsum_drive +
                        shift_age_1_on_logsum_drive * (age in 0..17).D +
                        shift_age_56_on_logsum_drive * (age in 50..69).D +
                        shift_age_78_on_logsum_drive * (age in 70..120).D +
                        shift_educ_on_logsum_drive * (employment in studentTypes).D +
                        shift_arb_on_logsum_drive * (employment in noWorkTypes).D +
                        shift_zk_on_logsum_drive * hasCommuterTicket +
                        shift_carav_on_logsum_drive * (carsPerAdult >= 1).D +
                        shift_nocar_on_logsum_drive * (householdCars == 0).D +
                        shift_high_inc_on_logsum_drive * (householdEconomicalStatus in 4..5).D

                    ) * ( // LOGSUM_DEST_DRIVE
                    ln(
                        availabilityModel.isCarAvailable.D * exp( // utility_car_d
                            asc_car_d +
                                b_tt_car_d * min(999.0, impedance.travelTimeCar.minutes) +
                                b_cost * min(999.0, impedance.travelCostCar.euros),
                        ) +
                            exp( // utility_car_p
                                asc_car_p +
                                    b_tt_car_p * min(999.0, impedance.travelTimeCar.minutes + 3),
                            ),
                    ) + (!availabilityModel.isCarAvailable && !availabilityModel.isPassengerAvailable) * (-50)
                    )

                ) +

            (
                ( // b_logsum_pt_fix
                    b_logsum_pt_active +
                        shift_purp_on_logsum_pt_fix +
                        shift_age_1_on_logsum_pt_fix * (age in 0..17).D +
                        shift_age_56_on_logsum_pt_fix * (age in 50..69).D +
                        shift_age_78_on_logsum_pt_fix * (age in 70..120).D +
                        shift_educ_on_logsum_pt_fix * (employment in studentTypes).D +
                        shift_home_on_logsum_pt_fix * (employment in noWorkTypes).D +
                        shift_zk_on_logsum_pt_fix * hasCommuterTicket +
                        shift_carav_on_logsum_pt_fix * (carsPerAdult >= 1).D +
                        shift_nocar_on_logsum_pt_fix * (householdCars == 0).D +
                        shift_high_inc_on_logsum_pt_fix * (householdEconomicalStatus in 4..5).D
                    ) * ( // LOGSUM_FIX_DEST_PT
                    ln(
                        exp( // utility_put
                            asc_put +
                                b_tt_put * min(999.0, impedance.travelTimeFixedPut.minutes) +
                                b_cost * min(999.0, impedance.travelCostFixedPut.euros),
                            // + b_logsum_acc_put/2 * exp(elasticity_acc_put*(LOGSUM_ACC_PUT_FIX > (-50))?(LOGSUM_ACC_PUT_FIX):(-50))
                            // + b_logsum_acc_put/2 * exp(elasticity_acc_put*(LOGSUM_EGR_PUT_FIX > (-50))?(LOGSUM_EGR_PUT_FIX):(-50))
                        ) +

                            exp( // utility_ped
                                asc_ped +
                                    b_tt_ped * min(999.0, impedance.travelTimeFixedPed.minutes),
                            ) +

                            exp( // utility_bike
                                asc_bike +
                                    b_tt_bike * min(999.0, impedance.travelTimeFixedBike.minutes),
                            ) +

                            availabilityModel.isBikesharingAvailable.D * exp( // utility_bike
                                asc_bs +
                                    b_tt_bike * min(999.0, impedance.travelTimeFixedBike.minutes) +
                                    b_cost * min(999.0, impedance.travelCostFixedBikesharing.euros),
                            ),
                    ) +
                        (
                            !availabilityModel.isPedAvailable && !availabilityModel.isBikeAvailable &&
                                !availabilityModel.isPutAvailable
                            ).D *
                        (-50)

                    )
                ) +

            (
                ( // b_logsum_drive_fix
                    b_logsum_drive_fix +
                        shift_age_1_on_logsum_drive_fix * (age in 0..17).D +
                        shift_age_56_on_logsum_drive_fix * (age in 50..69).D +
                        shift_age_78_on_logsum_drive_fix * (age in 70..120).D +
                        shift_educ_on_logsum_drive_fix * (employment in studentTypes).D +
                        shift_home_on_logsum_drive_fix * (employment in noWorkTypes).D +
                        shift_zk_on_logsum_drive_fix * hasCommuterTicket +
                        shift_carav_on_logsum_drive_fix * (carsPerAdult >= 1).D +
                        shift_nocar_on_logsum_drive_fix * (householdCars == 0).D +
                        shift_high_inc_on_logsum_drive_fix * (householdEconomicalStatus in 4..5).D

                    ) * ( // LOGSUM_FIX_DEST_DRIVE
                    ln(
                        availabilityModel.isCarAvailable * exp( // utility_car_d
                            asc_car_d +
                                b_tt_car_d * min(999.0, impedance.travelTimeFixedCar.minutes) +
                                b_cost * min(999.0, impedance.travelCostFixedCar.euros),
                        ) +

                            exp( // utility_car_p
                                asc_car_p +
                                    b_tt_car_p * min(999.0, impedance.travelTimeFixedCar.minutes + 3),
                            ),
                    ) + (!availabilityModel.isCarAvailable && !availabilityModel.isPassengerAvailable).D * (-50)
                    )
                )
    }
}.openMultinomialLogit("LegacyDestinationChoiceModel")

val <C> C.legacyDestinationChoice where C : HasImpedance, C : HasAttractivenessModel, C : HasModeAvailabilityModel
    get() = legacyDestinationChoiceBuilder.build(
        parameters = DestinationChoiceParameters(),
    )

fun createLegacyDestinationChoice(
    impedance: Impedance,
    attractivenessModel: AttractivenessModel,
    availabilityModel: ModeAvailabilityModel,
) = createLegacyDestinationChoiceBuilder(
    impedance,
    attractivenessModel,
    availabilityModel,
).build(DestinationChoiceParameters())
