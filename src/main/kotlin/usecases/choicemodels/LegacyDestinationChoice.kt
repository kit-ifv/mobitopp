@file:Suppress(
    "UnusedParameter",
    "MagicNumber",
    "MaxLineLength",
    "VariableNaming",
    "FunctionMaxLength",
    "FunctionNaming",
    "CyclomaticComplexMethod",
    "TooManyFunctions",
    "CognitiveComplexMethod",
    "LargeClass",
    "LongMethod",
    "LongParameterList",
    "FunctionOnlyReturningConstant"
)

package usecases.choicemodels

import datastructure.StationaryAction
import domain.data.Employment
import domain.data.Person
import domain.data.Zone
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.enums.Mode
import domain.location.Location
import domain.location.Metrics
import modeling.discreteChoice.D
import modeling.models.ChoiceModel
import modeling.models.LogitModel
import units.CurrencyUnit
import units.DistanceUnit
import usecases.AttractivenessModel
import usecases.choicemodels.parameters.LegacyBusinessParameters
import usecases.choicemodels.parameters.LegacyLeisureParameters
import usecases.choicemodels.parameters.LegacyServiceParameters
import usecases.choicemodels.parameters.LegacyShoppingParameters
import utils.units.AbsoluteTime
import utils.units.Time
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.time.DurationUnit

interface ILegacyDestinationChoice {
    fun Collection<Location>.selectDestination(
        person: Person,
        prevActivity: StationaryAction,
        nextActivity: StationaryAction,
        modes: Collection<Mode>,
        randomNumber: Double
    ): Location

    fun calculateU_destination(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        availableModes: Collection<Mode>,
        randomNumber: Double
    ): Double
}

