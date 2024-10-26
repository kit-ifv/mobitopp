package usecases.choicemodels

import datastructure.StationaryAction
import domain.data.Person
import domain.enums.Mode
import domain.location.Metrics
import domain.location.ZoneLocation
import usecases.AttractivenessModel
import kotlin.math.max
import kotlin.math.pow

@Suppress(
    "VariableNaming",
    "MagicNumber",
    "LongMethod",
    "LargeClass",
    "MaxLineLength",
    "MaximumLineLength",
    "UnusedPrivateProperty"
)
class GeneratedHcUtilityFunction(
    attractivities: AttractivenessModel,
    logitParameters: ModeChoiceParameters = ModeChoiceParameters(),
    override val modes: ChoiceModelModes,
    helper: ModeChoiceHelperMNL = ModeChoiceHelperMNL(attractivities, modes),
) : IGeneratedHcUtilityFunction {
    private val asc_bs = logitParameters.asc_bs
    private val asc_cs_ff = logitParameters.asc_cs_ff
    private val asc_cs_sb = logitParameters.asc_cs_sb
    private val asc_escooter = logitParameters.asc_escooter
    private val asc_fuss = logitParameters.asc_fuss
    private val asc_mf = logitParameters.asc_mf
    private val asc_moia = logitParameters.asc_moia
    private val asc_oev = logitParameters.asc_oev
    private val asc_pkw = logitParameters.asc_pkw
    private val asc_rad = logitParameters.asc_rad
    private val asc_taxi = logitParameters.asc_taxi
    private val b_age_1_on_fuss = logitParameters.b_age_1_on_fuss
    private val b_age_1_on_mf = logitParameters.b_age_1_on_mf
    private val b_age_1_on_oev = logitParameters.b_age_1_on_oev
    private val b_age_1_on_rad = logitParameters.b_age_1_on_rad
    private val b_age_2_on_bs = logitParameters.b_age_2_on_bs
    private val b_age_2_on_cs_ff = logitParameters.b_age_2_on_cs_ff
    private val b_age_2_on_escooter = logitParameters.b_age_2_on_escooter
    private val b_age_2_on_fuss = logitParameters.b_age_2_on_fuss
    private val b_age_2_on_mf = logitParameters.b_age_2_on_mf
    private val b_age_2_on_oev = logitParameters.b_age_2_on_oev
    private val b_age_2_on_rad = logitParameters.b_age_2_on_rad
    private val b_age_2_on_taxi = logitParameters.b_age_2_on_taxi
    private val b_age_34_on_bs = logitParameters.b_age_34_on_bs
    private val b_age_34_on_escooter = logitParameters.b_age_34_on_escooter
    private val b_age_34_on_fuss = logitParameters.b_age_34_on_fuss
    private val b_age_34_on_moia = logitParameters.b_age_34_on_moia
    private val b_age_34_on_oev = logitParameters.b_age_34_on_oev
    private val b_age_34_on_rad = logitParameters.b_age_34_on_rad
    private val b_age_56_on_bs = logitParameters.b_age_56_on_bs
    private val b_age_56_on_cs_ff = logitParameters.b_age_56_on_cs_ff
    private val b_age_56_on_escooter = logitParameters.b_age_56_on_escooter
    private val b_age_56_on_moia = logitParameters.b_age_56_on_moia
    private val b_age_78_on_moia = logitParameters.b_age_78_on_moia
    private val b_arb_on_bs = logitParameters.b_arb_on_bs
    private val b_arb_on_fuss = logitParameters.b_arb_on_fuss
    private val b_arb_on_mf = logitParameters.b_arb_on_mf
    private val b_arb_on_moia = logitParameters.b_arb_on_moia
    private val b_arb_on_oev = logitParameters.b_arb_on_oev
    private val b_arb_on_pkw = logitParameters.b_arb_on_pkw
    private val b_arb_on_rad = logitParameters.b_arb_on_rad
    private val b_arb_on_taxi = logitParameters.b_arb_on_taxi
    private val b_arb_on_cs_ff = logitParameters.b_arb_on_cs_ff
    private val b_arb_on_tt_fuss = logitParameters.b_arb_on_tt_fuss
    private val b_arb_on_tt_mf = logitParameters.b_arb_on_tt_mf
    private val b_arb_on_tt_oev = logitParameters.b_arb_on_tt_oev
    private val b_arb_on_tt_pkw = logitParameters.b_arb_on_tt_pkw
    private val b_arb_on_tt_rad = logitParameters.b_arb_on_tt_rad
    private val b_cost = logitParameters.b_cost
    private val b_cost_pkw = logitParameters.b_cost_pkw
    private val b_taxi_on_cost = logitParameters.b_taxi_on_cost
    private val b_cs_ffmitgl_on_cs_ff = logitParameters.b_cs_ffmitgl_on_cs_ff
    private val b_dienst_on_bs = logitParameters.b_dienst_on_bs
    private val b_dienst_on_cost = logitParameters.b_dienst_on_cost
    private val b_dienst_on_fuss = logitParameters.b_dienst_on_fuss
    private val b_dienst_on_mf = logitParameters.b_dienst_on_mf
    private val b_dienst_on_moia = logitParameters.b_dienst_on_moia
    private val b_dienst_on_oev = logitParameters.b_dienst_on_oev
    private val b_dienst_on_rad = logitParameters.b_dienst_on_rad
    private val b_dienst_on_taxi = logitParameters.b_dienst_on_taxi
    private val b_dienst_on_cs_ff = logitParameters.b_dienst_on_cs_ff
    private val b_dienst_on_tt_fuss = logitParameters.b_dienst_on_tt_fuss
    private val b_dienst_on_tt_mf = logitParameters.b_dienst_on_tt_mf
    private val b_dienst_on_tt_moia = logitParameters.b_dienst_on_tt_moia
    private val b_dienst_on_tt_oev = logitParameters.b_dienst_on_tt_oev
    private val b_dienst_on_tt_rad = logitParameters.b_dienst_on_tt_rad
    private val b_dienst_on_tt_taxi = logitParameters.b_dienst_on_tt_taxi
    private val b_edu_on_fuss = logitParameters.b_edu_on_fuss
    private val b_edu_on_mf = logitParameters.b_edu_on_mf
    private val b_edu_on_moia = logitParameters.b_edu_on_moia
    private val b_edu_on_oev = logitParameters.b_edu_on_oev
    private val b_edu_on_pkw = logitParameters.b_edu_on_pkw
    private val b_edu_on_rad = logitParameters.b_edu_on_rad
    private val b_edu_on_cs_ff = logitParameters.b_edu_on_cs_ff
    private val b_evening_on_moia = logitParameters.b_evening_on_moia
    private val b_evening_on_taxi = logitParameters.b_evening_on_taxi
    private val b_beforemidnight_on_moia = logitParameters.b_beforemidnight_on_moia
    private val b_beforemidnight_on_taxi = logitParameters.b_beforemidnight_on_taxi
    private val b_aftermidnight_on_moia = logitParameters.b_aftermidnight_on_moia
    private val b_aftermidnight_on_taxi = logitParameters.b_aftermidnight_on_taxi
    private val b_is_fri_aftermidnight_on_moia = logitParameters.b_is_fri_aftermidnight_on_moia
    private val b_is_fri_beforemidnight_on_moia = logitParameters.b_is_fri_beforemidnight_on_moia
    private val b_is_sat_beforemidnight_on_moia = logitParameters.b_is_sat_beforemidnight_on_moia
    private val b_is_sat_aftermidnight_on_moia = logitParameters.b_is_sat_aftermidnight_on_moia
    private val b_is_sun_aftermidnight_on_moia = logitParameters.b_is_sun_aftermidnight_on_moia
    private val b_is_fri_aftermidnight_on_taxi = logitParameters.b_is_fri_aftermidnight_on_taxi
    private val b_is_fri_beforemidnight_on_taxi = logitParameters.b_is_fri_beforemidnight_on_taxi
    private val b_is_sat_beforemidnight_on_taxi = logitParameters.b_is_sat_beforemidnight_on_taxi
    private val b_is_sat_aftermidnight_on_taxi = logitParameters.b_is_sat_aftermidnight_on_taxi
    private val b_is_sun_aftermidnight_on_taxi = logitParameters.b_is_sun_aftermidnight_on_taxi
    private val b_evening_on_cost = logitParameters.b_evening_on_cost
    private val b_night_on_cost = logitParameters.b_night_on_cost
    private val b_frau_on_bs = logitParameters.b_frau_on_bs
    private val b_frau_on_cs_ff = logitParameters.b_frau_on_cs_ff
    private val b_frau_on_escooter = logitParameters.b_frau_on_escooter
    private val b_frau_on_mf = logitParameters.b_frau_on_mf
    private val b_frau_on_oev = logitParameters.b_frau_on_oev
    private val b_frau_on_pkw = logitParameters.b_frau_on_pkw
    private val b_freizeit_on_bs = logitParameters.b_freizeit_on_bs
    private val b_freizeit_on_escooter = logitParameters.b_freizeit_on_escooter
    private val b_freizeit_on_fuss = logitParameters.b_freizeit_on_fuss
    private val b_freizeit_on_moia = logitParameters.b_freizeit_on_moia
    private val b_freizeit_on_oev = logitParameters.b_freizeit_on_oev
    private val b_freizeit_on_pkw = logitParameters.b_freizeit_on_pkw
    private val b_freizeit_on_rad = logitParameters.b_freizeit_on_rad
    private val b_freizeit_on_cs_ff = logitParameters.b_freizeit_on_cs_ff
    private val b_freizeit_on_tt_cs_ff = logitParameters.b_freizeit_on_tt_cs_ff
    private val b_freizeit_on_tt_fuss = logitParameters.b_freizeit_on_tt_fuss
    private val b_freizeit_on_tt_mf = logitParameters.b_freizeit_on_tt_mf
    private val b_freizeit_on_tt_pkw = logitParameters.b_freizeit_on_tt_pkw
    private val b_fs_on_escooter = logitParameters.b_fs_on_escooter
    private val b_fs_on_mf = logitParameters.b_fs_on_mf
    private val b_fs_on_moia = logitParameters.b_fs_on_moia
    private val b_fs_on_oev = logitParameters.b_fs_on_oev
    private val b_fs_on_taxi = logitParameters.b_fs_on_taxi
    private val b_konto_moia_on_moia = logitParameters.b_konto_moia_on_moia
    private val b_logsum_ab_oev = logitParameters.b_logsum_ab_oev
    private val b_logsum_zu_oev = logitParameters.b_logsum_zu_oev
    private val b_mode_before_oev = logitParameters.b_mode_before_oev
    private val b_mode_before_pkw = logitParameters.b_mode_before_pkw
    private val b_mode_before_rad = logitParameters.b_mode_before_rad
    private val b_mode_before_mf = logitParameters.b_mode_before_mf
    private val b_nutzer_123_moia_on_cost_moia = logitParameters.b_nutzer_123_moia_on_cost_moia
    private val b_nutzer_123_moia_on_moia = logitParameters.b_nutzer_123_moia_on_moia
    private val b_oekstat5_on_cost = logitParameters.b_oekstat5_on_cost
    private val b_oekstat5_on_cost_pkw = logitParameters.b_oekstat5_on_cost_pkw
    private val b_oekstat_on_moia = logitParameters.b_oekstat_on_moia
    private val b_oekstat_on_oev = logitParameters.b_oekstat_on_oev
    private val b_oekstat_on_pkw = logitParameters.b_oekstat_on_pkw
    private val b_oekstat_on_taxi = logitParameters.b_oekstat_on_taxi
    private val b_parkdruck_on_cs_ff = logitParameters.b_parkdruck_on_cs_ff
    private val b_parkdruck_on_oev = logitParameters.b_parkdruck_on_oev
    private val b_pkwimHH_on_bs = logitParameters.b_pkwimHH_on_bs
    private val b_pkwimHH_on_cs_ff = logitParameters.b_pkwimHH_on_cs_ff
    private val b_pkwimHH_on_escooter = logitParameters.b_pkwimHH_on_escooter
    private val b_pkwimHH_on_fuss = logitParameters.b_pkwimHH_on_fuss
    private val b_pkwimHH_on_mf = logitParameters.b_pkwimHH_on_mf
    private val b_pkwimHH_on_moia = logitParameters.b_pkwimHH_on_moia
    private val b_pkwimHH_on_oev = logitParameters.b_pkwimHH_on_oev
    private val b_pkwimHH_on_rad = logitParameters.b_pkwimHH_on_rad
    private val b_pkwimHH_on_taxi = logitParameters.b_pkwimHH_on_taxi
    private val b_service_on_bs = logitParameters.b_service_on_bs
    private val b_service_on_escooter = logitParameters.b_service_on_escooter
    private val b_service_on_fuss = logitParameters.b_service_on_fuss
    private val b_service_on_pkw = logitParameters.b_service_on_pkw
    private val b_service_on_mf = logitParameters.b_service_on_mf
    private val b_service_on_moia = logitParameters.b_service_on_moia
    private val b_service_on_oev = logitParameters.b_service_on_oev
    private val b_service_on_rad = logitParameters.b_service_on_rad
    private val b_service_on_taxi = logitParameters.b_service_on_taxi
    private val b_service_on_cs_ff = logitParameters.b_service_on_cs_ff
    private val b_shopping_on_escooter = logitParameters.b_shopping_on_escooter
    private val b_shopping_on_fuss = logitParameters.b_shopping_on_fuss
    private val b_shopping_on_moia = logitParameters.b_shopping_on_moia
    private val b_shopping_on_oev = logitParameters.b_shopping_on_oev
    private val b_shopping_on_pkw = logitParameters.b_shopping_on_pkw
    private val b_shopping_on_rad = logitParameters.b_shopping_on_rad
    private val b_shopping_on_taxi = logitParameters.b_shopping_on_taxi
    private val b_shopping_on_cs_ff = logitParameters.b_shopping_on_cs_ff
    private val b_tt_bs = logitParameters.b_tt_bs
    private val b_tt_escooter = logitParameters.b_tt_escooter
    private val b_tt_fuss = logitParameters.b_tt_fuss
    private val b_tt_mf = logitParameters.b_tt_mf
    private val b_tt_moia = logitParameters.b_tt_moia
    private val b_tt_oev = logitParameters.b_tt_oev
    private val b_tt_pkw = logitParameters.b_tt_pkw
    private val b_tt_rad = logitParameters.b_tt_rad
    private val b_tt_taxi = logitParameters.b_tt_taxi
    private val b_wt_moia = logitParameters.b_wt_moia
    private val b_night_on_wt = logitParameters.b_night_on_wt
    private val b_zeitkarte_on_bs = logitParameters.b_zeitkarte_on_bs
    private val b_zeitkarte_on_mf = logitParameters.b_zeitkarte_on_mf
    private val b_zeitkarte_on_oev = logitParameters.b_zeitkarte_on_oev
    private val b_zeitkarte_on_pkw = logitParameters.b_zeitkarte_on_pkw
    private val b_zeitkarte_on_rad = logitParameters.b_zeitkarte_on_rad
    private val b_zeitkarte_on_tt_oev = logitParameters.b_zeitkarte_on_tt_oev
    private val b_zu_bs = logitParameters.b_zu_bs
    private val b_zu_es = logitParameters.b_zu_es
    private val b_zuab_cs_ff = logitParameters.b_zuab_cs_ff
    private val b_zuab_moia = logitParameters.b_zuab_moia
    private val b_zuab_pkw = logitParameters.b_zuab_pkw
    private val elast_parkdruck_on_cs_ff = logitParameters.elast_parkdruck_on_cs_ff
    private val elast_parkdruck_on_oev = logitParameters.elast_parkdruck_on_oev
    private val factor_cost_bev = logitParameters.factor_cost_bev
    private val factor_cost_phev = logitParameters.factor_cost_phev
    private val b_0_1_on_moia = logitParameters.b_0_1_on_moia
    private val b_1_2_on_moia = logitParameters.b_1_2_on_moia
    private val b_0_1_on_pkw = logitParameters.b_0_1_on_pkw
    private val b_0_1_on_taxi = logitParameters.b_0_1_on_taxi
    private val b_1_2_on_fuss = logitParameters.b_1_2_on_fuss
    private val b_leisure_travel_on_moia = logitParameters.b_leisure_travel_on_moia
    private val b_business_travel_on_moia = logitParameters.b_business_travel_on_moia

    private val helper: ModeChoiceHelperMNL = helper

    override fun calculateU_fuss(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "Pedestrian"

        val HOUSEHOLD_NUMBER_OF_OWNED_CARS_value: Double = helper.getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_WORK_value: Double = helper.getACTIVITY_TYPE_IS_WORK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_EDUCATION_value: Double = helper.getACTIVITY_TYPE_IS_EDUCATION(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double = helper.getIS_ACTIVITY_TYPE_BUSINESS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_LEISURE_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_SERVICE_value: Double = helper.getIS_ACTIVITY_TYPE_SERVICE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_SHOPPING_value: Double = helper.getACTIVITY_TYPE_IS_SHOPPING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val AGE_value: Double = helper.getAGE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_PEDESTRIAN_value: Double = helper.getTRAVEL_TIME_PEDESTRIAN(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val DISTANCE_value: Double =
            helper.getDISTANCE(category, person, origin, destination, nextActivity, impedance, randomNumber)

        return (
            (asc_fuss) + (
                b_pkwimHH_on_fuss * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value
                ) +
                (b_arb_on_fuss * ACTIVITY_TYPE_IS_WORK_value) +
                (b_edu_on_fuss * ACTIVITY_TYPE_IS_EDUCATION_value) +
                (b_dienst_on_fuss * IS_ACTIVITY_TYPE_BUSINESS_value) +
                (b_freizeit_on_fuss * ACTIVITY_TYPE_IS_LEISURE_value) +
                (b_service_on_fuss * IS_ACTIVITY_TYPE_SERVICE_value) +
                (b_shopping_on_fuss * ACTIVITY_TYPE_IS_SHOPPING_value) +
                (b_age_1_on_fuss * (if ((AGE_value <= 17.0)) 1.0 else 0.0)) +
                (b_age_2_on_fuss * (if ((17.0 < AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) +
                (b_age_34_on_fuss * (if ((29.0 < AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) +
                (b_1_2_on_fuss * (if ((1.0 < DISTANCE_value && DISTANCE_value <= 2.0)) 1.0 else 0.0)) +
                (
                    (
                        b_tt_fuss + b_arb_on_tt_fuss * ACTIVITY_TYPE_IS_WORK_value +
                            b_freizeit_on_tt_fuss * ACTIVITY_TYPE_IS_LEISURE_value +
                            b_dienst_on_tt_fuss * IS_ACTIVITY_TYPE_BUSINESS_value
                        ) * (TRAVEL_TIME_PEDESTRIAN_value.coerceAtMost(1000.0))
                    )
            )
    }

    override fun calculateU_rad(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "Bike"

        val HAS_COMMUTER_TICKET_value: Double = helper.getHAS_COMMUTER_TICKET(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HOUSEHOLD_NUMBER_OF_OWNED_CARS_value: Double = helper.getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_WORK_value: Double = helper.getACTIVITY_TYPE_IS_WORK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_EDUCATION_value: Double = helper.getACTIVITY_TYPE_IS_EDUCATION(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double = helper.getIS_ACTIVITY_TYPE_BUSINESS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_LEISURE_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_SERVICE_value: Double = helper.getIS_ACTIVITY_TYPE_SERVICE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_SHOPPING_value: Double = helper.getACTIVITY_TYPE_IS_SHOPPING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_PREV_MODE_BIKE_value: Double = helper.getIS_PREV_MODE_BIKE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val AGE_value: Double = helper.getAGE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_BIKE_value: Double = helper.getTRAVEL_TIME_BIKE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_BIKE_FIX_value: Double = helper.getTRAVEL_TIME_BIKE_FIX(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TT_BIKE_MAX = max(TRAVEL_TIME_BIKE_FIX_value, TRAVEL_TIME_BIKE_value)

        return (
            (asc_rad) + (b_zeitkarte_on_rad * HAS_COMMUTER_TICKET_value) + (b_pkwimHH_on_rad * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value) + (b_arb_on_rad * ACTIVITY_TYPE_IS_WORK_value) + (b_edu_on_rad * ACTIVITY_TYPE_IS_EDUCATION_value) + (b_dienst_on_rad * IS_ACTIVITY_TYPE_BUSINESS_value) + (b_freizeit_on_rad * ACTIVITY_TYPE_IS_LEISURE_value) + (b_service_on_rad * IS_ACTIVITY_TYPE_SERVICE_value) + (b_shopping_on_rad * ACTIVITY_TYPE_IS_SHOPPING_value) + (b_mode_before_rad * IS_PREV_MODE_BIKE_value) + (b_age_1_on_rad * (if ((AGE_value <= 17.0)) 1.0 else 0.0)) + (b_age_2_on_rad * (if ((17.0 < AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) + (b_age_34_on_rad * (if ((29.0 < AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) + (
                (b_tt_rad + b_arb_on_tt_rad * ACTIVITY_TYPE_IS_WORK_value + b_dienst_on_tt_rad * IS_ACTIVITY_TYPE_BUSINESS_value) * (
                    TT_BIKE_MAX.coerceAtMost(
                        1000.0
                    )
                    )
                )
            )
    }

    override fun calculateU_pkw(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "Car"

        val HAS_COMMUTER_TICKET_value: Double = helper.getHAS_COMMUTER_TICKET(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_LEISURE_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_SHOPPING_value: Double = helper.getACTIVITY_TYPE_IS_SHOPPING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_EDUCATION_value: Double = helper.getACTIVITY_TYPE_IS_EDUCATION(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_PREV_MODE_CAR_value: Double = helper.getIS_PREV_MODE_CAR(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_SERVICE_value: Double = helper.getIS_ACTIVITY_TYPE_SERVICE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_FEMALE_value: Double = helper.getIS_FEMALE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HOUSEHOLD_ECONOMICAL_STATUS_value: Double = helper.getHOUSEHOLD_ECONOMICAL_STATUS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_WORK_value: Double = helper.getACTIVITY_TYPE_IS_WORK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_CAR_value: Double = helper.getTRAVEL_TIME_CAR(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH_value: Double = helper.getIS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_COST_CAR_value: Double = helper.getTRAVEL_COST_CAR(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACCESS_TIME_CAR_value: Double = helper.getACCESS_TIME_CAR(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val EGRESS_TIME_CAR_value: Double = helper.getEGRESS_TIME_CAR(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_CAR_FIX_value: Double = helper.getTRAVEL_TIME_CAR_FIX(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_COST_CAR_FIX_value: Double = helper.getTRAVEL_COST_CAR_FIX(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_BEV_value: Double = helper.getIS_BEV(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_PHEV_value: Double = helper.getIS_PHEV(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val DISTANCE_value: Double =
            helper.getDISTANCE(category, person, origin, destination, nextActivity, impedance, randomNumber)

        val TT_CAR_MAX = max(TRAVEL_TIME_CAR_FIX_value, TRAVEL_TIME_CAR_value)
        val TC_CAR_MAX = max(TRAVEL_COST_CAR_FIX_value, TRAVEL_COST_CAR_value)

        return (
            (asc_pkw) + (b_zeitkarte_on_pkw * HAS_COMMUTER_TICKET_value) +
                (b_freizeit_on_pkw * ACTIVITY_TYPE_IS_LEISURE_value) +
                (b_arb_on_pkw * ACTIVITY_TYPE_IS_WORK_value) +
                (b_shopping_on_pkw * ACTIVITY_TYPE_IS_SHOPPING_value) +
                (b_service_on_pkw * IS_ACTIVITY_TYPE_SERVICE_value) +
                (b_edu_on_pkw * ACTIVITY_TYPE_IS_EDUCATION_value) +
                (b_mode_before_pkw * IS_PREV_MODE_CAR_value) +
                (b_frau_on_pkw * IS_FEMALE_value) +
                (b_oekstat_on_pkw * HOUSEHOLD_ECONOMICAL_STATUS_value) +
                (b_0_1_on_pkw * (if ((0.0 < DISTANCE_value && DISTANCE_value <= 1.0)) 1.0 else 0.0)) +
                (
                    (
                        b_tt_pkw + b_arb_on_tt_pkw * ACTIVITY_TYPE_IS_WORK_value +
                            b_freizeit_on_tt_pkw * ACTIVITY_TYPE_IS_LEISURE_value
                        ) *
                        (TT_CAR_MAX.coerceAtMost(1000.0))
                    ) +
                (
                    (b_cost_pkw + b_oekstat5_on_cost_pkw * IS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH_value) *
                        (
                            TC_CAR_MAX.coerceAtMost(1000.0) *
                                (1 - (1 - factor_cost_bev) * IS_BEV_value + (1 - factor_cost_phev) * IS_PHEV_value)
                            )
                    ) +
                (b_zuab_pkw * (ACCESS_TIME_CAR_value + EGRESS_TIME_CAR_value + 3))
            )
    }

    override fun calculateU_mf(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "Passenger"

        val HAS_COMMUTER_TICKET_value: Double = helper.getHAS_COMMUTER_TICKET(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HOUSEHOLD_NUMBER_OF_OWNED_CARS_value: Double = helper.getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_WORK_value: Double = helper.getACTIVITY_TYPE_IS_WORK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_EDUCATION_value: Double = helper.getACTIVITY_TYPE_IS_EDUCATION(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double = helper.getIS_ACTIVITY_TYPE_BUSINESS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_PREV_MODE_CARP_value: Double = helper.getIS_PREV_MODE_CARP(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_SERVICE_value: Double = helper.getIS_ACTIVITY_TYPE_SERVICE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HAS_DRIVING_LICENSE_value: Double = helper.getHAS_DRIVING_LICENSE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_FEMALE_value: Double = helper.getIS_FEMALE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val AGE_value: Double = helper.getAGE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_LEISURE_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_PASSENGER_value: Double = helper.getTRAVEL_TIME_PASSENGER(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        return (
            (asc_mf) +
                (b_zeitkarte_on_mf * HAS_COMMUTER_TICKET_value) +
                (b_pkwimHH_on_mf * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value) +
                (b_arb_on_mf * ACTIVITY_TYPE_IS_WORK_value) +
                (b_edu_on_mf * ACTIVITY_TYPE_IS_EDUCATION_value) +
                (b_dienst_on_mf * IS_ACTIVITY_TYPE_BUSINESS_value) +
                (b_service_on_mf * IS_ACTIVITY_TYPE_SERVICE_value) +
                (b_fs_on_mf * HAS_DRIVING_LICENSE_value) +
                (b_frau_on_mf * IS_FEMALE_value) +
                (b_mode_before_mf * IS_PREV_MODE_CARP_value) +
                (b_age_1_on_mf * (if ((AGE_value <= 17.0)) 1.0 else 0.0)) +
                (b_age_2_on_mf * (if ((17.0 < AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) +
                (
                    (
                        b_tt_mf + b_arb_on_tt_mf * ACTIVITY_TYPE_IS_WORK_value +
                            b_freizeit_on_tt_mf * ACTIVITY_TYPE_IS_LEISURE_value +
                            b_dienst_on_tt_mf * IS_ACTIVITY_TYPE_BUSINESS_value
                        ) *
                        (TRAVEL_TIME_PASSENGER_value.coerceAtMost(1000.0))
                    )
            )
    }

    override fun calculateU_oev(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "PublicTransport"

        val HAS_COMMUTER_TICKET_value: Double = helper.getHAS_COMMUTER_TICKET(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HOUSEHOLD_NUMBER_OF_OWNED_CARS_value: Double = helper.getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_WORK_value: Double = helper.getACTIVITY_TYPE_IS_WORK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_EDUCATION_value: Double = helper.getACTIVITY_TYPE_IS_EDUCATION(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double = helper.getIS_ACTIVITY_TYPE_BUSINESS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_LEISURE_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_SERVICE_value: Double = helper.getIS_ACTIVITY_TYPE_SERVICE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_SHOPPING_value: Double = helper.getACTIVITY_TYPE_IS_SHOPPING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_PREV_MODE_PUBLICTRANSPORT_value: Double = helper.getIS_PREV_MODE_PUBLICTRANSPORT(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HAS_DRIVING_LICENSE_value: Double = helper.getHAS_DRIVING_LICENSE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_FEMALE_value: Double = helper.getIS_FEMALE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val AGE_value: Double = helper.getAGE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HOUSEHOLD_ECONOMICAL_STATUS_value: Double = helper.getHOUSEHOLD_ECONOMICAL_STATUS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val LOGSUM_ACCESS_PUBLICTRANSPORT_value: Double = helper.getLOGSUM_ACCESS_PUBLICTRANSPORT(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val LOGSUM_EGRESS_PUBLICTRANSPORT_value: Double = helper.getLOGSUM_EGRESS_PUBLICTRANSPORT(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val PARKDRUCK_value: Double = helper.getPARKDRUCK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_PUBLICTRANSPORT_value: Double = helper.getTRAVEL_TIME_PUBLICTRANSPORT(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH_value: Double = helper.getIS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_COST_PUBLICTRANSPORT_value: Double = helper.getTRAVEL_COST_PUBLICTRANSPORT(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        return (
            (asc_oev) +
                (b_zeitkarte_on_oev * HAS_COMMUTER_TICKET_value) +
                (b_pkwimHH_on_oev * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value) +
                (b_arb_on_oev * ACTIVITY_TYPE_IS_WORK_value) +
                (b_edu_on_oev * ACTIVITY_TYPE_IS_EDUCATION_value) +
                (b_dienst_on_oev * IS_ACTIVITY_TYPE_BUSINESS_value) +
                (b_freizeit_on_oev * ACTIVITY_TYPE_IS_LEISURE_value) +
                (b_service_on_oev * IS_ACTIVITY_TYPE_SERVICE_value) +
                (b_shopping_on_oev * ACTIVITY_TYPE_IS_SHOPPING_value) +
                (b_mode_before_oev * IS_PREV_MODE_PUBLICTRANSPORT_value) +
                (b_fs_on_oev * HAS_DRIVING_LICENSE_value) +
                (b_frau_on_oev * IS_FEMALE_value) +
                (b_age_1_on_oev * (if ((AGE_value <= 17.0)) 1.0 else 0.0)) +
                (b_age_2_on_oev * (if ((17.0 < AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) +
                (b_age_34_on_oev * (if ((29.0 < AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) +
                (b_oekstat_on_oev * HOUSEHOLD_ECONOMICAL_STATUS_value) +
                (b_logsum_zu_oev * LOGSUM_ACCESS_PUBLICTRANSPORT_value) +
                (b_logsum_ab_oev * LOGSUM_EGRESS_PUBLICTRANSPORT_value) +
                (b_parkdruck_on_oev * (PARKDRUCK_value.pow(elast_parkdruck_on_oev))) +
                (
                    (
                        b_tt_oev +
                            b_zeitkarte_on_tt_oev * HAS_COMMUTER_TICKET_value +
                            b_arb_on_tt_oev * ACTIVITY_TYPE_IS_WORK_value +
                            b_dienst_on_tt_oev * IS_ACTIVITY_TYPE_BUSINESS_value
                        ) *
                        (
                            (
                                if (TRAVEL_TIME_PUBLICTRANSPORT_value <= 0.0) {
                                    (1000.0)
                                } else {
                                    TRAVEL_TIME_PUBLICTRANSPORT_value.coerceAtMost(1000.0)
                                }
                                )
                            )
                    ) +
                (
                    (
                        (
                            b_cost +
                                b_oekstat5_on_cost * IS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH_value +
                                b_dienst_on_cost * IS_ACTIVITY_TYPE_BUSINESS_value
                            ) *
                            (TRAVEL_COST_PUBLICTRANSPORT_value.coerceAtMost(1000.0))
                        ) * (if ((HAS_COMMUTER_TICKET_value == 0.0)) 1.0 else 0.0)
                    )
            )
    }

    override fun calculateU_bs(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "BikeSharing"

        val HAS_COMMUTER_TICKET_value: Double = helper.getHAS_COMMUTER_TICKET(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HOUSEHOLD_NUMBER_OF_OWNED_CARS_value: Double = helper.getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_WORK_value: Double = helper.getACTIVITY_TYPE_IS_WORK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double = helper.getIS_ACTIVITY_TYPE_BUSINESS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_LEISURE_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_SERVICE_value: Double = helper.getIS_ACTIVITY_TYPE_SERVICE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_FEMALE_value: Double = helper.getIS_FEMALE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val AGE_value: Double = helper.getAGE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_BIKESHARING_value: Double = helper.getTRAVEL_TIME_BIKESHARING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH_value: Double = helper.getIS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_COST_BIKESHARING_value: Double = helper.getTRAVEL_COST_BIKESHARING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACCESS_TIME_BIKESHARING_value: Double = helper.getACCESS_TIME_BIKESHARING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        return (
            (asc_bs) +
                (b_zeitkarte_on_bs * HAS_COMMUTER_TICKET_value) +
                (b_pkwimHH_on_bs * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value) +
                (b_arb_on_bs * ACTIVITY_TYPE_IS_WORK_value) +
                (b_dienst_on_bs * IS_ACTIVITY_TYPE_BUSINESS_value) +
                (b_freizeit_on_bs * ACTIVITY_TYPE_IS_LEISURE_value) +
                (b_service_on_bs * IS_ACTIVITY_TYPE_SERVICE_value) +
                (b_frau_on_bs * IS_FEMALE_value) +
                (b_age_2_on_bs * (if ((17.0 < AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) +
                (b_age_34_on_bs * (if ((29.0 < AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) +
                (b_age_56_on_bs * (if ((49.0 < AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) +
                (b_tt_bs * (TRAVEL_TIME_BIKESHARING_value.coerceAtMost(1000.0))) +
                (
                    (
                        b_cost +
                            b_oekstat5_on_cost * IS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH_value +
                            b_dienst_on_cost * IS_ACTIVITY_TYPE_BUSINESS_value
                        ) *
                        (TRAVEL_COST_BIKESHARING_value.coerceAtMost(1000.0))
                    ) +
                (b_zu_bs * ACCESS_TIME_BIKESHARING_value)
            )
    }

    override fun calculateU_moia(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "RidePooling"

        val HOUSEHOLD_NUMBER_OF_OWNED_CARS_value: Double = helper.getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val MEMBERSHIP_ACTIVE_MOIA_value: Double = helper.getMEMBERSHIP_ACTIVE_MOIA(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_WORK_value: Double = helper.getACTIVITY_TYPE_IS_WORK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_EDUCATION_value: Double = helper.getACTIVITY_TYPE_IS_EDUCATION(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double = helper.getIS_ACTIVITY_TYPE_BUSINESS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_LEISURE_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_SERVICE_value: Double = helper.getIS_ACTIVITY_TYPE_SERVICE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_HOME_value: Double = helper.getACTIVITY_TYPE_IS_HOME(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_SHOPPING_value: Double = helper.getACTIVITY_TYPE_IS_SHOPPING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HAS_DRIVING_LICENSE_value: Double = helper.getHAS_DRIVING_LICENSE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val AGE_value: Double = helper.getAGE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HOUSEHOLD_ECONOMICAL_STATUS_value: Double = helper.getHOUSEHOLD_ECONOMICAL_STATUS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_RIDE_POOLING_value: Double = helper.getTRAVEL_TIME_RIDE_POOLING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_EVENING_value: Double = helper.getIS_EVENING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_AFTERMIDNIGHT_value: Double = helper.getIS_AFTERMIDNIGHT(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_BEFOREMIDNIGHT_value: Double = helper.getIS_BEFOREMIDNIGHT(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_FRIDAY_value: Double = helper.getIS_FRIDAY(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_SATURDAY_value: Double = helper.getIS_SATURDAY(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_SUNDAY_value: Double = helper.getIS_SUNDAY(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH_value: Double = helper.getIS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_COST_RIDE_POOLING_value: Double = helper.getTRAVEL_COST_RIDE_POOLING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACCESS_TIME_RIDE_POOLING_value: Double = helper.getACCESS_TIME_RIDE_POOLING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val EGRESS_TIME_RIDE_POOLING_value: Double = helper.getEGRESS_TIME_RIDE_POOLING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val WAITING_TIME_MOIA_value: Double = helper.getWAITING_TIME_MOIA(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val DISTANCE_value: Double =
            helper.getDISTANCE(category, person, origin, destination, nextActivity, impedance, randomNumber)

        val ACTIVITY_TYPE_IS_LEISURE_TRAVEL_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE_TRAVEL(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_BUSINESS_TRAVEL_value: Double = helper.getACTIVITY_TYPE_IS_BUSINESS_TRAVEL(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        return (
            (asc_moia) +
                (b_pkwimHH_on_moia * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value) +
                (
                    b_0_1_on_moia *
                        (if ((0.0 < DISTANCE_value && DISTANCE_value <= 1.0)) 1.0 else 0.0)
                    ) +
                (b_1_2_on_moia * (if ((1.0 < DISTANCE_value && DISTANCE_value <= 2.0)) 1.0 else 0.0)) +
                (b_evening_on_moia * IS_EVENING_value) +
                (b_beforemidnight_on_moia * IS_BEFOREMIDNIGHT_value) +
                (b_aftermidnight_on_moia * IS_AFTERMIDNIGHT_value) +
                (b_is_fri_beforemidnight_on_moia * IS_FRIDAY_value * IS_BEFOREMIDNIGHT_value) +
                (b_is_fri_aftermidnight_on_moia * IS_FRIDAY_value * IS_AFTERMIDNIGHT_value) +
                (b_is_sat_beforemidnight_on_moia * IS_SATURDAY_value * IS_BEFOREMIDNIGHT_value) +
                (b_is_sat_aftermidnight_on_moia * IS_SATURDAY_value * IS_AFTERMIDNIGHT_value) +
                (b_is_sun_aftermidnight_on_moia * IS_SUNDAY_value * IS_AFTERMIDNIGHT_value) +
                (b_nutzer_123_moia_on_moia * MEMBERSHIP_ACTIVE_MOIA_value) +
                (b_konto_moia_on_moia) +
                (b_arb_on_moia * ACTIVITY_TYPE_IS_WORK_value) +
                (b_edu_on_moia * ACTIVITY_TYPE_IS_EDUCATION_value) +
                (b_dienst_on_moia * IS_ACTIVITY_TYPE_BUSINESS_value) +
                (b_freizeit_on_moia * ACTIVITY_TYPE_IS_LEISURE_value) +
                (b_service_on_moia * IS_ACTIVITY_TYPE_SERVICE_value) +
                (b_shopping_on_moia * ACTIVITY_TYPE_IS_SHOPPING_value) +
                (b_leisure_travel_on_moia * ACTIVITY_TYPE_IS_LEISURE_TRAVEL_value) +
                (b_business_travel_on_moia * ACTIVITY_TYPE_IS_BUSINESS_TRAVEL_value) +
                (b_fs_on_moia * HAS_DRIVING_LICENSE_value) +
                (b_age_34_on_moia * (if ((29.0 < AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) +
                (b_age_56_on_moia * (if ((49.0 < AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) +
                (b_age_78_on_moia * (if ((AGE_value > 69.0)) 1.0 else 0.0)) +
                (b_oekstat_on_moia * HOUSEHOLD_ECONOMICAL_STATUS_value) +
                (
                    (b_tt_moia + b_dienst_on_tt_moia * IS_ACTIVITY_TYPE_BUSINESS_value) *
                        (TRAVEL_TIME_RIDE_POOLING_value.coerceAtMost(1000.0))
                    ) +
                (
                    (
                        b_cost +
                            b_evening_on_cost * IS_EVENING_value +
                            b_night_on_cost * (IS_AFTERMIDNIGHT_value + IS_BEFOREMIDNIGHT_value) +
                            b_oekstat5_on_cost * IS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH_value +
                            b_dienst_on_cost * IS_ACTIVITY_TYPE_BUSINESS_value +
                            b_nutzer_123_moia_on_cost_moia * MEMBERSHIP_ACTIVE_MOIA_value
                        ) *
                        (TRAVEL_COST_RIDE_POOLING_value.coerceAtMost(1000.0))
                    ) +
                (
                    b_zuab_moia *
                        (ACCESS_TIME_RIDE_POOLING_value + EGRESS_TIME_RIDE_POOLING_value)
                    ) +
                (
                    (
                        b_wt_moia +
                            b_night_on_wt * (IS_AFTERMIDNIGHT_value + IS_BEFOREMIDNIGHT_value)
                        ) * WAITING_TIME_MOIA_value
                    )
            )
    }

    override fun calculateU_escooter(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "E_Scooter"

        val HOUSEHOLD_NUMBER_OF_OWNED_CARS_value: Double = helper.getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_LEISURE_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_SERVICE_value: Double = helper.getIS_ACTIVITY_TYPE_SERVICE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_SHOPPING_value: Double = helper.getACTIVITY_TYPE_IS_SHOPPING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HAS_DRIVING_LICENSE_value: Double = helper.getHAS_DRIVING_LICENSE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_FEMALE_value: Double = helper.getIS_FEMALE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val AGE_value: Double = helper.getAGE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_E_SCOOTER_value: Double = helper.getTRAVEL_TIME_E_SCOOTER(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double = helper.getIS_ACTIVITY_TYPE_BUSINESS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_COST_E_SCOOTER_value: Double = helper.getTRAVEL_COST_E_SCOOTER(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACCESS_TIME_E_SCOOTER_value: Double = helper.getACCESS_TIME_E_SCOOTER(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        return (
            (asc_escooter) +
                (b_pkwimHH_on_escooter * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value) +
                (b_freizeit_on_escooter * ACTIVITY_TYPE_IS_LEISURE_value) +
                (b_service_on_escooter * IS_ACTIVITY_TYPE_SERVICE_value) +
                (b_shopping_on_escooter * ACTIVITY_TYPE_IS_SHOPPING_value) +
                (b_fs_on_escooter * HAS_DRIVING_LICENSE_value) +
                (b_frau_on_escooter * IS_FEMALE_value) +
                (b_age_2_on_escooter * (if ((17.0 < AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) +
                (b_age_34_on_escooter * (if ((29.0 < AGE_value && AGE_value <= 49.0)) 1.0 else 0.0)) +
                (b_age_56_on_escooter * (if ((49.0 < AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) +
                (b_tt_escooter * (TRAVEL_TIME_E_SCOOTER_value.coerceAtMost(1000.0))) +
                (
                    (
                        b_cost +
                            b_oekstat5_on_cost * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value +
                            b_dienst_on_cost * IS_ACTIVITY_TYPE_BUSINESS_value
                        ) *
                        (TRAVEL_COST_E_SCOOTER_value.coerceAtMost(1000.0))
                    ) +
                (b_zu_es * ACCESS_TIME_E_SCOOTER_value)
            )
    }

    override fun calculateU_cs_ff(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "Carsharing_FreeFloating"

        val HOUSEHOLD_NUMBER_OF_OWNED_CARS_value: Double = helper.getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_FEMALE_value: Double = helper.getIS_FEMALE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val AGE_value: Double = helper.getAGE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val PARKDRUCK_value: Double = helper.getPARKDRUCK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_WORK_value: Double = helper.getACTIVITY_TYPE_IS_WORK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_EDUCATION_value: Double = helper.getACTIVITY_TYPE_IS_EDUCATION(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double = helper.getIS_ACTIVITY_TYPE_BUSINESS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_LEISURE_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_SERVICE_value: Double = helper.getIS_ACTIVITY_TYPE_SERVICE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_SHOPPING_value: Double = helper.getACTIVITY_TYPE_IS_SHOPPING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_CARSHARING_FREE_value: Double = helper.getTRAVEL_TIME_CARSHARING_FREE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_COST_CARSHARING_FREE_value: Double = helper.getTRAVEL_COST_CARSHARING_FREE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACCESS_TIME_CARSHARING_value: Double = helper.getACCESS_TIME_CARSHARING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val EGRESS_TIME_CARSHARING_value: Double = helper.getEGRESS_TIME_CARSHARING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        return (
            (asc_cs_ff) +
                (b_pkwimHH_on_cs_ff * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value) +
                (b_cs_ffmitgl_on_cs_ff) +
                (b_frau_on_cs_ff * IS_FEMALE_value) +
                (b_age_2_on_cs_ff * (if ((17.0 < AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) +
                (b_age_56_on_cs_ff * (if ((49.0 < AGE_value && AGE_value <= 69.0)) 1.0 else 0.0)) +
                (b_arb_on_cs_ff * ACTIVITY_TYPE_IS_WORK_value) +
                (b_edu_on_cs_ff * ACTIVITY_TYPE_IS_EDUCATION_value) +
                (b_dienst_on_cs_ff * IS_ACTIVITY_TYPE_BUSINESS_value) +
                (b_freizeit_on_cs_ff * ACTIVITY_TYPE_IS_LEISURE_value) +
                (b_service_on_cs_ff * IS_ACTIVITY_TYPE_SERVICE_value) +
                (b_shopping_on_cs_ff * ACTIVITY_TYPE_IS_SHOPPING_value) +
                (
                    b_parkdruck_on_cs_ff * (
                        PARKDRUCK_value.pow(
                            elast_parkdruck_on_cs_ff
                        )
                        )
                    ) +
                (
                    (
                        b_tt_pkw +
                            b_freizeit_on_tt_cs_ff * ACTIVITY_TYPE_IS_LEISURE_value
                        ) *
                        (TRAVEL_TIME_CARSHARING_FREE_value.coerceAtMost(1000.0))
                    ) +
                (
                    (
                        b_cost +
                            b_oekstat5_on_cost * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value +
                            b_dienst_on_cost * IS_ACTIVITY_TYPE_BUSINESS_value
                        ) *
                        (TRAVEL_COST_CARSHARING_FREE_value.coerceAtMost(1000.0))
                    ) +
                (b_zuab_cs_ff * (ACCESS_TIME_CARSHARING_value + EGRESS_TIME_CARSHARING_value))
            )
    }

    override fun calculateU_cs_sb(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "Carsharing_Station"

        val TRAVEL_TIME_CARSHARING_STATION_value: Double = helper.getTRAVEL_TIME_CARSHARING_STATION(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HOUSEHOLD_NUMBER_OF_OWNED_CARS_value: Double = helper.getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double = helper.getIS_ACTIVITY_TYPE_BUSINESS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_COST_CARSHARING_STATION_value: Double = helper.getTRAVEL_COST_CARSHARING_STATION(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        return (
            (asc_cs_sb) +
                (
                    b_tt_pkw *
                        (TRAVEL_TIME_CARSHARING_STATION_value.coerceAtMost(1000.0))
                    ) + (
                    (
                        b_cost +
                            b_oekstat5_on_cost * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value +
                            b_dienst_on_cost * IS_ACTIVITY_TYPE_BUSINESS_value
                        ) *
                        (TRAVEL_COST_CARSHARING_STATION_value.coerceAtMost(1000.0))
                    )
            )
    }

    override fun calculateU_taxi(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        val category = "Taxi"

        val HOUSEHOLD_NUMBER_OF_OWNED_CARS_value: Double = helper.getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_WORK_value: Double = helper.getACTIVITY_TYPE_IS_WORK(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_BUSINESS_value: Double = helper.getIS_ACTIVITY_TYPE_BUSINESS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_LEISURE_value: Double = helper.getACTIVITY_TYPE_IS_LEISURE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_ACTIVITY_TYPE_SERVICE_value: Double = helper.getIS_ACTIVITY_TYPE_SERVICE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_HOME_value: Double = helper.getACTIVITY_TYPE_IS_HOME(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val ACTIVITY_TYPE_IS_SHOPPING_value: Double = helper.getACTIVITY_TYPE_IS_SHOPPING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HAS_DRIVING_LICENSE_value: Double = helper.getHAS_DRIVING_LICENSE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val AGE_value: Double = helper.getAGE(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val HOUSEHOLD_ECONOMICAL_STATUS_value: Double = helper.getHOUSEHOLD_ECONOMICAL_STATUS(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_TIME_TAXI_value: Double = helper.getTRAVEL_TIME_TAXI(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_EVENING_value: Double = helper.getIS_EVENING(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_AFTERMIDNIGHT_value: Double = helper.getIS_AFTERMIDNIGHT(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_FRIDAY_value: Double = helper.getIS_FRIDAY(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_SATURDAY_value: Double = helper.getIS_SATURDAY(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_SUNDAY_value: Double = helper.getIS_SUNDAY(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val IS_BEFOREMIDNIGHT_value: Double = helper.getIS_BEFOREMIDNIGHT(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val TRAVEL_COST_TAXI_value: Double = helper.getTRAVEL_COST_TAXI(
            category, person, origin, destination, previousActivity, nextActivity, choiceSet, impedance, randomNumber
        )

        val DISTANCE_value: Double =
            helper.getDISTANCE(category, person, origin, destination, nextActivity, impedance, randomNumber)

        return (
            (asc_taxi) +
                (b_pkwimHH_on_taxi * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value) +
                (b_0_1_on_taxi * (if ((0.0 < DISTANCE_value && DISTANCE_value <= 1.0)) 1.0 else 0.0)) +
                (b_evening_on_taxi * IS_EVENING_value) +
                (b_beforemidnight_on_taxi * IS_BEFOREMIDNIGHT_value) +
                (b_aftermidnight_on_taxi * IS_AFTERMIDNIGHT_value) +
                (b_is_fri_beforemidnight_on_taxi * IS_FRIDAY_value * IS_BEFOREMIDNIGHT_value) +
                (b_is_fri_aftermidnight_on_taxi * IS_FRIDAY_value * IS_AFTERMIDNIGHT_value) +
                (b_is_sat_beforemidnight_on_taxi * IS_SATURDAY_value * IS_BEFOREMIDNIGHT_value) +
                (b_is_sat_aftermidnight_on_taxi * IS_SATURDAY_value * IS_AFTERMIDNIGHT_value) +
                (b_is_sun_aftermidnight_on_taxi * IS_SUNDAY_value * IS_AFTERMIDNIGHT_value) +
                (b_arb_on_taxi * ACTIVITY_TYPE_IS_WORK_value) +
                (b_dienst_on_taxi * IS_ACTIVITY_TYPE_BUSINESS_value) +
                (b_shopping_on_taxi * ACTIVITY_TYPE_IS_SHOPPING_value) +
                (b_service_on_taxi * IS_ACTIVITY_TYPE_SERVICE_value) +
                (b_fs_on_taxi * HAS_DRIVING_LICENSE_value) +
                (b_age_2_on_taxi * (if ((17.0 < AGE_value && AGE_value <= 29.0)) 1.0 else 0.0)) +
                (b_oekstat_on_taxi * HOUSEHOLD_ECONOMICAL_STATUS_value) +
                (
                    (
                        b_tt_taxi +
                            b_dienst_on_tt_taxi * IS_ACTIVITY_TYPE_BUSINESS_value
                        ) *
                        (TRAVEL_TIME_TAXI_value.coerceAtMost(1000.0))
                    ) +
                (
                    (
                        b_cost +
                            b_taxi_on_cost +
                            b_evening_on_cost * (IS_AFTERMIDNIGHT_value + IS_BEFOREMIDNIGHT_value) +
                            b_oekstat5_on_cost * HOUSEHOLD_NUMBER_OF_OWNED_CARS_value +
                            b_dienst_on_cost * IS_ACTIVITY_TYPE_BUSINESS_value
                        ) *
                        (TRAVEL_COST_TAXI_value.coerceAtMost(1000.0))
                    )
            )
    }
}

@Suppress(
    "LongParameterList",
    "FunctionNaming",
    "TooManyFunctions"
) // I agree with detekt on this one, good thing we are reengineering and perhaps moving away from this
interface IGeneratedHcUtilityFunction {
    val modes: ChoiceModelModes
    fun calculateU_fuss(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double

    fun calculateU_rad(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double

    fun calculateU_pkw(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double

    fun calculateU_mf(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double

    fun calculateU_oev(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double

    fun calculateU_bs(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double

    fun calculateU_moia(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double

    fun calculateU_escooter(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double

    fun calculateU_cs_ff(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double

    fun calculateU_cs_sb(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double

    fun calculateU_taxi(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double
}
