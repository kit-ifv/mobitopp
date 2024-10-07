@file:Suppress(
    "VariableNaming",
    "MaximumLineLength",
    "CyclomaticComplexMethod",
    "LongParameterList",
    "CognitiveComplexMethod",
    "CognitiveComplexMethod",
    "LongMethod"
)

package usecases.choicemodels

import datastructure.StationaryAction
import domain.data.Person
import domain.enums.Mode
import domain.location.Metrics
import domain.location.ZoneLocation
import modeling.models.ChoiceModel
import usecases.AttractivenessModel
import utils.CodePlan
import utils.collections.select
import utils.units.Time
import kotlin.math.exp
import kotlin.math.ln

fun interface MakeUtilities {
    fun createFrom(
        a: AttractivenessModel,
        l: ModeChoiceParameters,
        m: CodePlan<Mode>,
        h: ModeChoiceHelperMNL
    ): IGeneratedHcUtilityFunction
}

class LegacyModeChoiceModel(
    attractivenessModel: AttractivenessModel,
    logitParameters: ModeChoiceParameters = ModeChoiceParameters(),
    override val modes: CodePlan<Mode>,
    private val helper: ModeChoiceHelperMNL = ModeChoiceHelperMNL(attractivenessModel, modes),
    val impedance: Metrics,
    override val modeFilter: ModeFilter<Mode, Person> = ModeAvailabilityFilter(modes),
    val utilitiesGenerator: MakeUtilities = MakeUtilities { a, l, m, h -> GeneratedHcUtilityFunction(a, l, m, h) }
) : ChoiceModel<Person, Mode>, BasicModesModel {

    /** Generate the utilities by the generator function so that the wild parameter list does not need to be passed
     * for every different constructor.
     */
    val utilities: IGeneratedHcUtilityFunction by lazy {
        utilitiesGenerator.createFrom(attractivenessModel, logitParameters, modes, helper)
    }
    override val name: String = "LegacyModeChoiceModel"

    override fun choices(agent: Person, time: Time): Set<Mode> {
        return modes.values()
    }

    fun select(agent: Person, time: Time): Mode {
        return select(agent, modes.values(), time)
    }

    override fun filter(agent: Person, choices: Set<Mode>, time: Time) =
        modeFilter.filter(choices.toList(), agent).toSet()

    override fun select(agent: Person, choices: Set<Mode>, time: Time): Mode {
        val lastActivity = agent.schedule.pastActivities().last { it <= time }
        val nextActivity = agent.schedule.activities().first { it > time }
        return choices.select(
            LegacyModeChoiceParameters(
                agent,
                lastActivity,
                nextActivity,
                lastActivity.location as ZoneLocation,
                nextActivity.location as ZoneLocation
            )
        )
    }

    val lambda_miv: Double
    val lambda_newmob: Double
    val lambda_oevrad: Double
    val lambda_root: Double
    val lambda_taxi: Double
    val alpha0_newmob: Double
    val alpha0_miv: Double
    val alpha0_oevrad: Double
    val alpha0_taxi: Double
    val b_members_on_alpha0_newmob: Double
    val b_members_on_alpha0_oevrad: Double
    val b_members_on_alpha0_miv: Double
    val b_members_on_alpha0_taxi: Double

    /**
     * Instantiates a GeneratedHcModeChoice with the given LogitParameters, HcModeChoiceHelper, IntermodalModeChoiceModelLogger
     */
    init {
        this.lambda_miv = logitParameters.lambda_miv
        this.lambda_newmob = logitParameters.lambda_newmob
        this.lambda_oevrad = logitParameters.lambda_oevrad
        this.lambda_root = logitParameters.lambda_root
        this.lambda_taxi = logitParameters.lambda_taxi
        this.alpha0_newmob = logitParameters.alpha0_newmob
        this.alpha0_miv = logitParameters.alpha0_miv
        this.alpha0_oevrad = logitParameters.alpha0_oevrad
        this.alpha0_taxi = logitParameters.alpha0_taxi
        this.b_members_on_alpha0_newmob = logitParameters.b_members_on_alpha0_newmob
        this.b_members_on_alpha0_oevrad = logitParameters.b_members_on_alpha0_oevrad
        this.b_members_on_alpha0_miv = logitParameters.b_members_on_alpha0_miv
        this.b_members_on_alpha0_taxi = logitParameters.b_members_on_alpha0_taxi
    }

    private fun Set<Mode>.select(params: LegacyModeChoiceParameters): Mode {
        return selectMode(
            params.person,
            params.from,
            params.to,
            params.previous,
            params.next,
            this,
            impedance,
            params.person.random.nextDouble()
        )
    }

    fun selectMode(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Mode {
        val _choiceSet: Set<Mode> = helper.getChoiceSet(
            person,
            origin,
            destination,
            previousActivity,
            nextActivity,
            choiceSet,
            impedance,
            randomNumber
        ).toSet()
        // Availability flags
        val is_taxi_available = _choiceSet.contains(modeMap[TAXI_KEY])
        val is_rp_available = _choiceSet.contains(modeMap[RIDE_POOLING_KEY])
        val is_bs_available = _choiceSet.contains(modeMap[BIKESHARING_KEY])
        val is_cs_ff_available = _choiceSet.contains(modeMap[CARSHARING_FREE_KEY])
        val is_e_scooter_available = _choiceSet.contains(modeMap[E_SCOOTER_KEY])
        val is_card_available = _choiceSet.contains(modeMap[CAR_KEY])
        val is_passenger_available = _choiceSet.contains(modeMap[PASSENGER_KEY])
        val is_cs_sb_available = _choiceSet.contains(modeMap[CARSHARING_STATION_KEY])
        val is_bike_available = _choiceSet.contains(modeMap[BIKE_KEY])
        val is_pt_available = _choiceSet.contains(modeMap[PUBLICTRANSPORT_KEY])
        val is_walk_available = _choiceSet.contains(modeMap[PEDESTRIAN_KEY])
        val is_nest_taxi_available = is_taxi_available || is_rp_available
        val is_nest_newmob_available = is_bs_available || is_cs_ff_available || is_e_scooter_available
        val is_nest_miv_available = is_card_available || is_passenger_available || is_cs_sb_available
        val is_nest_oevrad_available = is_bike_available || is_pt_available

        // Calculate probabilities for each category using the given utilities object with respect to the availability flags above
        val U_fuss = if (is_walk_available) {
            utilities.calculateU_fuss(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }

        val U_rad = if (is_bike_available) {
            utilities.calculateU_rad(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }

        val U_pkw = if (is_card_available) {
            utilities.calculateU_pkw(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }

        val U_mf = if (is_passenger_available) {
            utilities.calculateU_mf(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }

        val U_oev = if (is_pt_available) {
            utilities.calculateU_oev(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }

        val U_bs = if (is_bs_available) {
            utilities.calculateU_bs(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }
        val U_moia = if (is_rp_available) {
            utilities.calculateU_moia(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }

        val U_escooter = if (is_e_scooter_available) {
            utilities.calculateU_escooter(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }

        val U_cs_ff = if (is_cs_ff_available) {
            utilities.calculateU_cs_ff(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }

        val U_cs_sb = if (is_cs_sb_available) {
            utilities.calculateU_cs_sb(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }

        val U_taxi = if (is_taxi_available) {
            utilities.calculateU_taxi(
                person,
                origin,
                destination,
                previousActivity,
                nextActivity,
                choiceSet,
                impedance,
                randomNumber
            )
        } else {
            0.0
        }

        // alpha probabilities
        val alpha_newmob =
            exp(alpha0_newmob + b_members_on_alpha0_newmob) / (
                exp(alpha0_newmob + b_members_on_alpha0_newmob) + exp(
                    alpha0_miv + b_members_on_alpha0_miv
                ) + exp(alpha0_oevrad + b_members_on_alpha0_oevrad) + exp(alpha0_taxi + b_members_on_alpha0_taxi)
                )
        val alpha_miv =
            exp(alpha0_miv + b_members_on_alpha0_miv) / (
                exp(alpha0_newmob + b_members_on_alpha0_newmob) + exp(
                    alpha0_miv + b_members_on_alpha0_miv
                ) + exp(alpha0_oevrad + b_members_on_alpha0_oevrad) + exp(alpha0_taxi + b_members_on_alpha0_taxi)
                )
        val alpha_oevrad =
            exp(alpha0_oevrad + b_members_on_alpha0_oevrad) / (
                exp(alpha0_newmob + b_members_on_alpha0_newmob) + exp(
                    alpha0_miv + b_members_on_alpha0_miv
                ) + exp(alpha0_oevrad + b_members_on_alpha0_oevrad) + exp(alpha0_taxi + b_members_on_alpha0_taxi)
                )
        val alpha_taxi =
            exp(alpha0_taxi + b_members_on_alpha0_taxi) / (
                exp(alpha0_newmob + b_members_on_alpha0_newmob) + exp(
                    alpha0_miv + b_members_on_alpha0_miv
                ) + exp(alpha0_oevrad + b_members_on_alpha0_oevrad) + exp(alpha0_taxi + b_members_on_alpha0_taxi)
                )

        // nest: taxi --------------------------------------------------------------------------------
        val exp_U_taxi = if (is_taxi_available) exp(U_taxi / lambda_taxi) else 0.0
        val exp_U_moia_taxi = if (is_rp_available) alpha_taxi * exp(U_moia / lambda_taxi) else 0.0

        // nest sum and logsum of taxi
        val nest_taxi_sum = exp_U_taxi + exp_U_moia_taxi
        val logsum_nest_taxi = ln(nest_taxi_sum)

        // nest probabilities of taxi
        val p_taxi_in_nest_taxi = if (is_taxi_available) customDivide(exp_U_taxi, nest_taxi_sum) else 0.0

        val p_rp_in_nest_taxi = if (is_rp_available) customDivide(exp_U_moia_taxi, nest_taxi_sum) else 0.0

        // -------------------------------------------------------------------------------------------

        // nest: newmob --------------------------------------------------------------------------------
        val exp_U_bs = if (is_bs_available) exp(U_bs / lambda_newmob) else 0.0
        val exp_U_cs_ff = if (is_cs_ff_available) exp(U_cs_ff / lambda_newmob) else 0.0
        val exp_U_escooter = if (is_e_scooter_available) exp(U_escooter / lambda_newmob) else 0.0
        val exp_U_moia_newmob = if (is_rp_available) alpha_newmob * exp(U_moia / lambda_newmob) else 0.0

        // nest sum and logsum of newmob
        val nest_newmob_sum = exp_U_bs + exp_U_cs_ff + exp_U_escooter + exp_U_moia_newmob
        val logsum_nest_newmob = ln(nest_newmob_sum)

        // nest probabilities of newmob
        val p_bs_in_nest_newmob = if (is_bs_available) customDivide(exp_U_bs, nest_newmob_sum) else 0.0

        val p_cs_ff_in_nest_newmob = if (is_cs_ff_available) customDivide(exp_U_cs_ff, nest_newmob_sum) else 0.0

        val p_e_scooter_in_nest_newmob =
            if (is_e_scooter_available) customDivide(exp_U_escooter, nest_newmob_sum) else 0.0

        val p_rp_in_nest_newmob = if (is_rp_available) customDivide(exp_U_moia_newmob, nest_newmob_sum) else 0.0

        // ---------------------------------------------------------------------------------------------

        // nest: miv --------------------------------------------------------------------------------
        val exp_U_pkw = if (is_card_available) exp(U_pkw / lambda_miv) else 0.0
        val exp_U_mf = if (is_passenger_available) exp(U_mf / lambda_miv) else 0.0
        val exp_U_cs_sb = if (is_cs_sb_available) exp(U_cs_sb / lambda_miv) else 0.0
        val exp_U_moia_miv = if (is_rp_available) alpha_miv * exp(U_moia / lambda_newmob) else 0.0

        // nest sum and logsum of miv
        val nest_miv_sum = exp_U_pkw + exp_U_mf + exp_U_cs_sb + exp_U_moia_miv
        val logsum_nest_miv = ln(nest_miv_sum)

        // nest probabilities of miv
        val p_card_in_nest_miv = if (is_card_available) customDivide(exp_U_pkw, nest_miv_sum) else 0.0

        val p_passenger_in_nest_miv = if (is_passenger_available) customDivide(exp_U_mf, nest_miv_sum) else 0.0

        val p_cs_sb_in_nest_miv = if (is_cs_sb_available) customDivide(exp_U_cs_sb, nest_miv_sum) else 0.0

        val p_rp_in_nest_miv = if (is_rp_available) customDivide(exp_U_moia_miv, nest_miv_sum) else 0.0

        // ------------------------------------------------------------------------------------------

        // nest: oevrad --------------------------------------------------------------------------------
        val exp_U_rad = if (is_bike_available) exp(U_rad / lambda_oevrad) else 0.0
        val exp_U_oev = if (is_pt_available) exp(U_oev / lambda_oevrad) else 0.0
        val exp_U_moia_oevrad = if (is_rp_available) alpha_oevrad * exp(U_moia / lambda_newmob) else 0.0

        // nest sum and logsum of oevrad
        val nest_oevrad_sum = exp_U_rad + exp_U_oev + exp_U_moia_oevrad
        val logsum_nest_oevrad = ln(nest_oevrad_sum)

        // nest probabilities of oevrad
        val p_bike_in_nest_oevrad = if (is_bike_available) customDivide(exp_U_rad, nest_oevrad_sum) else 0.0

        val p_pt_in_nest_oevrad = if (is_pt_available) customDivide(exp_U_oev, nest_oevrad_sum) else 0.0

        val p_rp_in_nest_oevrad = if (is_pt_available) customDivide(exp_U_moia_oevrad, nest_oevrad_sum) else 0.0

        // ---------------------------------------------------------------------------------------------

        // nest: root --------------------------------------------------------------------------------
        val exp_U_fuss = if (is_walk_available) exp(U_fuss / lambda_root) else 0.0
        val exp_nest_taxi = if (is_nest_taxi_available) exp((logsum_nest_taxi * lambda_taxi) / lambda_root) else 0.0
        val exp_nest_newmob =
            if (is_nest_newmob_available) exp((logsum_nest_newmob * lambda_newmob) / lambda_root) else 0.0
        val exp_nest_miv = if (is_nest_miv_available) exp((logsum_nest_miv * lambda_miv) / lambda_root) else 0.0
        val exp_nest_oevrad =
            if (is_nest_oevrad_available) exp((logsum_nest_oevrad * lambda_oevrad) / lambda_root) else 0.0

        // nest sum and logsum of root
        val nest_root_sum = exp_U_fuss + exp_nest_taxi + exp_nest_newmob + exp_nest_miv + exp_nest_oevrad

        // nest probabilities of root
        val p_walk_in_nest_root = if (is_walk_available) customDivide(exp_U_fuss, nest_root_sum) else 0.0

        val p_taxi_in_nest_root = if (is_nest_taxi_available) customDivide(exp_nest_taxi, nest_root_sum) else 0.0

        val p_newmob_in_nest_root = if (is_nest_newmob_available) customDivide(exp_nest_newmob, nest_root_sum) else 0.0

        val p_miv_in_nest_root = if (is_nest_miv_available) customDivide(exp_nest_miv, nest_root_sum) else 0.0

        val p_oevrad_in_nest_root = if (is_nest_oevrad_available) customDivide(exp_nest_oevrad, nest_root_sum) else 0.0

        // -------------------------------------------------------------------------------------------

        // Put probability of each category in a map. The probabilities are computed by multiplying the nest-probabilities along the paths in the nest structure.
        // If a category is marked '?' in the nest structure it is only added to the map if it is available
        val probabilities: MutableMap<Mode, Double> = mutableMapOf()
        if (is_bike_available) {
            probabilities[modeMap[BIKE_KEY]!!] = p_oevrad_in_nest_root * p_bike_in_nest_oevrad
        }
        if (is_bs_available) {
            probabilities[modeMap[BIKESHARING_KEY]!!] = p_newmob_in_nest_root * p_bs_in_nest_newmob
        }
        if (is_card_available) {
            probabilities[modeMap[CAR_KEY]!!] = p_miv_in_nest_root * p_card_in_nest_miv
        }
        if (is_cs_ff_available) {
            probabilities[modeMap[CARSHARING_FREE_KEY]!!] = p_newmob_in_nest_root * p_cs_ff_in_nest_newmob
        }
        if (is_cs_sb_available) {
            probabilities[modeMap[CARSHARING_STATION_KEY]!!] = p_miv_in_nest_root * p_cs_sb_in_nest_miv
        }
        if (is_e_scooter_available) {
            probabilities[modeMap[E_SCOOTER_KEY]!!] = p_newmob_in_nest_root * p_e_scooter_in_nest_newmob
        }
        if (is_passenger_available) {
            probabilities[modeMap[PASSENGER_KEY]!!] = p_miv_in_nest_root * p_passenger_in_nest_miv
        }
        if (is_walk_available) {
            probabilities[modeMap[PEDESTRIAN_KEY]!!] = p_walk_in_nest_root
        }
        if (is_pt_available) {
            probabilities[modeMap[PUBLICTRANSPORT_KEY]!!] = p_oevrad_in_nest_root * p_pt_in_nest_oevrad
        }
        if (is_rp_available) {
            probabilities[modeMap[RIDE_POOLING_KEY]!!] =
                p_taxi_in_nest_root * p_rp_in_nest_taxi + p_newmob_in_nest_root * p_rp_in_nest_newmob + p_oevrad_in_nest_root * p_rp_in_nest_oevrad + p_miv_in_nest_root * p_rp_in_nest_miv
        }
        if (is_taxi_available) {
            probabilities[modeMap[TAXI_KEY]!!] = p_taxi_in_nest_root * p_taxi_in_nest_taxi
        }

        // TODO Logit model cannot be used, as it calculates the exp(U) internally, which is already done at this point
        // Filter probabilities
//        val build: LogitModel<Person, Mode> = object : LogitModel<Person, Mode>() {
//            override fun utility(agent: Person, choice: Mode, time: Time): Double {
//                return ln(probabilities[choice] ?: Double.NEGATIVE_INFINITY)
//            }
//
//            override val name: String = "TEST MODE LOGIT"
//
//            override fun choices(agent: Person, time: Time): Set<Mode> {
//                return _choiceSet
//            }
//        }
//        //TODO no access to time and reconstructing via previous activity is not good.
//        return build.select(person, _choiceSet, previousActivity.endTime)
        val filteredProbabilities = probabilities.filter { it.value.isFinite() }
        return filteredProbabilities.select(randomNumber)
    }

    private fun customDivide(numerator: Double, denominator: Double): Double {
        return if (numerator.isInfinite() && denominator.isInfinite()) {
            1.0
        } else {
            numerator / denominator
        }
    }
}
