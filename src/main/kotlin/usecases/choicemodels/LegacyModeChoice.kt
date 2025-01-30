@file:Suppress(
    "UnusedParameter",
    "MagicNumber",
    "MaxLineLength",
    "FunctionMaxLength",
    "FunctionNaming",
    "FunctionOnlyReturningConstant",
    "UnusedPrivateProperty",
    "CyclomaticComplexMethod",
    "TooManyFunctions",
    "CognitiveComplexMethod",
    "LargeClass",
    "LongMethod",
    "LongParameterList"
)

package usecases.choicemodels

import datastructure.Activity
import datastructure.StationaryAction
import domain.data.EconomicStatus
import domain.data.EngineType
import domain.data.Person
import domain.data.Sex
import domain.data.lastTransportMode
import domain.enums.Mode
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import domain.location.Metrics
import units.CurrencyUnit
import units.DistanceUnit
import usecases.AttractivenessModel
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import java.time.DayOfWeek
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

operator fun Duration.rem(other: Duration): Duration =
    (this.inWholeSeconds % other.inWholeSeconds).toDuration(DurationUnit.SECONDS)

fun Activity.isFixed(purposes: ChoiceModelPurposes) = type == purposes.work || type in purposes.educationTypes
fun Person.nextFixedActivity(purposes: ChoiceModelPurposes) = schedule.activities().firstOrNull {
    it.isFixed(purposes) && it.location != LOCATIONUNKNOWN
}

@Suppress("VariableNaming", "MagicNumber") // Sadly naming and magic number remains for the legacy mode choice
class ModeChoiceParameters {

