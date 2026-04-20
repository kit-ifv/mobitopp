@file: Suppress("MagicNumber", "ConstructorParameterNaming", "MatchingDeclarationName")

package domain.simulation.behavior

import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.Mode
import domain.shared.location.StandardLocation
import domain.shared.location.attributes.HasZoneID
import domain.synthesis.data.Employment
import domain.synthesis.data.isAdult
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

val DestinationChoiceCharacteristics.age: Int get() = person.age
val DestinationChoiceCharacteristics.employment: Employment get() = person.employment
val DestinationChoiceCharacteristics.hasCommuterTicket: Double get() = person.hasCommuterTicket.D

// Household properties
val DestinationChoiceCharacteristics.householdEconomicalStatus: Int get() = person.household.economicStatus.code
val DestinationChoiceCharacteristics.adultsInHousehold: Int get() = person.household.members.count { it.isAdult }
val DestinationChoiceCharacteristics.householdCars: Int get() = person.household.cars.size
val DestinationChoiceCharacteristics.carsPerAdult: Double get() = householdCars / max(adultsInHousehold, 1).toDouble()

// Trip properties
val DestinationChoiceCharacteristics.purpose: ActivityType
    get() = person.schedule.activities().first().type
val DestinationAlternative.attractivity: Double
    get() =
        attractivityModel.attractivenessFor(
            choice.zoneID,
            purpose
        ).value
val DestinationAlternative.distance: Distance get() = impedance.distance(origin, choice, LegacyMode.CAR)
val DestinationAlternative.isIntrazonal: Double get() = (origin.zoneID == choice.zoneID).D

val DestinationAlternative.travelTimePed: Duration
    get() = impedance.duration(
        origin,
        choice,
        LegacyMode.PEDESTRIAN,
        time
    )
val DestinationAlternative.travelTimeBike: Duration get() = impedance.duration(origin, choice, LegacyMode.BIKE, time)
val DestinationAlternative.travelTimePut: Duration
    get() = impedance.duration(
        origin,
        choice,
        LegacyMode.PUBLICTRANSPORT,
        time
    )
val DestinationAlternative.travelTimeCar: Duration get() = impedance.duration(origin, choice, LegacyMode.CAR, time)
val DestinationAlternative.travelCostPut: Currency
    get() = impedance.cost(
        origin,
        choice,
        LegacyMode.PUBLICTRANSPORT,
        time
    )
val DestinationAlternative.travelCostCar: Currency get() = impedance.cost(origin, choice, LegacyMode.CAR, time)

// Next fixed destination properties
val DestinationChoiceCharacteristics.nextFixedActivity
    get() = person.schedule.activities().find { it.location != StandardLocation.LOCATIONUNKNOWN }
val DestinationChoiceCharacteristics.nextFixedDestination: HasZoneID
    get() = nextFixedActivity?.location ?: person.household.location
val DestinationChoiceCharacteristics.nextFixedActivityEnd get() = nextFixedActivity?.endTime ?: time.plus(7.hours)

// TODO get next fixed!
val DestinationAlternative.travelTimeFixedPed: Duration
    get() = impedance.duration(
        choice,
        nextFixedDestination,
        LegacyMode.PEDESTRIAN,
        nextFixedActivityEnd
    )
val DestinationAlternative.travelTimeFixedBike: Duration
    get() = impedance.duration(
        choice,
        nextFixedDestination,
        LegacyMode.BIKE,
        nextFixedActivityEnd
    )
val DestinationAlternative.travelTimeFixedPut: Duration
    get() = impedance.duration(
        choice,
        nextFixedDestination,
        LegacyMode.PUBLICTRANSPORT,
        nextFixedActivityEnd
    )
val DestinationAlternative.travelTimeFixedCar: Duration
    get() = impedance.duration(
        choice,
        nextFixedDestination,
        LegacyMode.CAR,
        nextFixedActivityEnd
    )
val DestinationAlternative.travelCostFixedPut: Currency
    get() = impedance.cost(
        choice,
        nextFixedDestination,
        LegacyMode.PUBLICTRANSPORT,
        nextFixedActivityEnd
    )
val DestinationAlternative.travelCostFixedBikesharing: Currency
    get() = impedance.cost(
        choice,
        nextFixedDestination,
        LegacyMode.BIKESHARING,
        nextFixedActivityEnd
    )
val DestinationAlternative.travelCostFixedCar: Currency
    get() = impedance.cost(
        choice,
        nextFixedDestination,
        LegacyMode.CAR,
        nextFixedActivityEnd
    )

// availability properties
fun DestinationAlternative.isAvailable(mode: Mode) =
    modeAvailabilityFilter.filter(mode)