class LegacyDestinationChoice(
    impedance: Metrics,
    attractivenessModel: AttractivenessModel,
    umlands: (Location) -> Boolean, // TODO ask Lucas
    zones: Set<Zone>,
    val modes: ChoiceModelModes,
    val filter: ChoiceFilter<Mode, Person> = NoFilter,
) : ChoiceModel<Person, Location>, ILegacyDestinationChoice {

    val car = modes.car
    val bike = modes.bike
    val pedestrian = modes.pedestrian
    val publicTransport = modes.publicTransport
    val passenger = modes.passenger
    val bikeSharing = modes.bikeSharing

    override val name: String = "HamburgLegacyDestinationChoiceModel"

    private val _choices: Set<Location> = zones.map { it.centroid }.toSet()

    override fun choices(agent: Person, time: Time): Set<Location> {
        return _choices
    }

    override fun select(agent: Person, choices: Set<Location>, time: Time): Location {
        // TODO comparision issue, have to use <= instead of <, maybe we need to think about this again
        val prevActivity = agent.schedule.pastActivities().lastOrNull { it <= time }
            ?: throw NoSuchElementException("Activity plan of agent ${agent.id} has no past activities.")
        val nextActivity = agent.schedule.activities().firstOrNull { it > time }
            ?: throw NoSuchElementException("Activity plan of agent ${agent.id} has future planned activity.")
        val modeOptions = filter.filter(modes.options, agent)
        return choices.selectDestination(agent, prevActivity, nextActivity, modeOptions, agent.random.nextDouble())
    }

    /**
     * Taken from DestinationChoiceModelLoader.java Transmove line:55-80
     */
    private fun getParams(type: ActivityType): IDestinationParameters { // TODO independence of legacy activity type
        return when (type) {
            LegacyActivityType.BUSINESS, LegacyActivityType.BUSINESS_TRAVEL, LegacyActivityType.BUSINESS_OUT, LegacyActivityType.BUSINESS_TO_WORK -> LegacyBusinessParameters
            LegacyActivityType.SHOPPING, LegacyActivityType.PRIVATE_BUSINESS, LegacyActivityType.SHOPPING_OTHER, LegacyActivityType.SHOPPING_DAILY -> LegacyShoppingParameters
            LegacyActivityType.SERVICE -> LegacyServiceParameters
            else -> LegacyLeisureParameters
        }
    }

    val helper = LegacyDestinationHelper(impedance, attractivenessModel, umlands, modes = modes)

    override fun Collection<Location>.selectDestination(
        person: Person,
        prevActivity: StationaryAction,
        nextActivity: StationaryAction,
        modes: Collection<Mode>,
        randomNumber: Double
    ): Location {
        val origin = prevActivity.location
        val endTime = prevActivity.endTime

        //             helper.getATTRACTIVITY(category, person, origin, destination, nextActivity, time, randomNumber)
        val zonesWithAttractivity =
            filter { helper.getATTRACTIVITY(it, person, origin, it, nextActivity, endTime, randomNumber) > 0.0 }
        val build: LogitModel<Person, Location> = object : LogitModel<Person, Location>() {
            override fun utility(agent: Person, choice: Location, time: Time): Double {
                return calculateU_destination(choice, agent, origin, choice, nextActivity, time, modes, randomNumber)
            }

            override val name: String = "TEST DESTINATION LOGIT"

            override fun choices(agent: Person, time: Time): Set<Location> {
                return zonesWithAttractivity.toSet()
            }
        }
        return build.select(person, zonesWithAttractivity.toSet(), endTime)
    }

    override fun calculateU_destination(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        availableModes: Collection<Mode>,
        randomNumber: Double
    ): Double {
        val type: ActivityType = nextActivity.type
        val parameterSet = getParams(type)

        val AGE_value: Double = helper.getAGE(category, person, origin, destination, nextActivity, time, randomNumber)

        val IS_EMPLOYMENT_STUDENT_value: Double =
            helper.getIS_EMPLOYMENT_STUDENT(category, person, origin, destination, nextActivity, time, randomNumber)

        val IS_EMPLOYMENT_STUDENT_PRIMARY_value: Double = helper.getIS_EMPLOYMENT_STUDENT_PRIMARY(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val IS_EMPLOYMENT_STUDENT_SECONDARY_value: Double = helper.getIS_EMPLOYMENT_STUDENT_SECONDARY(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val IS_EMPLOYMENT_STUDENT_TERTIARY_value: Double = helper.getIS_EMPLOYMENT_STUDENT_TERTIARY(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val IS_EMPLOYMENT_EDUCATION_value: Double =
            helper.getIS_EMPLOYMENT_EDUCATION(category, person, origin, destination, nextActivity, time, randomNumber)

        val IS_EMPLOYMENT_FULLTIME_value: Double =
            helper.getIS_EMPLOYMENT_FULLTIME(category, person, origin, destination, nextActivity, time, randomNumber)

        val IS_EMPLOYMENT_PARTTIME_value: Double =
            helper.getIS_EMPLOYMENT_PARTTIME(category, person, origin, destination, nextActivity, time, randomNumber)

        val IS_EMPLOYMENT_MARGINAL_value: Double =
            helper.getIS_EMPLOYMENT_MARGINAL(category, person, origin, destination, nextActivity, time, randomNumber)

        val HAS_COMMUTER_TICKET_value: Double =
            helper.getHAS_COMMUTER_TICKET(category, person, origin, destination, nextActivity, time, randomNumber)

        val CARS_PER_ADULT_value: Double =
            helper.getCARS_PER_ADULT(category, person, origin, destination, nextActivity, time, randomNumber)

        val HOUSEHOLD_ECONOMICAL_STATUS_value: Double = helper.getHOUSEHOLD_ECONOMICAL_STATUS(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val IS_UMLAND_value: Double =
            helper.getIS_UMLAND(category, person, origin, destination, nextActivity, time, randomNumber)

        val DISTANCE_value: Double =
            helper.getDISTANCE(category, person, origin, destination, nextActivity, time, randomNumber)

        val ATTRACTIVITY_value: Double =
            helper.getATTRACTIVITY(category, person, origin, destination, nextActivity, time, randomNumber)

        val PARKDRUCK_value: Double =
            helper.getPARKDRUCK(category, person, origin, destination, nextActivity, time, randomNumber)

        val AVAIL_FUSS_value: Double = (pedestrian in availableModes).D

        val AVAIL_RAD_value: Double = (bike in availableModes).D

        val AVAIL_OEV_value: Double = (publicTransport in availableModes).D

        val TRAVEL_TIME_PUBLICTRANSPORT_value: Double = helper.getTRAVEL_TIME_PUBLICTRANSPORT(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val TRAVEL_COST_PUBLICTRANSPORT_value: Double = helper.getTRAVEL_COST_PUBLICTRANSPORT(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val ACCESS_TIME_PUBLICTRANSPORT_value: Double = helper.getACCESS_TIME_PUBLICTRANSPORT(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val EGRESS_TIME_PUBLICTRANSPORT_value: Double = helper.getEGRESS_TIME_PUBLICTRANSPORT(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val IS_ACTIVITY_TYPE_WORK_value: Double =
            helper.getIS_ACTIVITY_TYPE_WORK(category, person, origin, destination, nextActivity, time, randomNumber)

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double =
            helper.getIS_ACTIVITY_TYPE_BUSINESS(category, person, origin, destination, nextActivity, time, randomNumber)

        val ACTIVITY_TYPE_IS_LEISURE_value: Double =
            helper.getACTIVITY_TYPE_IS_LEISURE(category, person, origin, destination, nextActivity, time, randomNumber)

        val IS_ACTIVITY_TYPE_SERVICE_value: Double =
            helper.getIS_ACTIVITY_TYPE_SERVICE(category, person, origin, destination, nextActivity, time, randomNumber)

        val TRAVEL_TIME_PEDESTRIAN_value: Double =
            helper.getTRAVEL_TIME_PEDESTRIAN(category, person, origin, destination, nextActivity, time, randomNumber)

        val TRAVEL_TIME_BIKE_value: Double =
            helper.getTRAVEL_TIME_BIKE(category, person, origin, destination, nextActivity, time, randomNumber)

        val AVAIL_PKW_value: Double = (car in availableModes).D

        val AVAIL_MF_value: Double = (passenger in availableModes).D

        val TRAVEL_TIME_CAR_value: Double =
            helper.getTRAVEL_TIME_CAR(category, person, origin, destination, nextActivity, time, randomNumber)

        val TRAVEL_COST_CAR_value: Double =
            helper.getTRAVEL_COST_CAR(category, person, origin, destination, nextActivity, time, randomNumber)

        val ACCESS_TIME_CAR_value: Double =
            helper.getACCESS_TIME_CAR(category, person, origin, destination, nextActivity, time, randomNumber)

        val EGRESS_TIME_CAR_value: Double =
            helper.getEGRESS_TIME_CAR(category, person, origin, destination, nextActivity, time, randomNumber)

        val ACTIVITY_TYPE_IS_HOME_value: Double =
            helper.getACTIVITY_TYPE_IS_HOME(category, person, origin, destination, nextActivity, time, randomNumber)

        val TRAVEL_TIME_PUBLICTRANSPORT_FIX_value: Double = helper.getTRAVEL_TIME_PUBLICTRANSPORT_FIX(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val TRAVEL_COST_PUBLICTRANSPORT_FIX_value: Double = helper.getTRAVEL_COST_PUBLICTRANSPORT_FIX(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val ACCESS_TIME_PUBLICTRANSPORT_FIX_value: Double = helper.getACCESS_TIME_PUBLICTRANSPORT_FIX(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val EGRESS_TIME_PUBLICTRANSPORT_FIX_value: Double = helper.getEGRESS_TIME_PUBLICTRANSPORT_FIX(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val PARKDRUCK_FIX_value: Double =
            helper.getPARKDRUCK_FIX(category, person, origin, destination, nextActivity, time, randomNumber)

        val TRAVEL_TIME_PEDESTRIAN_FIX_value: Double = helper.getTRAVEL_TIME_PEDESTRIAN_FIX(
            category,
            person,
            origin,
            destination,
            nextActivity,
            time,
            randomNumber
        )

        val TRAVEL_TIME_BIKE_FIX_value: Double =
            helper.getTRAVEL_TIME_BIKE_FIX(category, person, origin, destination, nextActivity, time, randomNumber)

        val TRAVEL_TIME_CAR_FIX_value: Double =
            helper.getTRAVEL_TIME_CAR_FIX(category, person, origin, destination, nextActivity, time, randomNumber)

        val TRAVEL_COST_CAR_FIX_value: Double =
            helper.getTRAVEL_COST_CAR_FIX(category, person, origin, destination, nextActivity, time, randomNumber)

        val ACCESS_TIME_CAR_FIX_value: Double =
            helper.getACCESS_TIME_CAR_FIX(category, person, origin, destination, nextActivity, time, randomNumber)

        val EGRESS_TIME_CAR_FIX_value: Double =
            helper.getEGRESS_TIME_CAR_FIX(category, person, origin, destination, nextActivity, time, randomNumber)
        val output = parameterSet.run {
            val zlogsumAttrFactor =
                b_attr + shift_age_2_on_attr *
                    (if ((18.0 <= AGE_value && AGE_value <= 29.0)) 1.0 else 0.0) +
                    (shift_age_3_on_attr * (if ((30.0 <= AGE_value && AGE_value <= 39.0)) 1.0 else 0.0)) +
                    (shift_age_4_on_attr * (if ((40.0 <= AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) +
                    (shift_age_56_on_attr * (if ((50.0 <= AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) +
                    (shift_age_78_on_attr * (if ((70.0 <= AGE_value && AGE_value <= 120.0)) 1.0 else 0.0)) +
                    (
                        shift_educ_on_attr *
                            (
                                if ((
                                    (
                                        IS_EMPLOYMENT_STUDENT_value +
                                            IS_EMPLOYMENT_STUDENT_PRIMARY_value +
                                            IS_EMPLOYMENT_STUDENT_SECONDARY_value +
                                            IS_EMPLOYMENT_STUDENT_TERTIARY_value +
                                            IS_EMPLOYMENT_EDUCATION_value
                                        ) > 0.0
                                    )
                                ) {
                                    1.0
                                } else {
                                    0.0
                                }
                                )
                        ) +
                    (
                        shift_arb_on_attr * (
                            if ((
                                (
                                    IS_EMPLOYMENT_FULLTIME_value +
                                        IS_EMPLOYMENT_PARTTIME_value +
                                        IS_EMPLOYMENT_MARGINAL_value
                                    ) > 0.0
                                )
                            ) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                        ) +
                    (shift_zk_on_attr * HAS_COMMUTER_TICKET_value) +
                    (shift_carav_on_attr * (if ((CARS_PER_ADULT_value >= 1.0)) 1.0 else 0.0)) +
                    (
                        shift_high_inc_on_attr * (
                            if (
                                (4.0 <= HOUSEHOLD_ECONOMICAL_STATUS_value && HOUSEHOLD_ECONOMICAL_STATUS_value <= 5.0)
                            ) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                        ) +
                    (shift_uml_on_attr * IS_UMLAND_value) +
                    (shift_b_0_1_on_attr * (if ((0.0 < DISTANCE_value && DISTANCE_value <= 1.0)) 1.0 else 0.0)) +
                    (shift_b_1_2_on_attr * (if ((1.0 < DISTANCE_value && DISTANCE_value <= 2.0)) 1.0 else 0.0))
            val zLogsumAttr = ln(
                (if ((ATTRACTIVITY_value > max_attractivity)) (max_attractivity) else (ATTRACTIVITY_value))
            )
            val firstVar =
                zlogsumAttrFactor * zLogsumAttr
            val oevExp = exp(
                (
                    asc_oev + b_tt_oev *
                        (TRAVEL_TIME_PUBLICTRANSPORT_value.coerceAtMost(1000.0)) +
                        (b_cost_oev * (TRAVEL_COST_PUBLICTRANSPORT_value.coerceAtMost(1000.0))) +
                        (
                            b_zuab_oev * (
                                (
                                    if ((
                                        1000.0 > ACCESS_TIME_PUBLICTRANSPORT_value &&
                                            1000.0 > EGRESS_TIME_PUBLICTRANSPORT_value
                                        )
                                    ) {
                                        (ACCESS_TIME_PUBLICTRANSPORT_value + EGRESS_TIME_PUBLICTRANSPORT_value)
                                    } else {
                                        (999.0)
                                    }
                                    )
                                )
                            ) +
                        (b_arb_on_oev * IS_ACTIVITY_TYPE_WORK_value) +
                        (b_dienst_on_oev * IS_ACTIVITY_TYPE_BUSINESS_value) +
                        (b_freizeit_on_oev * ACTIVITY_TYPE_IS_LEISURE_value) +
                        (b_service_on_oev * IS_ACTIVITY_TYPE_SERVICE_value) + (
                            (
                                b_park_oev * (
                                    (if ((50.0 > PARKDRUCK_value)) (PARKDRUCK_value) else (50.0)).pow(
                                        elasticity_park_oev
                                    )
                                    )
                                ) * (if ((999.0 > PARKDRUCK_value)) 1.0 else 0.0)
                            )
                    )
            )
            val zlogsumPTFactor = b_logsum_pt_active +
                shift_age_2_on_logsum_pt_active *
                (if ((18.0 <= AGE_value && AGE_value <= 29.0)) 1.0 else 0.0) +
                (
                    shift_age_3_on_logsum_pt_active *
                        (if ((30.0 <= AGE_value && AGE_value <= 39.0)) 1.0 else 0.0)
                    ) +
                (
                    shift_age_4_on_logsum_pt_active *
                        (if ((40.0 <= AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)
                    ) +
                (shift_age_56_on_logsum_pt_active * (if ((50.0 <= AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) +
                (shift_age_78_on_logsum_pt_active * (if ((70.0 <= AGE_value && AGE_value <= 120.0)) 1.0 else 0.0)) +
                (
                    shift_educ_on_logsum_pt_active *
                        (
                            if ((
                                (
                                    IS_EMPLOYMENT_STUDENT_value +
                                        IS_EMPLOYMENT_STUDENT_PRIMARY_value +
                                        IS_EMPLOYMENT_STUDENT_SECONDARY_value +
                                        IS_EMPLOYMENT_STUDENT_TERTIARY_value +
                                        IS_EMPLOYMENT_EDUCATION_value
                                    ) > 0.0
                                )
                            ) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                    ) +
                (
                    shift_arb_on_logsum_pt_active * (
                        if ((
                            (
                                IS_EMPLOYMENT_FULLTIME_value +
                                    IS_EMPLOYMENT_PARTTIME_value +
                                    IS_EMPLOYMENT_MARGINAL_value
                                ) > 0.0
                            )
                        ) {
                            1.0
                        } else {
                            0.0
                        }
                        )
                    ) +
                (shift_zk_on_logsum_pt_active * HAS_COMMUTER_TICKET_value) +
                (shift_carav_on_logsum_pt_active * (if ((CARS_PER_ADULT_value >= 1.0)) 1.0 else 0.0)) +
                (
                    shift_high_inc_on_logsum_pt_active *
                        (
                            if ((4.0 <= HOUSEHOLD_ECONOMICAL_STATUS_value && HOUSEHOLD_ECONOMICAL_STATUS_value <= 5.0)) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                    ) +
                (shift_uml_on_logsum_pt_active * IS_UMLAND_value)
            val zLogsumPT =
                if (AVAIL_FUSS_value + AVAIL_RAD_value + AVAIL_OEV_value >= 1.0) {
                    (AVAIL_FUSS_value + AVAIL_RAD_value + AVAIL_OEV_value) * (
                        ln(
                            AVAIL_OEV_value * oevExp +
                                (
                                    AVAIL_FUSS_value *
                                        (
                                            exp(
                                                (
                                                    asc_fuss +
                                                        (
                                                            b_tt_fuss *
                                                                (
                                                                    TRAVEL_TIME_PEDESTRIAN_value.coerceAtMost(
                                                                        1000.0
                                                                    )
                                                                    )
                                                            ) +
                                                        (b_arb_on_fuss * IS_ACTIVITY_TYPE_WORK_value) +
                                                        (b_dienst_on_fuss * IS_ACTIVITY_TYPE_BUSINESS_value) +
                                                        (b_service_on_fuss * IS_ACTIVITY_TYPE_SERVICE_value) +
                                                        (b_freizeit_on_fuss * ACTIVITY_TYPE_IS_LEISURE_value)
                                                    )
                                            )
                                            )
                                    ) + (
                                    AVAIL_RAD_value * (
                                        exp(
                                            (
                                                asc_rad +
                                                    (
                                                        b_tt_rad *
                                                            (
                                                                TRAVEL_TIME_BIKE_value.coerceAtMost(
                                                                    1000.0
                                                                )
                                                                )
                                                        ) +
                                                    (b_arb_on_rad * IS_ACTIVITY_TYPE_WORK_value) +
                                                    (b_dienst_on_rad * IS_ACTIVITY_TYPE_BUSINESS_value) +
                                                    (b_freizeit_on_rad * ACTIVITY_TYPE_IS_LEISURE_value) +
                                                    (b_service_on_rad * IS_ACTIVITY_TYPE_SERVICE_value)
                                                )
                                        )
                                        )
                                    )
                        )
                        )
                } else {
                    -50.0
                }

//                (AVAIL_FUSS_value + AVAIL_RAD_value + AVAIL_OEV_value) * (
//                    ln(
//                        AVAIL_OEV_value * oevExp +
//                                (AVAIL_FUSS_value *
//                                        (exp((
//                                            asc_fuss +
//                                                    (b_tt_fuss *
//                                                            (TRAVEL_TIME_PEDESTRIAN_value.coerceAtMost(1000.0))) +
//                                                    (b_arb_on_fuss * IS_ACTIVITY_TYPE_WORK_value) +
//                                                    (b_dienst_on_fuss * IS_ACTIVITY_TYPE_BUSINESS_value) +
//                                                    (b_service_on_fuss * IS_ACTIVITY_TYPE_SERVICE_value) +
//                                                    (b_freizeit_on_fuss * ACTIVITY_TYPE_IS_LEISURE_value)
//                                                )
//                                        ))) + (
//                                AVAIL_RAD_value * (
//                                        exp((
//                                            asc_rad +
//                                                    (b_tt_rad *
//                                                            (TRAVEL_TIME_BIKE_value.coerceAtMost(1000.0))) +
//                                                    (b_arb_on_rad * IS_ACTIVITY_TYPE_WORK_value) +
//                                                    (b_dienst_on_rad * IS_ACTIVITY_TYPE_BUSINESS_value) +
//                                                    (b_freizeit_on_rad * ACTIVITY_TYPE_IS_LEISURE_value) +
//                                                    (b_service_on_rad * IS_ACTIVITY_TYPE_SERVICE_value)
//                                                )
//                                        )
//                                        )
//                                )
//                    )
//                    ) + (if (((AVAIL_FUSS_value == 0.0) && (AVAIL_RAD_value == 0.0) && (AVAIL_OEV_value == 0.0))) 1.0 else 0.0) * (-50.0)
            val secondVar =
                zlogsumPTFactor * zLogsumPT
            val zLogsumCar = if (AVAIL_PKW_value + AVAIL_MF_value >= 1.0) {
                (AVAIL_PKW_value + AVAIL_MF_value) * (
                    ln(
                        AVAIL_PKW_value *
                            (
                                exp(
                                    (
                                        asc_pkw +
                                            (b_tt_pkw * (TRAVEL_TIME_CAR_value.coerceAtMost(1000.0))) +
                                            (b_cost_pkw * (TRAVEL_COST_CAR_value.coerceAtMost(1000.0))) +
                                            (
                                                (b_zuab_pkw * (ACCESS_TIME_CAR_value + EGRESS_TIME_CAR_value)) *
                                                    (
                                                        (if ((1000.0 > ACCESS_TIME_CAR_value)) 1.0 else 0.0) *
                                                            (if ((1000.0 > EGRESS_TIME_CAR_value)) 1.0 else 0.0)
                                                        )
                                                ) +
                                            (b_arb_on_pkw * IS_ACTIVITY_TYPE_WORK_value) +
                                            (b_dienst_on_pkw * IS_ACTIVITY_TYPE_BUSINESS_value) +
                                            (b_freizeit_on_pkw * ACTIVITY_TYPE_IS_LEISURE_value) +
                                            (b_service_on_pkw * IS_ACTIVITY_TYPE_SERVICE_value)
                                        )
                                )
                                ) +
                            AVAIL_MF_value * (
                                exp(
                                    (
                                        asc_mf +
                                            (
                                                b_tt_mf_taxi *
                                                    (TRAVEL_TIME_CAR_value + 3.0).coerceAtMost(
                                                        1000.0
                                                    )
                                                ) +
                                            (b_arb_on_mf * IS_ACTIVITY_TYPE_WORK_value) +
                                            (b_dienst_on_mf * IS_ACTIVITY_TYPE_BUSINESS_value) +
                                            (b_home_on_mf * ACTIVITY_TYPE_IS_HOME_value) +
                                            (b_service_on_mf * IS_ACTIVITY_TYPE_SERVICE_value)
                                        )
                                )
                                )
                    )
                    )
            } else {
                -50.0
            }
            val zlogsumCarFactor = b_logsum_drive +
                (
                    shift_age_2_on_logsum_drive *
                        (if ((18.0 <= AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)
                    ) +
                (shift_age_3_on_logsum_drive * (if ((30.0 <= AGE_value && AGE_value <= 39.0)) 1.0 else 0.0)) +
                (shift_age_4_on_logsum_drive * (if ((40.0 <= AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) +
                (shift_age_56_on_logsum_drive * (if ((50.0 <= AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) +
                (shift_age_78_on_logsum_drive * (if ((70.0 <= AGE_value && AGE_value <= 120.0)) 1.0 else 0.0)) +
                (
                    shift_educ_on_logsum_drive * (
                        if ((
                            (
                                IS_EMPLOYMENT_STUDENT_value +
                                    IS_EMPLOYMENT_STUDENT_PRIMARY_value +
                                    IS_EMPLOYMENT_STUDENT_SECONDARY_value +
                                    IS_EMPLOYMENT_STUDENT_TERTIARY_value +
                                    IS_EMPLOYMENT_EDUCATION_value
                                ) > 0.0
                            )
                        ) {
                            1.0
                        } else {
                            0.0
                        }
                        )
                    ) +
                (
                    shift_arb_on_logsum_drive *
                        (
                            if ((
                                (
                                    IS_EMPLOYMENT_FULLTIME_value +
                                        IS_EMPLOYMENT_PARTTIME_value +
                                        IS_EMPLOYMENT_MARGINAL_value
                                    ) > 0.0
                                )
                            ) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                    ) +
                (shift_zk_on_logsum_drive * HAS_COMMUTER_TICKET_value) +
                (shift_carav_on_logsum_drive * (if ((CARS_PER_ADULT_value >= 1.0)) 1.0 else 0.0)) +
                (
                    shift_high_inc_on_logsum_drive *
                        (
                            if ((4.0 <= HOUSEHOLD_ECONOMICAL_STATUS_value && HOUSEHOLD_ECONOMICAL_STATUS_value <= 5.0)) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                    ) +
                (shift_uml_on_logsum_drive * IS_UMLAND_value)
            val thirdVar =
                zlogsumCarFactor * zLogsumCar
            val zlogsumPtFixFactor = b_logsum_pt_active_fix +
                (
                    shift_age_2_on_logsum_pt_active_fix *
                        (if ((18.0 <= AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)
                    ) +
                (
                    shift_age_3_on_logsum_pt_active_fix *
                        (if ((30.0 <= AGE_value && AGE_value <= 39.0)) 1.0 else 0.0)
                    ) +
                (
                    shift_age_4_on_logsum_pt_active_fix *
                        (if ((40.0 <= AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)
                    ) +
                (
                    shift_age_56_on_logsum_pt_active_fix *
                        (if ((50.0 <= AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)
                    ) +
                (
                    shift_age_78_on_logsum_pt_active_fix *
                        (if ((70.0 <= AGE_value && AGE_value <= 120.0)) 1.0 else 0.0)
                    ) +
                (
                    shift_educ_on_logsum_pt_active_fix *
                        (
                            if ((
                                (
                                    IS_EMPLOYMENT_STUDENT_value +
                                        IS_EMPLOYMENT_STUDENT_PRIMARY_value +
                                        IS_EMPLOYMENT_STUDENT_SECONDARY_value +
                                        IS_EMPLOYMENT_STUDENT_TERTIARY_value +
                                        IS_EMPLOYMENT_EDUCATION_value
                                    ) > 0.0
                                )
                            ) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                    ) +
                (
                    shift_arb_on_logsum_pt_active_fix *
                        (
                            if ((
                                (
                                    IS_EMPLOYMENT_FULLTIME_value +
                                        IS_EMPLOYMENT_PARTTIME_value +
                                        IS_EMPLOYMENT_MARGINAL_value
                                    ) > 0.0
                                )
                            ) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                    ) +
                (shift_zk_on_logsum_pt_active_fix * HAS_COMMUTER_TICKET_value) +
                (shift_carav_on_logsum_pt_active_fix * (if ((CARS_PER_ADULT_value >= 1.0)) 1.0 else 0.0)) +
                (
                    shift_high_inc_on_logsum_pt_active_fix *
                        (
                            if ((4.0 <= HOUSEHOLD_ECONOMICAL_STATUS_value && HOUSEHOLD_ECONOMICAL_STATUS_value <= 5.0)) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                    ) +
                (shift_uml_on_logsum_pt_active_fix * IS_UMLAND_value)
            val zlogsumPtFix =
                if (AVAIL_FUSS_value + AVAIL_RAD_value + AVAIL_OEV_value >= 1.0) {
                    (AVAIL_FUSS_value + AVAIL_RAD_value + AVAIL_OEV_value) * (
                        ln(
                            (
                                AVAIL_OEV_value * (
                                    exp(
                                        (
                                            asc_oev +
                                                (
                                                    b_tt_oev *
                                                        (
                                                            TRAVEL_TIME_PUBLICTRANSPORT_FIX_value.coerceAtMost(
                                                                1000.0
                                                            )
                                                            )
                                                    ) +
                                                (
                                                    b_cost_oev *
                                                        (
                                                            TRAVEL_COST_PUBLICTRANSPORT_FIX_value.coerceAtMost(
                                                                1000.0
                                                            )
                                                            )
                                                    ) +
                                                (
                                                    b_zuab_oev * (
                                                        (
                                                            if ((1000.0 > ACCESS_TIME_PUBLICTRANSPORT_FIX_value && 1000.0 > EGRESS_TIME_PUBLICTRANSPORT_FIX_value)) {
                                                                (ACCESS_TIME_PUBLICTRANSPORT_FIX_value + EGRESS_TIME_PUBLICTRANSPORT_FIX_value)
                                                            } else {
                                                                (999.0)
                                                            }
                                                            )
                                                        )
                                                    ) +
                                                (b_arb_on_oev * IS_ACTIVITY_TYPE_WORK_value) +
                                                (b_dienst_on_oev * IS_ACTIVITY_TYPE_BUSINESS_value) +
                                                (b_freizeit_on_oev * ACTIVITY_TYPE_IS_LEISURE_value) +
                                                (b_service_on_oev * IS_ACTIVITY_TYPE_SERVICE_value) + (
                                                    (
                                                        b_park_oev * (
                                                            (if ((50.0 > PARKDRUCK_FIX_value)) (PARKDRUCK_FIX_value) else (50.0)).pow(
                                                                elasticity_park_oev
                                                            )
                                                            )
                                                        ) * (if ((999.0 > PARKDRUCK_FIX_value)) 1.0 else 0.0)
                                                    )
                                            )
                                    )
                                    )
                                ) + (
                                AVAIL_FUSS_value * (
                                    exp(
                                        (
                                            asc_fuss + (
                                                b_tt_fuss *
                                                    (
                                                        TRAVEL_TIME_PEDESTRIAN_FIX_value.coerceAtMost(
                                                            1000.0
                                                        )
                                                        )
                                                ) +
                                                (b_arb_on_fuss * IS_ACTIVITY_TYPE_WORK_value) +
                                                (b_dienst_on_fuss * IS_ACTIVITY_TYPE_BUSINESS_value) +
                                                (b_service_on_fuss * IS_ACTIVITY_TYPE_SERVICE_value) +
                                                (b_freizeit_on_fuss * ACTIVITY_TYPE_IS_LEISURE_value)
                                            )
                                    )
                                    )
                                ) +
                                (
                                    AVAIL_RAD_value * (
                                        exp(
                                            (
                                                asc_rad + (
                                                    b_tt_rad *
                                                        (
                                                            TRAVEL_TIME_BIKE_FIX_value.coerceAtMost(
                                                                1000.0
                                                            )
                                                            )
                                                    ) +
                                                    (b_arb_on_rad * IS_ACTIVITY_TYPE_WORK_value) +
                                                    (b_dienst_on_rad * IS_ACTIVITY_TYPE_BUSINESS_value) +
                                                    (b_freizeit_on_rad * ACTIVITY_TYPE_IS_LEISURE_value) +
                                                    (b_service_on_rad * IS_ACTIVITY_TYPE_SERVICE_value)
                                                )
                                        )
                                        )
                                    )
                        )
                        )
                } else {
                    -50.0
                }
//                (AVAIL_FUSS_value + AVAIL_RAD_value + AVAIL_OEV_value) * (
//                    ln(
//                        (
//                                AVAIL_OEV_value * (
//                                        exp((
//                                            asc_oev +
//                                                    (b_tt_oev *
//                                                            (TRAVEL_TIME_PUBLICTRANSPORT_FIX_value.coerceAtMost(1000.0))) +
//                                                    (b_cost_oev *
//                                                            (TRAVEL_COST_PUBLICTRANSPORT_FIX_value.coerceAtMost(1000.0))) +
//                                                    (b_zuab_oev * ((if ((1000.0 > ACCESS_TIME_PUBLICTRANSPORT_FIX_value && 1000.0 > EGRESS_TIME_PUBLICTRANSPORT_FIX_value))
//                                                        (ACCESS_TIME_PUBLICTRANSPORT_FIX_value + EGRESS_TIME_PUBLICTRANSPORT_FIX_value) else (999.0)))) +
//                                                    (b_arb_on_oev * IS_ACTIVITY_TYPE_WORK_value) +
//                                                    (b_dienst_on_oev * IS_ACTIVITY_TYPE_BUSINESS_value) +
//                                                    (b_freizeit_on_oev * ACTIVITY_TYPE_IS_LEISURE_value) +
//                                                    (b_service_on_oev * IS_ACTIVITY_TYPE_SERVICE_value) + (
//                                                    (
//                                                            b_park_oev * (
//                                                                    (if ((50.0 > PARKDRUCK_FIX_value)) (PARKDRUCK_FIX_value) else (50.0)).pow(
//                                                                        elasticity_park_oev
//                                                                    )
//                                                                    )
//                                                            ) * (if ((999.0 > PARKDRUCK_FIX_value)) 1.0 else 0.0)
//                                                    )
//                                                )
//                                        )
//                                        )
//                                ) + (AVAIL_FUSS_value * (
//                                exp((
//                                    asc_fuss + (b_tt_fuss *
//                                            (TRAVEL_TIME_PEDESTRIAN_FIX_value.coerceAtMost(1000.0)))
//                                            + (b_arb_on_fuss * IS_ACTIVITY_TYPE_WORK_value) +
//                                            (b_dienst_on_fuss * IS_ACTIVITY_TYPE_BUSINESS_value) +
//                                            (b_service_on_fuss * IS_ACTIVITY_TYPE_SERVICE_value) +
//                                            (b_freizeit_on_fuss * ACTIVITY_TYPE_IS_LEISURE_value))
//                                ))) +
//                                (
//                                        AVAIL_RAD_value * (
//                                                exp((
//                                                    asc_rad + (b_tt_rad *
//                                                            (TRAVEL_TIME_BIKE_FIX_value.coerceAtMost(1000.0)))
//                                                            + (b_arb_on_rad * IS_ACTIVITY_TYPE_WORK_value)
//                                                            + (b_dienst_on_rad * IS_ACTIVITY_TYPE_BUSINESS_value)
//                                                            + (b_freizeit_on_rad * ACTIVITY_TYPE_IS_LEISURE_value)
//                                                            + (b_service_on_rad * IS_ACTIVITY_TYPE_SERVICE_value)
//                                                        )
//                                                )
//                                                )
//                                        )
//                    )
//                    ) + (if (((AVAIL_FUSS_value == 0.0) && (AVAIL_RAD_value == 0.0) && (AVAIL_OEV_value == 0.0))) 1.0 else 0.0) * (-50.0)
            val fourthVar =
                zlogsumPtFixFactor * zlogsumPtFix
            val zlogSumCarFix = if (AVAIL_PKW_value + AVAIL_MF_value >= 1.0) {
                (AVAIL_PKW_value + AVAIL_MF_value) * (
                    ln(
                        AVAIL_PKW_value *
                            (
                                exp(
                                    (
                                        asc_pkw +
                                            (
                                                b_tt_pkw *
                                                    (
                                                        TRAVEL_TIME_CAR_FIX_value.coerceAtMost(
                                                            1000.0
                                                        )
                                                        )
                                                ) +
                                            (
                                                b_cost_pkw * (
                                                    TRAVEL_COST_CAR_FIX_value.coerceAtMost(
                                                        1000.0
                                                    )
                                                    )
                                                ) +
                                            (
                                                (b_zuab_pkw * (ACCESS_TIME_CAR_FIX_value + EGRESS_TIME_CAR_FIX_value)) *
                                                    (
                                                        (if ((1000.0 > ACCESS_TIME_CAR_FIX_value)) 1.0 else 0.0) *
                                                            (if ((1000.0 > EGRESS_TIME_CAR_FIX_value)) 1.0 else 0.0)
                                                        )
                                                ) +
                                            (b_arb_on_pkw * IS_ACTIVITY_TYPE_WORK_value) +
                                            (b_dienst_on_pkw * IS_ACTIVITY_TYPE_BUSINESS_value) +
                                            (b_freizeit_on_pkw * ACTIVITY_TYPE_IS_LEISURE_value) +
                                            (b_service_on_pkw * IS_ACTIVITY_TYPE_SERVICE_value)
                                        )
                                )
                                ) +
                            AVAIL_MF_value * (
                                exp(
                                    (
                                        asc_mf + (
                                            b_tt_mf_taxi *
                                                (TRAVEL_TIME_CAR_FIX_value + 3).coerceAtMost(1000.0)
                                            ) +
                                            (b_arb_on_mf * IS_ACTIVITY_TYPE_WORK_value) +
                                            (b_dienst_on_mf * IS_ACTIVITY_TYPE_BUSINESS_value) +
                                            (b_home_on_mf * ACTIVITY_TYPE_IS_HOME_value) +
                                            (b_service_on_mf * IS_ACTIVITY_TYPE_SERVICE_value)
                                        )
                                )
                                )
                    )
                    )
            } else {
                -50.0
            }
            val zLogsumCarFixFactor = b_logsum_drive_fix +
                (shift_age_2_on_logsum_drive_fix * (if ((18.0 <= AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) +
                (shift_age_3_on_logsum_drive_fix * (if ((30.0 <= AGE_value && AGE_value <= 39.0)) 1.0 else 0.0)) +
                (shift_age_4_on_logsum_drive_fix * (if ((40.0 <= AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) +
                (shift_age_56_on_logsum_drive_fix * (if ((50.0 <= AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) +
                (shift_age_78_on_logsum_drive_fix * (if ((70.0 <= AGE_value && AGE_value <= 120.0)) 1.0 else 0.0)) +
                (
                    shift_educ_on_logsum_drive_fix *
                        (
                            if ((
                                (
                                    IS_EMPLOYMENT_STUDENT_value +
                                        IS_EMPLOYMENT_STUDENT_PRIMARY_value +
                                        IS_EMPLOYMENT_STUDENT_SECONDARY_value +
                                        IS_EMPLOYMENT_STUDENT_TERTIARY_value +
                                        IS_EMPLOYMENT_EDUCATION_value
                                    ) > 0.0
                                )
                            ) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                    ) +
                (
                    shift_arb_on_logsum_drive_fix * (
                        if ((
                            (
                                IS_EMPLOYMENT_FULLTIME_value +
                                    IS_EMPLOYMENT_PARTTIME_value +
                                    IS_EMPLOYMENT_MARGINAL_value
                                ) > 0.0
                            )
                        ) {
                            1.0
                        } else {
                            0.0
                        }
                        )
                    ) +
                (shift_zk_on_logsum_drive_fix * HAS_COMMUTER_TICKET_value) +
                (shift_carav_on_logsum_drive_fix * (if ((CARS_PER_ADULT_value >= 1.0)) 1.0 else 0.0)) +
                (
                    shift_high_inc_on_logsum_drive_fix *
                        (
                            if ((4.0 <= HOUSEHOLD_ECONOMICAL_STATUS_value && HOUSEHOLD_ECONOMICAL_STATUS_value <= 5.0)) {
                                1.0
                            } else {
                                0.0
                            }
                            )
                    ) +
                (shift_uml_on_logsum_drive_fix * IS_UMLAND_value)
            val fifthVar =
                zLogsumCarFixFactor * zlogSumCarFix
            val zConstant = (b_0_1 * (if ((0.0 < DISTANCE_value && DISTANCE_value <= 1.0)) 1.0 else 0.0)) +
                (b_1_2 * (if ((1.0 < DISTANCE_value && DISTANCE_value <= 2.0)) 1.0 else 0.0)) +
                (
                    b_parken * (
                        (if ((50.0 > PARKDRUCK_value)) (PARKDRUCK_value) else (50.0)).pow(
                            elasticity_parken * (if ((999.0 > PARKDRUCK_value)) 1.0 else 0.0)
                        )
                        )
                    )
            (
                firstVar + zConstant +
                    secondVar +
                    thirdVar +
                    fourthVar +
                    fifthVar
                )
        }
//        val output = parameterSet.run {
//            (
//                (
//                    (b_attr + shift_age_2_on_attr * (if ((18.0 <= AGE_value && AGE_value <= 29.0)) 1.0 else 0.0) + (shift_age_3_on_attr * (if ((30.0 <= AGE_value && AGE_value <= 39.0)) 1.0 else 0.0)) + (shift_age_4_on_attr * (if ((40.0 <= AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) + (shift_age_56_on_attr * (if ((50.0 <= AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) + (shift_age_78_on_attr * (if ((70.0 <= AGE_value && AGE_value <= 120.0)) 1.0 else 0.0)) + (shift_educ_on_attr * (if (((IS_EMPLOYMENT_STUDENT_value + IS_EMPLOYMENT_STUDENT_PRIMARY_value + IS_EMPLOYMENT_STUDENT_SECONDARY_value + IS_EMPLOYMENT_STUDENT_TERTIARY_value + IS_EMPLOYMENT_EDUCATION_value) > 0.0)) 1.0 else 0.0)) + (shift_arb_on_attr * (if (((IS_EMPLOYMENT_FULLTIME_value + IS_EMPLOYMENT_PARTTIME_value + IS_EMPLOYMENT_MARGINAL_value) > 0.0)) 1.0 else 0.0)) + (shift_zk_on_attr * HAS_COMMUTER_TICKET_value) + (shift_carav_on_attr * (if ((CARS_PER_ADULT_value >= 1.0)) 1.0 else 0.0)) + (shift_high_inc_on_attr * (if ((4.0 <= HOUSEHOLD_ECONOMICAL_STATUS_value && HOUSEHOLD_ECONOMICAL_STATUS_value <= 5.0)) 1.0 else 0.0)) + (shift_uml_on_attr * IS_UMLAND_value) + (shift_b_0_1_on_attr * (if ((0.0 < DISTANCE_value && DISTANCE_value <= 1.0)) 1.0 else 0.0)) + (shift_b_1_2_on_attr * (if ((1.0 < DISTANCE_value && DISTANCE_value <= 2.0)) 1.0 else 0.0))) * (
//                        ln(
//                            (if ((ATTRACTIVITY_value > max_attractivity)) (max_attractivity) else (ATTRACTIVITY_value))
//                        )
//                        )
//                    ) +
//                    (b_0_1 * (if ((0.0 < DISTANCE_value && DISTANCE_value <= 1.0)) 1.0 else 0.0)) +
//                    (b_1_2 * (if ((1.0 < DISTANCE_value && DISTANCE_value <= 2.0)) 1.0 else 0.0)) +
//                    (b_parken * ((if ((50.0 > PARKDRUCK_value)) (PARKDRUCK_value) else (50.0)).pow(elasticity_parken * (if ((999.0 > PARKDRUCK_value)) 1.0 else 0.0)))) +
//                    (
//                        (b_logsum_pt_active + shift_age_2_on_logsum_pt_active * (if ((18.0 <= AGE_value && AGE_value <= 29.0)) 1.0 else 0.0) + (shift_age_3_on_logsum_pt_active * (if ((30.0 <= AGE_value && AGE_value <= 39.0)) 1.0 else 0.0)) + (shift_age_4_on_logsum_pt_active * (if ((40.0 <= AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) + (shift_age_56_on_logsum_pt_active * (if ((50.0 <= AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) + (shift_age_78_on_logsum_pt_active * (if ((70.0 <= AGE_value && AGE_value <= 120.0)) 1.0 else 0.0)) + (shift_educ_on_logsum_pt_active * (if (((IS_EMPLOYMENT_STUDENT_value + IS_EMPLOYMENT_STUDENT_PRIMARY_value + IS_EMPLOYMENT_STUDENT_SECONDARY_value + IS_EMPLOYMENT_STUDENT_TERTIARY_value + IS_EMPLOYMENT_EDUCATION_value) > 0.0)) 1.0 else 0.0)) + (shift_arb_on_logsum_pt_active * (if (((IS_EMPLOYMENT_FULLTIME_value + IS_EMPLOYMENT_PARTTIME_value + IS_EMPLOYMENT_MARGINAL_value) > 0.0)) 1.0 else 0.0)) + (shift_zk_on_logsum_pt_active * HAS_COMMUTER_TICKET_value) + (shift_carav_on_logsum_pt_active * (if ((CARS_PER_ADULT_value >= 1.0)) 1.0 else 0.0)) + (shift_high_inc_on_logsum_pt_active * (if ((4.0 <= HOUSEHOLD_ECONOMICAL_STATUS_value && HOUSEHOLD_ECONOMICAL_STATUS_value <= 5.0)) 1.0 else 0.0)) + (shift_uml_on_logsum_pt_active * IS_UMLAND_value)) * (
//                            (AVAIL_FUSS_value + AVAIL_RAD_value + AVAIL_OEV_value) * (
//                                ln(
//                                    AVAIL_OEV_value * (
//                                        exp(
//                                            asc_oev + b_tt_oev * ((if ((1000.0 > TRAVEL_TIME_PUBLICTRANSPORT_value)) (TRAVEL_TIME_PUBLICTRANSPORT_value) else (999.0))) + (b_cost_oev * ((if ((1000.0 > TRAVEL_COST_PUBLICTRANSPORT_value)) (TRAVEL_COST_PUBLICTRANSPORT_value) else (999.0)))) + (b_zuab_oev * ((if ((1000.0 > ACCESS_TIME_PUBLICTRANSPORT_value && 1000.0 > EGRESS_TIME_PUBLICTRANSPORT_value)) (ACCESS_TIME_PUBLICTRANSPORT_value + EGRESS_TIME_PUBLICTRANSPORT_value) else (999.0)))) + (b_arb_on_oev * IS_ACTIVITY_TYPE_WORK_value) + (b_dienst_on_oev * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_freizeit_on_oev * ACTIVITY_TYPE_IS_LEISURE_value) + (b_service_on_oev * IS_ACTIVITY_TYPE_SERVICE_value) + (
//                                                (
//                                                    b_park_oev * (
//                                                        (if ((50.0 > PARKDRUCK_value)) (PARKDRUCK_value) else (50.0)).pow(
//                                                            elasticity_park_oev
//                                                        )
//                                                        )
//                                                    ) * (if ((999.0 > PARKDRUCK_value)) 1.0 else 0.0)
//                                                )
//                                        )
//                                        ) + (AVAIL_FUSS_value * (exp(asc_fuss + (b_tt_fuss * ((if ((1000.0 > TRAVEL_TIME_PEDESTRIAN_value)) (TRAVEL_TIME_PEDESTRIAN_value) else (999.0)))) + (b_arb_on_fuss * IS_ACTIVITY_TYPE_WORK_value) + (b_dienst_on_fuss * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_service_on_fuss * IS_ACTIVITY_TYPE_SERVICE_value) + (b_freizeit_on_fuss * ACTIVITY_TYPE_IS_LEISURE_value)))) + (
//                                        AVAIL_RAD_value * (
//                                            exp(
//                                                asc_rad + (b_tt_rad * ((if ((1000.0 > TRAVEL_TIME_BIKE_value)) (TRAVEL_TIME_BIKE_value) else (999.0)))) + (b_arb_on_rad * IS_ACTIVITY_TYPE_WORK_value) + (b_dienst_on_rad * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_freizeit_on_rad * ACTIVITY_TYPE_IS_LEISURE_value) + (b_service_on_rad * IS_ACTIVITY_TYPE_SERVICE_value)
//                                            )
//                                            )
//                                        )
//                                )
//                                ) + (if (((AVAIL_FUSS_value == 0.0) && (AVAIL_RAD_value == 0.0) && (AVAIL_OEV_value == 0.0))) 1.0 else 0.0) * (-50.0)
//                            )
//                        ) +
//                    (
//                        (b_logsum_drive + (shift_age_2_on_logsum_drive * (if ((18.0 <= AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) + (shift_age_3_on_logsum_drive * (if ((30.0 <= AGE_value && AGE_value <= 39.0)) 1.0 else 0.0)) + (shift_age_4_on_logsum_drive * (if ((40.0 <= AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) + (shift_age_56_on_logsum_drive * (if ((50.0 <= AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) + (shift_age_78_on_logsum_drive * (if ((70.0 <= AGE_value && AGE_value <= 120.0)) 1.0 else 0.0)) + (shift_educ_on_logsum_drive * (if (((IS_EMPLOYMENT_STUDENT_value + IS_EMPLOYMENT_STUDENT_PRIMARY_value + IS_EMPLOYMENT_STUDENT_SECONDARY_value + IS_EMPLOYMENT_STUDENT_TERTIARY_value + IS_EMPLOYMENT_EDUCATION_value) > 0.0)) 1.0 else 0.0)) + (shift_arb_on_logsum_drive * (if (((IS_EMPLOYMENT_FULLTIME_value + IS_EMPLOYMENT_PARTTIME_value + IS_EMPLOYMENT_MARGINAL_value) > 0.0)) 1.0 else 0.0)) + (shift_zk_on_logsum_drive * HAS_COMMUTER_TICKET_value) + (shift_carav_on_logsum_drive * (if ((CARS_PER_ADULT_value >= 1.0)) 1.0 else 0.0)) + (shift_high_inc_on_logsum_drive * (if ((4.0 <= HOUSEHOLD_ECONOMICAL_STATUS_value && HOUSEHOLD_ECONOMICAL_STATUS_value <= 5.0)) 1.0 else 0.0)) + (shift_uml_on_logsum_drive * IS_UMLAND_value)) * (
//                            (AVAIL_PKW_value + AVAIL_MF_value) * (
//                                ln(
//                                    AVAIL_PKW_value * (exp(asc_pkw + (b_tt_pkw * ((if ((1000.0 > TRAVEL_TIME_CAR_value)) (TRAVEL_TIME_CAR_value) else (999.0)))) + (b_cost_pkw * ((if ((1000.0 > TRAVEL_COST_CAR_value)) (TRAVEL_COST_CAR_value) else (999.0)))) + ((b_zuab_pkw * (ACCESS_TIME_CAR_value + EGRESS_TIME_CAR_value)) * ((if ((1000.0 > ACCESS_TIME_CAR_value)) 1.0 else 0.0) * (if ((1000.0 > EGRESS_TIME_CAR_value)) 1.0 else 0.0))) + (b_arb_on_pkw * IS_ACTIVITY_TYPE_WORK_value) + (b_dienst_on_pkw * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_freizeit_on_pkw * ACTIVITY_TYPE_IS_LEISURE_value) + (b_service_on_pkw * IS_ACTIVITY_TYPE_SERVICE_value))) + AVAIL_MF_value * (
//                                        exp(
//                                            asc_mf + (b_tt_mf_taxi * ((if ((1000.0 > TRAVEL_TIME_CAR_value)) (TRAVEL_TIME_CAR_value + 3.0) else (999.0)))) + (b_arb_on_mf * IS_ACTIVITY_TYPE_WORK_value) + (b_dienst_on_mf * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_home_on_mf * ACTIVITY_TYPE_IS_HOME_value) + (b_service_on_mf * IS_ACTIVITY_TYPE_SERVICE_value)
//                                        )
//                                        )
//                                )
//                                ) + (if ((AVAIL_PKW_value == 0.0 && AVAIL_MF_value == 0.0)) 1.0 else 0.0) * (-50.0)
//                            )
//                        ) +
//                    (
//                        (b_logsum_pt_active_fix + (shift_age_2_on_logsum_pt_active_fix * (if ((18.0 <= AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) + (shift_age_3_on_logsum_pt_active_fix * (if ((30.0 <= AGE_value && AGE_value <= 39.0)) 1.0 else 0.0)) + (shift_age_4_on_logsum_pt_active_fix * (if ((40.0 <= AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) + (shift_age_56_on_logsum_pt_active_fix * (if ((50.0 <= AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) + (shift_age_78_on_logsum_pt_active_fix * (if ((70.0 <= AGE_value && AGE_value <= 120.0)) 1.0 else 0.0)) + (shift_educ_on_logsum_pt_active_fix * (if (((IS_EMPLOYMENT_STUDENT_value + IS_EMPLOYMENT_STUDENT_PRIMARY_value + IS_EMPLOYMENT_STUDENT_SECONDARY_value + IS_EMPLOYMENT_STUDENT_TERTIARY_value + IS_EMPLOYMENT_EDUCATION_value) > 0.0)) 1.0 else 0.0)) + (shift_arb_on_logsum_pt_active_fix * (if (((IS_EMPLOYMENT_FULLTIME_value + IS_EMPLOYMENT_PARTTIME_value + IS_EMPLOYMENT_MARGINAL_value) > 0.0)) 1.0 else 0.0)) + (shift_zk_on_logsum_pt_active_fix * HAS_COMMUTER_TICKET_value) + (shift_carav_on_logsum_pt_active_fix * (if ((CARS_PER_ADULT_value >= 1.0)) 1.0 else 0.0)) + (shift_high_inc_on_logsum_pt_active_fix * (if ((4.0 <= HOUSEHOLD_ECONOMICAL_STATUS_value && HOUSEHOLD_ECONOMICAL_STATUS_value <= 5.0)) 1.0 else 0.0)) + (shift_uml_on_logsum_pt_active_fix * IS_UMLAND_value)) * (
//                            (AVAIL_FUSS_value + AVAIL_RAD_value + AVAIL_OEV_value) * (
//                                ln(
//                                    (
//                                        AVAIL_OEV_value * (
//                                            exp(
//                                                asc_oev + (b_tt_oev * ((if ((1000.0 > TRAVEL_TIME_PUBLICTRANSPORT_FIX_value)) (TRAVEL_TIME_PUBLICTRANSPORT_FIX_value) else (999.0)))) + (b_cost_oev * ((if ((1000.0 > TRAVEL_COST_PUBLICTRANSPORT_FIX_value)) (TRAVEL_COST_PUBLICTRANSPORT_FIX_value) else (999.0)))) + (b_zuab_oev * ((if ((1000.0 > ACCESS_TIME_PUBLICTRANSPORT_FIX_value && 1000.0 > EGRESS_TIME_PUBLICTRANSPORT_FIX_value)) (ACCESS_TIME_PUBLICTRANSPORT_FIX_value + EGRESS_TIME_PUBLICTRANSPORT_FIX_value) else (999.0)))) + (b_arb_on_oev * IS_ACTIVITY_TYPE_WORK_value) + (b_dienst_on_oev * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_freizeit_on_oev * ACTIVITY_TYPE_IS_LEISURE_value) + (b_service_on_oev * IS_ACTIVITY_TYPE_SERVICE_value) + (
//                                                    (
//                                                        b_park_oev * (
//                                                            (if ((50.0 > PARKDRUCK_FIX_value)) (PARKDRUCK_FIX_value) else (50.0)).pow(
//                                                                elasticity_park_oev
//                                                            )
//                                                            )
//                                                        ) * (if ((999.0 > PARKDRUCK_FIX_value)) 1.0 else 0.0)
//                                                    )
//                                            )
//                                            )
//                                        ) + (AVAIL_FUSS_value * (exp(asc_fuss + (b_tt_fuss * ((if ((1000.0 > TRAVEL_TIME_PEDESTRIAN_FIX_value)) (TRAVEL_TIME_PEDESTRIAN_FIX_value) else (999.0)))) + (b_arb_on_fuss * IS_ACTIVITY_TYPE_WORK_value) + (b_dienst_on_fuss * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_service_on_fuss * IS_ACTIVITY_TYPE_SERVICE_value) + (b_freizeit_on_fuss * ACTIVITY_TYPE_IS_LEISURE_value)))) + (
//                                        AVAIL_RAD_value * (
//                                            exp(
//                                                asc_rad + (b_tt_rad * ((if ((1000.0 > TRAVEL_TIME_BIKE_FIX_value)) (TRAVEL_TIME_BIKE_FIX_value) else (999.0)))) + (b_arb_on_rad * IS_ACTIVITY_TYPE_WORK_value) + (b_dienst_on_rad * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_freizeit_on_rad * ACTIVITY_TYPE_IS_LEISURE_value) + (b_service_on_rad * IS_ACTIVITY_TYPE_SERVICE_value)
//                                            )
//                                            )
//                                        )
//                                )
//                                ) + (if (((AVAIL_FUSS_value == 0.0) && (AVAIL_RAD_value == 0.0) && (AVAIL_OEV_value == 0.0))) 1.0 else 0.0) * (-50.0)
//                            )
//                        ) +
//                    (
//                        (b_logsum_drive_fix +
//                        (shift_age_2_on_logsum_drive_fix *
//                        (if ((18.0 <= AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) +
//                        (shift_age_3_on_logsum_drive_fix *
//                        (if ((30.0 <= AGE_value && AGE_value <= 39.0)) 1.0 else 0.0)) +
//                        (shift_age_4_on_logsum_drive_fix *
//                        (if ((40.0 <= AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) +
//                        (shift_age_56_on_logsum_drive_fix *
//                        (if ((50.0 <= AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) +
//                        (shift_age_78_on_logsum_drive_fix *
//                        (if ((70.0 <= AGE_value && AGE_value <= 120.0)) 1.0 else 0.0)) +
//                        (shift_educ_on_logsum_drive_fix *
//                        (if (((IS_EMPLOYMENT_STUDENT_value +
//                        IS_EMPLOYMENT_STUDENT_PRIMARY_value +
//                        IS_EMPLOYMENT_STUDENT_SECONDARY_value +
//                        IS_EMPLOYMENT_STUDENT_TERTIARY_value +
//                        IS_EMPLOYMENT_EDUCATION_value) > 0.0)) 1.0 else 0.0)) +
//                        (shift_arb_on_logsum_drive_fix *
//                        (if (((IS_EMPLOYMENT_FULLTIME_value +
//                        IS_EMPLOYMENT_PARTTIME_value + IS_EMPLOYMENT_MARGINAL_value) > 0.0)) 1.0 else 0.0)) +
//                        (shift_zk_on_logsum_drive_fix * HAS_COMMUTER_TICKET_value) +
//                        (shift_carav_on_logsum_drive_fix * (if ((CARS_PER_ADULT_value >= 1.0)) 1.0 else 0.0)) +
//                        (shift_high_inc_on_logsum_drive_fix *
//                        (if ((4.0 <= HOUSEHOLD_ECONOMICAL_STATUS_value && HOUSEHOLD_ECONOMICAL_STATUS_value <= 5.0))
//                        1.0 else 0.0)) + (shift_uml_on_logsum_drive_fix * IS_UMLAND_value)) * (
//                            (AVAIL_PKW_value + AVAIL_MF_value) * (
//                                ln(
//                                    AVAIL_PKW_value * (exp(asc_pkw + (b_tt_pkw * ((if ((1000.0 > TRAVEL_TIME_CAR_FIX_value)) (TRAVEL_TIME_CAR_FIX_value) else (999.0)))) + (b_cost_pkw * ((if ((1000.0 > TRAVEL_COST_CAR_FIX_value)) (TRAVEL_COST_CAR_FIX_value) else (999.0)))) + ((b_zuab_pkw * (ACCESS_TIME_CAR_FIX_value + EGRESS_TIME_CAR_FIX_value)) * ((if ((1000.0 > ACCESS_TIME_CAR_FIX_value)) 1.0 else 0.0) * (if ((1000.0 > EGRESS_TIME_CAR_FIX_value)) 1.0 else 0.0))) + (b_arb_on_pkw * IS_ACTIVITY_TYPE_WORK_value) + (b_dienst_on_pkw * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_freizeit_on_pkw * ACTIVITY_TYPE_IS_LEISURE_value) + (b_service_on_pkw * IS_ACTIVITY_TYPE_SERVICE_value))) + AVAIL_MF_value * (
//                                        exp(
//                                            asc_mf + (b_tt_mf_taxi * ((if ((1000.0 > TRAVEL_TIME_CAR_FIX_value)) (TRAVEL_TIME_CAR_FIX_value + 3.0) else (999.0)))) + (b_arb_on_mf * IS_ACTIVITY_TYPE_WORK_value) + (b_dienst_on_mf * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_home_on_mf * ACTIVITY_TYPE_IS_HOME_value) + (b_service_on_mf * IS_ACTIVITY_TYPE_SERVICE_value)
//                                        )
//                                        )
//                                )
//                                ) + (if ((AVAIL_PKW_value == 0.0 && AVAIL_MF_value == 0.0)) 1.0 else 0.0) * (-50.0)
//                            )
//                        )
//                )
//        }
        return output
    }
}

class LegacyDestinationHelper(
    private val impedance: Metrics,
    private val attractiveness: AttractivenessModel,
    val umlands: (Location) -> Boolean,
    private val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    private val durationUnit: DurationUnit = DurationUnit.MINUTES,
    private val modes: ChoiceModelModes,
) {

    private val currencyUnit: CurrencyUnit = CurrencyUnit.EUROS

    val car = modes.car
    val bike = modes.bike
    val pedestrian = modes.pedestrian
    val publicTransport = modes.publicTransport
    val passenger = modes.passenger
    val bikesharing = modes.bikeSharing

    fun getAGE(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return person.age.toDouble()
    }

    fun getIS_EMPLOYMENT_STUDENT(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (person.employment == Employment.STUDENT).D
    }

    fun getIS_EMPLOYMENT_STUDENT_PRIMARY(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (person.employment == Employment.STUDENT_PRIMARY).D
    }

    fun getIS_EMPLOYMENT_STUDENT_SECONDARY(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (person.employment == Employment.STUDENT_SECONDARY).D
    }

    fun getIS_EMPLOYMENT_STUDENT_TERTIARY(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (person.employment == Employment.STUDENT_TERTIARY).D
    }

    fun getIS_EMPLOYMENT_EDUCATION(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (person.employment == Employment.EDUCATION).D
    }

    fun getIS_EMPLOYMENT_FULLTIME(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (person.employment == Employment.FULLTIME).D
    }

    fun getIS_EMPLOYMENT_PARTTIME(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (person.employment == Employment.PARTTIME).D
    }

    fun getIS_EMPLOYMENT_MARGINAL(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (person.employment == Employment.MARGINAL).D
    }

    fun getHAS_COMMUTER_TICKET(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (person.hasCommuterTicket).D
    }

    fun getCARS_PER_ADULT(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return if ((person.household.cars.size).toDouble() >= (
                person.household.members.count {
                    it.age >= 18
                }
                )
        ) {
            1.0
        } else {
            0.0
        }
    }

    fun getHOUSEHOLD_ECONOMICAL_STATUS(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        // TODO another insanity, the original implementation in transmove maps to the code number
        return person.household.economicStatus.code.toDouble()
    }

    fun getIS_UMLAND(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        // TODO this is real code from transmove, apparently the extenral id is used to determine umland
        // return (Integer.parseInt(category.getId().getExternalId()) > 1900) ? 1.0 : 0.0;
        // it is also the only time the (aptly named /s) category Location is used in the helper. I dont understand why
        // this Location should be passed ever
        // return if ((category> 1900)) 1.0 else 0.0

        return umlands(category).D
    }

    fun getDISTANCE(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return impedance.distance(origin, destination, car).toDouble(distanceUnit)
    }

    fun getATTRACTIVITY(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return attractiveness.attractivenessFor(destination.requireZone().id, nextActivity.type)
    }

    fun getPARKDRUCK(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return iGetParkdruck(destination)
    }

    fun iGetParkdruck(destination: Location): Double {
        // TODO this is a temporary hack to avoid cluttering of the destination choice by domination through parkdruck
        return 0.0
//        // TODO this value is insane, is the calculation correct?
//        val attractivity =
//            attractiveness.attractivenessFor(
//                destination.zone.id,
//                LegacyActivityType.WORK
//            ) + attractiveness.attractivenessFor(
//                destination.zone.id,
//                LegacyActivityType.PRIVATE_VISIT
//            )
//
//        if (0 == destination.zone.parkingPlaces) {
//            return if (1e-6 > abs(attractivity)) 0.0 else 999.0
//        }
//        return attractivity / destination.zone.parkingPlaces.toDouble()
    }

    fun getTRAVEL_TIME_PUBLICTRANSPORT(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return publicTransport.tTime(origin, destination, time)
    }

    fun getTRAVEL_COST_PUBLICTRANSPORT(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return publicTransport.tCost(origin, destination, time)
    }

    fun getACCESS_TIME_PUBLICTRANSPORT(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return aTime(origin, destination, time)
    }

    fun getEGRESS_TIME_PUBLICTRANSPORT(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return eTime(origin, destination, time)
    }

    fun getIS_ACTIVITY_TYPE_WORK(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (nextActivity.type == LegacyActivityType.WORK).D
    }

    fun getIS_ACTIVITY_TYPE_BUSINESS(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (nextActivity.type == LegacyActivityType.BUSINESS).D
    }

    val leisures = listOf(
        LegacyActivityType.LEISURE,
        LegacyActivityType.LEISURE_INDOOR,
        LegacyActivityType.LEISURE_OUTDOOR,
        LegacyActivityType.LEISURE_OTHER,
        LegacyActivityType.LEISURE_WALK,
        LegacyActivityType.LEISURE_SIGHTSEEING,
        LegacyActivityType.PRIVATE_VISIT,
    )

    fun getACTIVITY_TYPE_IS_LEISURE(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (nextActivity.type in leisures).D
    }

    fun getIS_ACTIVITY_TYPE_SERVICE(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (nextActivity.type == LegacyActivityType.SERVICE).D
    }

    fun getTRAVEL_TIME_PEDESTRIAN(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return pedestrian.tTime(origin, destination, time)
    }

    fun getTRAVEL_TIME_BIKE(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return bike.tTime(origin, destination, time)
    }

    private fun toTime(mode: Mode, origin: Location, destination: Location, time: Time) =
        impedance.duration(origin, destination, mode, time).toDouble(durationUnit)

    private fun aoTime(mode: Mode, origin: Location, destination: Location, time: Time) = 0.0 // TODO remove

    private fun eoTime(mode: Mode, origin: Location, destination: Location, time: Time) = 0.0 // TODO remove

    private fun toCost(mode: Mode, origin: Location, destination: Location, time: Time) =
        impedance.cost(origin, destination, mode, time).toDouble(currencyUnit)

    private fun Mode.tTime(origin: Location, destination: Location, time: Time) =
        impedance.duration(origin, destination, this, time).toDouble(durationUnit)

    private fun aTime(origin: Location, destination: Location, time: Time) = 0.0 // TODO remove

    private fun eTime(origin: Location, destination: Location, time: Time) = 0.0 // TODO remove

    private fun Mode.tCost(origin: Location, destination: Location, time: Time) =
        impedance.cost(origin, destination, this, time).toDouble(currencyUnit)

//    fun getAVAIL_PKW(
//        category: Location,
//        person: Person,
//        origin: Location,
//        destination: Location,
//        nextActivity: StationaryAction,
//        time: AbsoluteTime,
//        randomNumber: Double
//    ): Double {
//        return availabilities(destination, modeMap[CAR).D
//    }
//
//    fun getAVAIL_MF(
//        category: Location,
//        person: Person,
//        origin: Location,
//        destination: Location,
//        nextActivity: StationaryAction,
//        time: AbsoluteTime,
//        randomNumber: Double
//    ): Double {
//        return availabilities(destination, modeMap[PASSENGER).D
//    }

    fun getTRAVEL_TIME_CAR(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return car.tTime(origin, destination, time)
    }

    fun getTRAVEL_COST_CAR(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return car.tCost(origin, destination, time)
    }

    fun getACCESS_TIME_CAR(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return aTime(origin, destination, time)
    }

    fun getEGRESS_TIME_CAR(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return eTime(origin, destination, time)
    }

    fun getACTIVITY_TYPE_IS_HOME(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return (nextActivity.type == LegacyActivityType.HOME).D
    }

    fun getTRAVEL_TIME_PUBLICTRANSPORT_FIX(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return publicTransport.calculateFixed(person, destination, ::toTime, time)
    }

    fun getTRAVEL_COST_PUBLICTRANSPORT_FIX(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return publicTransport.calculateFixed(person, destination, ::toCost, time)
    }

    private inline fun Mode.calculateFixed(
        person: Person,
        destination: Location,
        functor: Mode.(Location, Location, Time) -> Double,
        time: Time,
    ): Double {
        return (person.nextFixedActivity()?.location ?: person.household.location).let {
            this.functor(destination, it, time)
        }
    }

    fun getACCESS_TIME_PUBLICTRANSPORT_FIX( // TODO remove
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return publicTransport.calculateFixed(person, destination, ::aoTime, time)
    }

    fun getEGRESS_TIME_PUBLICTRANSPORT_FIX(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return publicTransport.calculateFixed(person, destination, ::eoTime, time)
    }

    fun getPARKDRUCK_FIX(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return person.nextFixedActivity()?.let { iGetParkdruck(it.location) } ?: 0.0
    }

    fun getTRAVEL_TIME_PEDESTRIAN_FIX(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return pedestrian.calculateFixed(person, destination, ::toTime, time)
    }

    fun getTRAVEL_TIME_BIKE_FIX(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return bike.calculateFixed(person, destination, ::toTime, time)
    }

    fun getTRAVEL_TIME_CAR_FIX(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return car.calculateFixed(person, destination, ::toTime, time)
    }

    fun getTRAVEL_COST_CAR_FIX(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return car.calculateFixed(person, destination, ::toCost, time)
    }

    fun getACCESS_TIME_CAR_FIX(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return car.calculateFixed(person, destination, ::aoTime, time)
    }

    fun getEGRESS_TIME_CAR_FIX(
        category: Location,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        time: AbsoluteTime,
        randomNumber: Double
    ): Double {
        return car.calculateFixed(person, destination, ::eoTime, time)
    }
}

@Suppress("ComplexInterface")
interface IDestinationParameters {
    val asc_fuss: Double
    val asc_mf: Double
    val asc_oev: Double
    val asc_pkw: Double
    val asc_rad: Double
    val b_0_1: Double
    val b_1_2: Double
    val b_arb_on_fuss: Double
    val b_arb_on_mf: Double
    val b_arb_on_oev: Double
    val b_arb_on_pkw: Double
    val b_arb_on_rad: Double
    val b_attr: Double
    val b_cost_oev: Double
    val b_cost_pkw: Double
    val b_dienst_on_fuss: Double
    val b_dienst_on_mf: Double
    val b_dienst_on_oev: Double
    val b_dienst_on_pkw: Double
    val b_dienst_on_rad: Double
    val b_freizeit_on_fuss: Double
    val b_freizeit_on_oev: Double
    val b_freizeit_on_pkw: Double
    val b_freizeit_on_rad: Double
    val b_home_on_mf: Double
    val b_logsum_drive: Double
    val b_logsum_drive_fix: Double
    val b_logsum_pt_active: Double
    val b_logsum_pt_active_fix: Double
    val b_park_oev: Double
    val b_parken: Double
    val b_service_on_fuss: Double
    val b_service_on_mf: Double
    val b_service_on_oev: Double
    val b_service_on_pkw: Double
    val b_service_on_rad: Double
    val b_tt_fuss: Double
    val b_tt_mf_taxi: Double
    val b_tt_oev: Double
    val b_tt_pkw: Double
    val b_tt_rad: Double
    val b_zuab_oev: Double
    val b_zuab_pkw: Double
    val elasticity_park_oev: Double
    val elasticity_parken: Double
    val max_attractivity: Double
    val shift_age_2_on_attr: Double
    val shift_age_2_on_logsum_drive: Double
    val shift_age_2_on_logsum_drive_fix: Double
    val shift_age_2_on_logsum_pt_active: Double
    val shift_age_2_on_logsum_pt_active_fix: Double
    val shift_age_3_on_attr: Double
    val shift_age_3_on_logsum_drive: Double
    val shift_age_3_on_logsum_drive_fix: Double
    val shift_age_3_on_logsum_pt_active: Double
    val shift_age_3_on_logsum_pt_active_fix: Double
    val shift_age_4_on_attr: Double
    val shift_age_4_on_logsum_drive: Double
    val shift_age_4_on_logsum_drive_fix: Double
    val shift_age_4_on_logsum_pt_active: Double
    val shift_age_4_on_logsum_pt_active_fix: Double
    val shift_age_56_on_attr: Double
    val shift_age_56_on_logsum_drive: Double
    val shift_age_56_on_logsum_drive_fix: Double
    val shift_age_56_on_logsum_pt_active: Double
    val shift_age_56_on_logsum_pt_active_fix: Double
    val shift_age_78_on_attr: Double
    val shift_age_78_on_logsum_drive: Double
    val shift_age_78_on_logsum_drive_fix: Double
    val shift_age_78_on_logsum_pt_active: Double
    val shift_age_78_on_logsum_pt_active_fix: Double
    val shift_arb_on_attr: Double
    val shift_arb_on_logsum_drive: Double
    val shift_arb_on_logsum_drive_fix: Double
    val shift_arb_on_logsum_pt_active: Double
    val shift_arb_on_logsum_pt_active_fix: Double
    val shift_b_0_1_on_attr: Double
    val shift_b_1_2_on_attr: Double
    val shift_carav_on_attr: Double
    val shift_carav_on_logsum_drive: Double
    val shift_carav_on_logsum_drive_fix: Double
    val shift_carav_on_logsum_pt_active: Double
    val shift_carav_on_logsum_pt_active_fix: Double
    val shift_educ_on_attr: Double
    val shift_educ_on_logsum_drive: Double
    val shift_educ_on_logsum_drive_fix: Double
    val shift_educ_on_logsum_pt_active: Double
    val shift_educ_on_logsum_pt_active_fix: Double
    val shift_high_inc_on_attr: Double
    val shift_high_inc_on_logsum_drive: Double
    val shift_high_inc_on_logsum_drive_fix: Double
    val shift_high_inc_on_logsum_pt_active: Double
    val shift_high_inc_on_logsum_pt_active_fix: Double
    val shift_uml_on_attr: Double
    val shift_uml_on_logsum_drive: Double
    val shift_uml_on_logsum_drive_fix: Double
    val shift_uml_on_logsum_pt_active: Double
    val shift_uml_on_logsum_pt_active_fix: Double
    val shift_zk_on_attr: Double
    val shift_zk_on_logsum_drive: Double
    val shift_zk_on_logsum_drive_fix: Double
    val shift_zk_on_logsum_pt_active: Double
    val shift_zk_on_logsum_pt_active_fix: Double
}