    val asc_fuss: Double = 0 - 0.1 - 0.25
    val asc_rad: Double = -3.34401676623963 - 0.5 - 0.05 - 0.2
    val asc_pkw: Double = -4.69347301935537 + 1.7 + 0.1 + 0.1
    val asc_mf: Double = -5.42649021160325 - 1.5 - 0.5 + 0.3 + 0.2
    val asc_oev: Double = -5.1223586833361 - 0.4 + 0.27
    val asc_taxi: Double = -6.26874836363272 + 1
    val asc_bs: Double = -7.87371999724625 + 1.1 + 1.15
    val asc_cs_ff: Double = -8.69976053689462 - 2.1 - 0.1
    val asc_cs_sb: Double = -7.79427402396165 + 1
    val asc_escooter: Double = -6.33113485902361 + 0.9 + 0.08
    val asc_moia: Double = -4.98883914602145 - 0.6
    val b_tt_fuss: Double = -0.148247995923713
    val b_tt_rad: Double = -0.132222310507272 - 0.05
    val b_tt_pkw: Double = -0.0397735554726612 - 0.02
    val b_tt_mf: Double = -0.0406043488494389 + 0.005
    val b_tt_oev: Double = -0.0332164435548626 + 0.01
    val b_tt_taxi: Double = -0.0337950092387434
    val b_tt_bs: Double = -0.111596101439312
    val b_tt_escooter: Double = -0.104258043365681
    val b_tt_moia: Double = -0.0558270784857267 + 0.019
    val b_cost: Double = -0.173579310377872
    val b_cost_pkw: Double = -0.0681515897245829 - 0.03
    val b_taxi_on_cost: Double = 0.02
    val b_evening_on_cost: Double = 0.173579310377872 / 3
    val b_night_on_cost: Double = 0.173579310377872 / 2
    val b_wt_moia: Double = -0.135230327718274 - 0.03
    val b_0_1_on_moia: Double = -3.0
    val b_1_2_on_moia: Double = -1.0
    val b_0_1_on_pkw: Double = -1.0
    val b_0_1_on_taxi: Double = -2.0
    val b_1_2_on_fuss: Double = -1.0
    val b_night_on_wt: Double = 0.135230327718274 / 2
    val b_beforemidnight_on_moia: Double = 0.5
    val b_beforemidnight_on_taxi: Double = 0.0
    val b_aftermidnight_on_moia: Double = 0.5
    val b_aftermidnight_on_taxi: Double = 0.0
    val b_evening_on_moia: Double = 0.4
    val b_evening_on_taxi: Double = 0.0
    val b_leisure_travel_on_moia: Double = 6.0
    val b_business_travel_on_moia: Double = 6.0
    val b_is_fri_aftermidnight_on_moia: Double = 1.3 * 1.2
    val b_is_fri_beforemidnight_on_moia: Double = 0.4 * 1.4
    val b_is_sat_beforemidnight_on_moia: Double = 0.7 * 1.2
    val b_is_sat_aftermidnight_on_moia: Double = 2 * 1.5
    val b_is_sun_aftermidnight_on_moia: Double = 5.0
    val b_is_fri_aftermidnight_on_taxi: Double = 2.0
    val b_is_fri_beforemidnight_on_taxi: Double = 1.0
    val b_is_sat_beforemidnight_on_taxi: Double = 1.0
    val b_is_sat_aftermidnight_on_taxi: Double = 2.0
    val b_is_sun_aftermidnight_on_taxi: Double = 2.0
    val b_zuab_pkw: Double = -0.0886209594184093
    val b_zuab_moia: Double = -0.187405202006469
    val b_zuab_cs_ff: Double = -0.113059664780004
    val b_zu_bs: Double = -0.253781573210888
    val b_zu_es: Double = -0.253781573210888
    val b_parkdruck_on_oev: Double = 0.569244759402515
    val b_parkdruck_on_cs_ff: Double = 0.99381505624896
    val elast_parkdruck_on_oev: Double = 0.498718831912856
    val elast_parkdruck_on_cs_ff: Double = 0.422310707928408
    val b_zeitkarte_on_rad: Double = -0.138496699678098
    val b_zeitkarte_on_pkw: Double = -0.42095211929704
    val b_zeitkarte_on_mf: Double = 0.0980079892366568
    val b_zeitkarte_on_oev: Double = 1.40430480389687
    val b_zeitkarte_on_bs: Double = 0.265046300898315
    val b_zeitkarte_on_tt_oev: Double = -0.0139162098577208
    val b_cs_ffmitgl_on_cs_ff: Double = 2.12500983359484
    val b_pkwimHH_on_fuss: Double = -0.596362099668231
    val b_pkwimHH_on_rad: Double = -0.854549090872297
    val b_pkwimHH_on_mf: Double = -0.094827093407709
    val b_pkwimHH_on_oev: Double = -1.07827861934364
    val b_pkwimHH_on_taxi: Double = -0.816141508020774
    val b_pkwimHH_on_bs: Double = -0.519466510039712
    val b_pkwimHH_on_escooter: Double = -0.291609361059158
    val b_pkwimHH_on_cs_ff: Double = -0.93467367132673
    val b_pkwimHH_on_moia: Double = -0.522964009216794
    val b_fs_on_mf: Double = -0.663019506336669
    val b_fs_on_oev: Double = -0.717703886473169
    val b_fs_on_taxi: Double = -1.24853468668945
    val b_fs_on_escooter: Double = -0.73583050105007
    val b_fs_on_moia: Double = -0.995775183512996
    val b_nutzer_123_moia_on_moia: Double = 0.920837012352522
    val b_konto_moia_on_moia: Double = 0.952788891671551
    val b_nutzer_123_moia_on_cost_moia: Double = 0.0357690383757927
    val b_frau_on_pkw: Double = -0.0530617111718515
    val b_frau_on_mf: Double = 0.893117885972587
    val b_frau_on_oev: Double = 0.140430498472284
    val b_frau_on_bs: Double = -0.742975367550372
    val b_frau_on_cs_ff: Double = -0.282109812615457
    val b_frau_on_escooter: Double = -0.925131277333908
    val b_age_1_on_fuss: Double = 2.34054180179764 - 0.7
    val b_age_2_on_fuss: Double = -0.285913206494658 - 0.3
    val b_age_34_on_fuss: Double = -0.326256906559177 + 0.1
    val b_age_1_on_rad: Double = 2.78514456752499
    val b_age_2_on_rad: Double = 0.389316612655132
    val b_age_34_on_rad: Double = 0.113391623780271
    val b_age_1_on_mf: Double = 3.11291433770843 + 0.3
    val b_age_2_on_mf: Double = 0.226300538038417 - 0.8
    val b_age_1_on_oev: Double = 2.84820590066083 - 0.3
    val b_age_2_on_oev: Double = 0.364277898178094
    val b_age_34_on_oev: Double = 0.0843166645310694
    val b_age_2_on_taxi: Double = -0.458010316471613
    val b_age_2_on_bs: Double = 0.880034329871052
    val b_age_34_on_bs: Double = 0.378296037637105
    val b_age_56_on_bs: Double = -0.203289071442423
    val b_age_2_on_cs_ff: Double = 0.231608297097211
    val b_age_56_on_cs_ff: Double = -0.167441511452748
    val b_age_2_on_escooter: Double = 1.30960251657394
    val b_age_34_on_escooter: Double = 0.320663130991353
    val b_age_56_on_escooter: Double = -0.392923883952931
    val b_age_34_on_moia: Double = -0.133870668781963
    val b_age_56_on_moia: Double = 0.240651982840271
    val b_age_78_on_moia: Double = 0.839643293408915
    val b_freizeit_on_fuss: Double = 1.16599124418 - 1.2
    val b_freizeit_on_rad: Double = 0.857464481463703 + 0.6
    val b_freizeit_on_pkw: Double = -0.135539830154819 - 0.3
    val b_freizeit_on_oev: Double = 0.782269682255019 - 0.2
    val b_freizeit_on_escooter: Double = 0.495988304864423
    val b_freizeit_on_bs: Double = 1.01847035480728
    val b_freizeit_on_moia: Double = 1.58945260978742
    val b_freizeit_on_cs_ff: Double = 1.0
    val b_freizeit_on_tt_fuss: Double = 0.0273293409702819 + 0.04
    val b_freizeit_on_tt_pkw: Double = 0.0224560635046835
    val b_freizeit_on_tt_mf: Double = 0.0136562607953001 + 0.01
    val b_freizeit_on_tt_cs_ff: Double = 0.0346206400712123
    val b_arb_on_fuss: Double = -0.503090102175567 + 1.2
    val b_arb_on_rad: Double = 1.7477080826499 + 1
    val b_arb_on_mf: Double = -1.39149377320426 - 0.7
    val b_arb_on_oev: Double = 1.19610336106273 + 0.55
    val b_arb_on_taxi: Double = -1.54402580548127
    val b_arb_on_bs: Double = 0.755684897761409
    val b_arb_on_moia: Double = 0.296408626063083 - 0.3
    val b_arb_on_pkw: Double = -0.9
    val b_arb_on_cs_ff: Double = -3.0
    val b_arb_on_tt_fuss: Double = -0.045632595478184 * 0
    val b_arb_on_tt_rad: Double = -0.0403054361662421 + 0.02
    val b_arb_on_tt_pkw: Double = 0.0198554464250517 + 0.01
    val b_arb_on_tt_mf: Double = 0.00658159982603899
    val b_arb_on_tt_oev: Double = -0.01921914022135 - 0.01
    val b_dienst_on_fuss: Double = -0.784166770512213 - 1.1
    val b_dienst_on_rad: Double = 1.27524641844584 - 2
    val b_dienst_on_mf: Double = -0.42509634025244 + 2
    val b_dienst_on_oev: Double = 1.7543148414908 - 1.1
    val b_dienst_on_taxi: Double = 1.82041837389995 - 1.5 - 0.5 - 0.5
    val b_dienst_on_bs: Double = 0.50393908569628 - 0.2
    val b_dienst_on_moia: Double = 1.19739398327784 - 0.6
    val b_dienst_on_cs_ff: Double = -0.5
    val b_dienst_on_tt_fuss: Double = -0.0324546622169199
    val b_dienst_on_tt_rad: Double = -0.0448785360310769 - 0.02
    val b_dienst_on_tt_mf: Double = -0.0168757023596564
    val b_dienst_on_tt_oev: Double = -0.0527001295272004 + 0.04
    val b_dienst_on_tt_moia: Double = -0.0165983651233786
    val b_dienst_on_tt_taxi: Double = -0.0165983651233786
    val b_dienst_on_cost: Double = 0.0840900269251931 - 0.02
    val b_edu_on_fuss: Double = 0.3
    val b_edu_on_pkw: Double = -2.0
    val b_edu_on_rad: Double = 1.3
    val b_edu_on_mf: Double = -1.5
    val b_edu_on_oev: Double = 0.5
    val b_edu_on_moia: Double = 0.0
    val b_edu_on_cs_ff: Double = 0.0
    val b_service_on_fuss: Double = -1.76177714595929 - 0.8
    val b_service_on_rad: Double = -1.71732162997153
    val b_service_on_mf: Double = -1.08327529817799 + 0.3
    val b_service_on_oev: Double = -0.727680174176148 - 2
    val b_service_on_pkw: Double = 0.9
    val b_service_on_escooter: Double = -1.02218083324273 - 0.5
    val b_service_on_bs: Double = -0.441718268397706 - 1
    val b_service_on_moia: Double = 0.280743524700044 - 2
    val b_service_on_taxi: Double = -5.0
    val b_service_on_cs_ff: Double = -0.3
    val b_shopping_on_fuss: Double = -1.05505852162273 + 0.25
    val b_shopping_on_rad: Double = -0.00714067693903813 + 0.15
    val b_shopping_on_pkw: Double = 0.484848310471716 - 0.3
    val b_shopping_on_oev: Double = -0.68430769614711 + 0.6
    val b_shopping_on_taxi: Double = -1.97851919243732 + 0.3
    val b_shopping_on_escooter: Double = -0.87569267923033
    val b_shopping_on_moia: Double = -1.0
    val b_shopping_on_cs_ff: Double = 0.0
    val b_oekstat_on_pkw: Double = -0.0925723881400436 - 0.1
    val b_oekstat_on_oev: Double = -0.0849228845892844 - 0.08
    val b_oekstat_on_taxi: Double = 0.100296146534807
    val b_oekstat_on_moia: Double = -0.0747033564230432
    val b_oekstat5_on_cost: Double = 0.029633074216897
    val b_oekstat5_on_cost_pkw: Double = 0.0276716356674532
    val b_mode_before_rad: Double = 3.3046316124107 - 1
    val b_mode_before_pkw: Double = 2.39983908693686
    val b_mode_before_oev: Double = 1.84784910323771
    val b_mode_before_mf: Double = 2.829225
    val b_logsum_zu_oev: Double = 0.171249144782522
    val b_logsum_ab_oev: Double = 0.224664410320345
    val lambda_newmob: Double = 0.667968298558931
    val lambda_miv: Double = 0.695349329824102
    val lambda_oevrad: Double = 0.859470096437733
    val lambda_taxi: Double = 0.518389277631693