val DestinationAlternative.isPedAvailable get() = isAvailable(LegacyMode.PEDESTRIAN)
val DestinationAlternative.isBikeAvailable get() = isAvailable(LegacyMode.BIKE)
val DestinationAlternative.isCarAvailable get() = isAvailable(LegacyMode.CAR)
val DestinationAlternative.isPassengerAvailable get() = isAvailable(LegacyMode.PASSENGER)
val DestinationAlternative.isPutAvailable get() = isAvailable(LegacyMode.PUBLICTRANSPORT)
val DestinationAlternative.isBikesharingAvailable get() = isAvailable(LegacyMode.BIKESHARING)

val studentTypes = listOf(
    Employment.STUDENT,
    Employment.STUDENT_PRIMARY,
    Employment.STUDENT_SECONDARY,
    Employment.STUDENT_TERTIARY,
    Employment.EDUCATION
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
val legacyDestinationChoiceBuilder =
    RuleBasedStructure<StandardLocation, DestinationChoiceCharacteristics, DestinationChoiceParameters> {
        ruleForAll { destination, tripchoice ->
            val it = tripchoice.with(destination)

            (
                ( // b_attr
                    b_attr +
                        shift_age_1_on_logsum_attr * (it.age in 0..17).D +
                        shift_age_56_on_logsum_attr * (it.age in 50..69).D +
                        shift_age_78_on_logsum_attr * (it.age in 70..120).D +
                        shift_educ_on_logsum_attr * (it.employment in studentTypes).D +
                        shift_home_on_logsum_attr * (it.employment in noWorkTypes).D +
                        shift_zk_on_logsum_attr * it.hasCommuterTicket +
                        shift_carav_on_logsum_attr * (it.carsPerAdult >= 1).D +
                        shift_high_inc_on_logsum_attr * (it.householdEconomicalStatus in 4..5).D +
                        shift_b_0_1_on_logsum_attr * (it.distance.kilometers in 0.0..1.0).D +
                        shift_b_1_2_on_logsum_attr * (1.0 < it.distance.kilometers && it.distance.kilometers <= 2.0).D +
                        shift_intrazonal_on_attr * it.isIntrazonal
                    )
                ) * ln(min(max_attractivity, it.attractivity)) +

                b_0_1 * (it.distance.kilometers in 0.0..1.0).D +
                b_1_2 * (1.0 < it.distance.kilometers && it.distance.kilometers <= 2.0).D +
                b_intrazonal * it.isIntrazonal +

                (
                    ( // b_logsum_pt
                        b_logsum_pt_active +
                            shift_purp_on_logsum_pt +
                            shift_age_1_on_logsum_pt * (it.age in 0..17) +
                            shift_age_56_on_logsum_pt * (it.age in 50..69) +
                            shift_age_78_on_logsum_pt * (it.age in 70..120) +
                            shift_educ_on_logsum_pt * (it.employment in studentTypes).D +
                            shift_home_on_logsum_pt * (it.employment in noWorkTypes).D +
                            shift_zk_on_logsum_pt * it.hasCommuterTicket +
                            shift_carav_on_logsum_pt * (it.carsPerAdult >= 1).D +
                            shift_nocar_on_logsum_pt * (it.householdCars == 0).D +
                            shift_high_inc_on_logsum_pt * (it.householdEconomicalStatus in 4..5).D
                        ) * ( // LOGSUM_DEST_PT
                        ln(
                            exp( // utility_put
                                asc_put +
                                    b_tt_put * min(999.0, it.travelTimePut.minutes) +
                                    b_cost * min(999.0, it.travelCostPut.euros)
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
                                        b_tt_ped * min(999.0, it.travelTimePed.minutes)
                                ) +
                                exp( // utility_bike
                                    asc_bike +
                                        b_tt_bike * min(999.0, it.travelTimeBike.minutes)
                                )
                        ) + (!it.isPedAvailable && !it.isBikeAvailable && !it.isPutAvailable).D * (-50)
                        )
                    ) +

                (
                    ( // b_logsum_drive
                        b_logsum_drive +
                            shift_age_1_on_logsum_drive * (it.age in 0..17).D +
                            shift_age_56_on_logsum_drive * (it.age in 50..69).D +
                            shift_age_78_on_logsum_drive * (it.age in 70..120).D +
                            shift_educ_on_logsum_drive * (it.employment in studentTypes).D +
                            shift_arb_on_logsum_drive * (it.employment in noWorkTypes).D +
                            shift_zk_on_logsum_drive * it.hasCommuterTicket +
                            shift_carav_on_logsum_drive * (it.carsPerAdult >= 1).D +
                            shift_nocar_on_logsum_drive * (it.householdCars == 0).D +
                            shift_high_inc_on_logsum_drive * (it.householdEconomicalStatus in 4..5).D

                        ) * ( // LOGSUM_DEST_DRIVE
                        ln(
                            it.isCarAvailable.D * exp( // utility_car_d
                                asc_car_d +
                                    b_tt_car_d * min(999.0, it.travelTimeCar.minutes) +
                                    b_cost * min(999.0, it.travelCostCar.euros)
                            ) +
                                exp( // utility_car_p
                                    asc_car_p +
                                        b_tt_car_p * min(999.0, it.travelTimeCar.minutes + 3)
                                )
                        ) + (!it.isCarAvailable && !it.isPassengerAvailable) * (-50)
                        )

                    ) +

                (
                    ( // b_logsum_pt_fix
                        b_logsum_pt_active +
                            shift_purp_on_logsum_pt_fix +
                            shift_age_1_on_logsum_pt_fix * (it.age in 0..17).D +
                            shift_age_56_on_logsum_pt_fix * (it.age in 50..69).D +
                            shift_age_78_on_logsum_pt_fix * (it.age in 70..120).D +
                            shift_educ_on_logsum_pt_fix * (it.employment in studentTypes).D +
                            shift_home_on_logsum_pt_fix * (it.employment in noWorkTypes).D +
                            shift_zk_on_logsum_pt_fix * it.hasCommuterTicket +
                            shift_carav_on_logsum_pt_fix * (it.carsPerAdult >= 1).D +
                            shift_nocar_on_logsum_pt_fix * (it.householdCars == 0).D +
                            shift_high_inc_on_logsum_pt_fix * (it.householdEconomicalStatus in 4..5).D
                        ) * ( // LOGSUM_FIX_DEST_PT
                        ln(
                            exp( // utility_put
                                asc_put +
                                    b_tt_put * min(999.0, it.travelTimeFixedPut.minutes) +
                                    b_cost * min(999.0, it.travelCostFixedPut.euros)
                                // + b_logsum_acc_put/2 * exp(elasticity_acc_put*(LOGSUM_ACC_PUT_FIX > (-50))?(LOGSUM_ACC_PUT_FIX):(-50))
                                // + b_logsum_acc_put/2 * exp(elasticity_acc_put*(LOGSUM_EGR_PUT_FIX > (-50))?(LOGSUM_EGR_PUT_FIX):(-50))
                            ) +

                                exp( // utility_ped
                                    asc_ped +
                                        b_tt_ped * min(999.0, it.travelTimeFixedPed.minutes)
                                ) +

                                exp( // utility_bike
                                    asc_bike +
                                        b_tt_bike * min(999.0, it.travelTimeFixedBike.minutes)
                                ) +

                                it.isBikesharingAvailable.D * exp( // utility_bike
                                    asc_bs +
                                        b_tt_bike * min(999.0, it.travelTimeFixedBike.minutes) +
                                        b_cost * min(999.0, it.travelCostFixedBikesharing.euros)
                                )
                        ) + (!it.isPedAvailable && !it.isBikeAvailable && !it.isPutAvailable).D * (-50)

                        )
                    ) +

                (
                    ( // b_logsum_drive_fix
                        b_logsum_drive_fix +
                            shift_age_1_on_logsum_drive_fix * (it.age in 0..17).D +
                            shift_age_56_on_logsum_drive_fix * (it.age in 50..69).D +
                            shift_age_78_on_logsum_drive_fix * (it.age in 70..120).D +
                            shift_educ_on_logsum_drive_fix * (it.employment in studentTypes).D +
                            shift_home_on_logsum_drive_fix * (it.employment in noWorkTypes).D +
                            shift_zk_on_logsum_drive_fix * it.hasCommuterTicket +
                            shift_carav_on_logsum_drive_fix * (it.carsPerAdult >= 1).D +
                            shift_nocar_on_logsum_drive_fix * (it.householdCars == 0).D +
                            shift_high_inc_on_logsum_drive_fix * (it.householdEconomicalStatus in 4..5).D

                        ) * ( // LOGSUM_FIX_DEST_DRIVE
                        ln(
                            it.isCarAvailable * exp( // utility_car_d
                                asc_car_d +
                                    b_tt_car_d * min(999.0, it.travelTimeFixedCar.minutes) +
                                    b_cost * min(999.0, it.travelCostFixedCar.euros)
                            ) +

                                exp( // utility_car_p
                                    asc_car_p +
                                        b_tt_car_p * min(999.0, it.travelTimeFixedCar.minutes + 3)
                                )
                        ) + (!it.isCarAvailable && !it.isPassengerAvailable).D * (-50)
                        )
                    )
        }
    }.openMultinomialLogit("LegacyDestinationChoiceModel")

val legacyDestinationChoice = legacyDestinationChoiceBuilder.build(
    parameters = DestinationChoiceParameters()
)