    val lambda_root = 1.0
    val alpha0_newmob: Double = -0.322082733283521
    val alpha0_miv: Double = -0.52330589402679
    val alpha0_oevrad: Double = 0.0
    val alpha0_taxi: Double = -1.67862502648719
    val b_members_on_alpha0_newmob: Double = 1.91942000587443
    val b_members_on_alpha0_miv: Double = -0.0924006817329263
    val b_members_on_alpha0_oevrad: Double = 0.0
    val b_members_on_alpha0_taxi: Double = 2.46354165735022
    val factor_cost_bev: Double = 1.0
    val factor_cost_phev: Double = 1.0
}

inline val Boolean.D get() = if (this) 1.0 else 0.0

class ModeChoiceHelperMNL(
    val attractivities: AttractivenessModel,
    val modes: ChoiceModelModes,
    val purposes: ChoiceModelPurposes
) {
    val car = modes.car
    val bike = modes.bike
    val pedestrian = modes.pedestrian
    val publicTransport = modes.publicTransport
    val passenger = modes.passenger
    val bikesharing = modes.bikeSharing
    val ridePooling = modes.ridePooling
    val carSharingFree = modes.carSharingFree
    val carSharingStation = modes.carSharingStation
    val taxi = modes.taxi
    val eScooter = modes.eScooter

    private val currencyUnit = CurrencyUnit.EUROS
    val distanceUnit = DistanceUnit.KILOMETERS
    val durationUnit = DurationUnit.MINUTES
    private val moiaString = "Moia_an_member"

    fun getHOUSEHOLD_NUMBER_OF_OWNED_CARS(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return person.household.cars.size.toDouble()
    }

    fun getACTIVITY_TYPE_IS_WORK(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (nextActivity.type == purposes.work).D
    }

    val educations = purposes.educationTypes
//        listOf( // TOPDO independent of legacy activity type
//        LegacyActivityType.EDUCATION,
//        LegacyActivityType.EDUCATION_PRIMARY,
//        LegacyActivityType.EDUCATION_SECONDARY,
//        LegacyActivityType.EDUCATION_TERTIARY,
//        LegacyActivityType.EDUCATION_OCCUP
//    )

    fun getACTIVITY_TYPE_IS_EDUCATION(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (nextActivity.type in educations).D
    }

    fun getIS_ACTIVITY_TYPE_BUSINESS(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (nextActivity.type == purposes.business).D
    }

    val leisures = purposes.leisureTypes
//        listOf(
//        LegacyActivityType.LEISURE,
//        LegacyActivityType.LEISURE_INDOOR,
//        LegacyActivityType.LEISURE_OUTDOOR,
//        LegacyActivityType.LEISURE_OTHER,
//        LegacyActivityType.LEISURE_WALK,
//        LegacyActivityType.LEISURE_SIGHTSEEING,
//        LegacyActivityType.PRIVATE_VISIT,
//    )

    fun getACTIVITY_TYPE_IS_LEISURE(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (nextActivity.type in leisures).D
    }

    fun getIS_ACTIVITY_TYPE_SERVICE(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (nextActivity.type == purposes.service).D
    }

    val shoppings = purposes.shoppingTypes.toList()
//        listOf(
//
//        LegacyActivityType.SHOPPING,
//        LegacyActivityType.SHOPPING_DAILY,
//        LegacyActivityType.SHOPPING_OTHER,
//        LegacyActivityType.PRIVATE_BUSINESS,
//    )

    fun getACTIVITY_TYPE_IS_SHOPPING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (nextActivity.type in shoppings).D
    }

    fun getAGE(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return person.age.toDouble()
    }

    fun getTRAVEL_TIME_PEDESTRIAN(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            pedestrian,
            previousActivity.endTime
        )
            .toDouble(durationUnit)
    }

    fun getDISTANCE(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        nextActivity: StationaryAction,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.distance(origin, destination, car).toDouble(distanceUnit)
    }

    fun getHAS_COMMUTER_TICKET(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (person.hasCommuterTicket).D
    }

    fun getIS_PREV_MODE_BIKE(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (person.lastTransportMode(previousActivity) == bike).D
    }

    fun getTRAVEL_TIME_BIKE(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            bike,
            previousActivity.endTime
        ).toDouble(durationUnit)
    }

    fun getTRAVEL_TIME_BIKE_FIX(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO figure out which logic is better, because a person in reengineering might not have a fixed location
        return person.nextFixedActivity(purposes)?.let {
            impedance.duration(destination, it.location, bike, previousActivity.endTime)
                .toDouble(durationUnit)
        } ?: 0.0
    }

    fun getIS_PREV_MODE_CAR(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (person.lastTransportMode(previousActivity) == car).D
    }

    fun getIS_FEMALE(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (person.sex == Sex.FEMALE).D
    }

    fun getHOUSEHOLD_ECONOMICAL_STATUS(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO this is insane, why would the encoding of the household number ever be used in the utility function
        return person.household.economicStatus.code.toDouble()
    }

    fun getTRAVEL_TIME_CAR(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            car,
            previousActivity.endTime
        ).toDouble(durationUnit)
    }

    fun getIS_HOUSEHOLD_ECONOMICAL_STATUS_VERY_HIGH(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (person.household.economicStatus == EconomicStatus.VERY_HIGH).D
    }

    fun getTRAVEL_COST_CAR(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.cost(
            origin,
            destination,
            car,
            previousActivity.endTime
        ).toDouble(currencyUnit)
    }

    fun getACCESS_TIME_CAR(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return 0.0 // TODO remove
    }

    fun getEGRESS_TIME_CAR(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return 0.0 // TODO remove
    }

    fun getTRAVEL_TIME_CAR_FIX(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return person.nextFixedActivity(purposes)?.let {
            impedance.duration(destination, it.location, car, previousActivity.endTime)
                .toDouble(durationUnit)
        } ?: 0.0
    }

    fun getTRAVEL_COST_CAR_FIX(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return person.nextFixedActivity(purposes)?.let {
            impedance.cost(destination, it.location, car, previousActivity.endTime)
                .toDouble(currencyUnit)
        } ?: 0.0
    }

    fun getIS_BEV(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO no good way to get the car from the person
        return person.household.cars.firstOrNull {
            it.mainUser == person
        }?.let { it.engine.type == EngineType.ELECTRIC }?.D ?: 0.0
    }

    fun getIS_PHEV(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO no good way to get the car from the person
        return person.household.cars.firstOrNull {
            it.mainUser == person
        }?.let { it.engine.type == EngineType.HYBRID }?.D ?: 0.0
    }

    fun getIS_PREV_MODE_CARP(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (person.lastTransportMode(previousActivity) == passenger).D
    }

    fun getHAS_DRIVING_LICENSE(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return person.hasLicense.D
    }

    fun getTRAVEL_TIME_PASSENGER(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            passenger,
            previousActivity.endTime
        )
            .toDouble(durationUnit)
    }

    fun getIS_PREV_MODE_PUBLICTRANSPORT(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (person.lastTransportMode(previousActivity) == publicTransport).D
    }

    fun getLOGSUM_ACCESS_PUBLICTRANSPORT(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO figure out a way to implement this
        return 0.0
    }

    fun getLOGSUM_EGRESS_PUBLICTRANSPORT(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO figure out a way to implement this
        return 0.0
    }

    fun getPARKDRUCK(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO this value is insane, is the calculation correct?
        val attractivity =
            attractivities.attractivenessFor(destination.requireZone().id, purposes.work) +
                attractivities.attractivenessFor(destination.requireZone().id, purposes.privateVisit)

        if (0 == destination.requireZone().parkingPlaces) {
            return if (1e-6 > abs(attractivity)) 0.0 else 999.0
        }
        return attractivity / (destination.requireZone().parkingPlaces)
    }

    fun getTRAVEL_TIME_PUBLICTRANSPORT(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            publicTransport,
            previousActivity.endTime
        )
            .toDouble(durationUnit)
    }

    fun getTRAVEL_COST_PUBLICTRANSPORT(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.cost(
            origin,
            destination,
            publicTransport,
            previousActivity.endTime
        )
            .toDouble(currencyUnit)
    }

    fun getTRAVEL_TIME_BIKESHARING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            bikesharing,
            previousActivity.endTime
        )
            .toDouble(durationUnit)
    }

    fun getTRAVEL_COST_BIKESHARING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.cost(
            origin,
            destination,
            bikesharing,
            previousActivity.endTime
        )
            .toDouble(currencyUnit)
    }

    fun getACCESS_TIME_BIKESHARING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return 0.0 // TODO remove
    }

    fun getACTIVITY_TYPE_IS_HOME(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (nextActivity.type == purposes.home).D
    }

    fun getMEMBERSHIP_ACTIVE_MOIA(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return person.memberships.any { it.key.name == moiaString }.D
    }

    fun getTRAVEL_TIME_RIDE_POOLING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            ridePooling,
            previousActivity.endTime
        )
            .toDouble(durationUnit)
    }

    fun getIS_EVENING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO this is taken directly from the utility function,
        //  it should not be done this way,
        //  the helper function should get the current time
        return (previousActivity.endTime.hour in (17..<21)).D
    }

    fun getIS_AFTERMIDNIGHT(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO I also don't agree with the definition of after midnight
        return (previousActivity.endTime.hour < 5).D
    }

    fun getIS_BEFOREMIDNIGHT(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO yeah I don't agree with the definition of before midnight, please verify
        return (previousActivity.endTime.hour >= 21).D
    }

    fun getIS_FRIDAY(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO this is under the assumption that the simulation starts at monday 0:00:00
        return (previousActivity.endTime.weekDay == DayOfWeek.FRIDAY).D
    }

    fun getIS_SATURDAY(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO this is under the assumption that the simulation starts at monday 0:00:00
        return (previousActivity.endTime.weekDay == DayOfWeek.SATURDAY).D
    }

    fun getIS_SUNDAY(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO this is under the assumption that the simulation starts at monday 0:00:00
        return (previousActivity.endTime.weekDay == DayOfWeek.SUNDAY).D
    }

    fun getTRAVEL_COST_RIDE_POOLING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.cost(
            origin,
            destination,
            ridePooling,
            previousActivity.endTime
        )
            .toDouble(currencyUnit)
    }

    fun getACCESS_TIME_RIDE_POOLING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return 0.0 // TODO remove
    }

    fun getEGRESS_TIME_RIDE_POOLING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return 0.0 // TODO remove
    }

    fun getWAITING_TIME_MOIA(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        // TODO figure out a way to implement this
        return 0.0
    }

    fun getACTIVITY_TYPE_IS_LEISURE_TRAVEL(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (nextActivity.type == purposes.leisureTravel).D
    }

    fun getACTIVITY_TYPE_IS_BUSINESS_TRAVEL(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return (nextActivity.type == purposes.businessTravel).D
    }

    fun getTRAVEL_TIME_E_SCOOTER(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            eScooter,
            previousActivity.endTime
        )
            .toDouble(durationUnit)
    }

    fun getTRAVEL_COST_E_SCOOTER(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.cost(
            origin,
            destination,
            eScooter,
            previousActivity.endTime
        )
            .toDouble(currencyUnit)
    }

    fun getACCESS_TIME_E_SCOOTER(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return 0.0 // TODO remove
    }

    fun getTRAVEL_TIME_CARSHARING_FREE(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            carSharingFree,
            previousActivity.endTime
        )
            .toDouble(durationUnit)
    }

    fun getTRAVEL_COST_CARSHARING_FREE(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.cost(
            origin,
            destination,
            carSharingFree,
            previousActivity.endTime
        )
            .toDouble(currencyUnit)
    }

    fun getACCESS_TIME_CARSHARING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return 0.0 // TODO remove
    }

    fun getEGRESS_TIME_CARSHARING(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return 0.0 // TODO remove
    }

    fun getTRAVEL_TIME_CARSHARING_STATION(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            carSharingStation,
            previousActivity.endTime
        )
            .toDouble(durationUnit)
    }

    fun getTRAVEL_COST_CARSHARING_STATION(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.cost(
            origin,
            destination,
            carSharingStation,
            previousActivity.endTime
        )
            .toDouble(currencyUnit)
    }

    fun getTRAVEL_TIME_TAXI(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.duration(
            origin,
            destination,
            taxi,
            previousActivity.endTime
        ).toDouble(durationUnit)
    }

    fun getTRAVEL_COST_TAXI(
        category: String,
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return impedance.cost(
            origin,
            destination,
            taxi,
            previousActivity.endTime
        ).toDouble(currencyUnit)
    }

    fun getChoiceSet(
        person: Person,
        origin: Location,
        destination: Location,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Collection<Mode> {
        return choiceSet
    }
}
