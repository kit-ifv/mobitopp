package domain.synthesis.behavior.activityGeneration

import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.datastructure.schedule.RawActivity
import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.ZoneRegionType
import domain.shared.location.StandardLocation
import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.HasCommuteDistance
import domain.synthesis.attributes.person.HasEducationDistance
import domain.synthesis.attributes.person.HasEmployment
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.attributes.person.employment
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.mobitopp.actitoppNG.ActiToppHousehold
import edu.kit.ifv.mobitopp.actitoppNG.ActitoppPerson
import edu.kit.ifv.mobitopp.actitoppNG.AllChoiceModels
import edu.kit.ifv.mobitopp.actitoppNG.Household
import edu.kit.ifv.mobitopp.actitoppNG.HouseholdPlanGeneration
import edu.kit.ifv.mobitopp.actitoppNG.PersonAttributes
import edu.kit.ifv.mobitopp.actitoppNG.PlanGenerationParameters
import edu.kit.ifv.mobitopp.actitoppNG.StandardHouseholdPlanGeneration
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ReusablePlanGeneration
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.kilometers
import utils.units.sinceStart
import java.lang.Double.min

typealias ACTHousehold = Household
typealias ActitoppEmployment = edu.kit.ifv.mobitopp.actitoppNG.enums.Employment
typealias ActitoppActivityType = edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType

/**
 * Generates an activity schedule for a household using the *actiTopp* engine.
 *
 * The generator translates a survey household into the actiTopp household model,
 *  runs the configured [HouseholdPlanGeneration] strategy and converts the
 *  resulting [MobilityPlan] objects back into [PreliminaryActivitySchedule]s.
 *
 * This class is responsible for the orchestration of the actiTopp generation
 *  process. Domain-specific mappings between the project model and actiTopp
 *  enums are delegated to the supplied [ActiToppAdapter].
 *
 *  **Key responsibilities**
 *  - Create an actiTopp household from the survey household.
 *  - Create one actiTopp person for each survey household member.
 *  - Run the configured actiTopp household plan generation strategy.
 *  - Convert generated actiTopp activities back to project-specific activities.
 *  - Clamp commute and education distances to [maxCommute] before passing them to
 *  actiTopp.
 *
 *  @param S the type that provides household-level attributes. Must implement
 *  both [MinimumHouseholdAttributes] and [HasNumberOfCars].
 *  @param T the type that provides person-level attributes. Must implement
 *  [MinimumPersonAttributes], [HasCommuteDistance],
 *  [HasEducationDistance] and [HasEmployment].
 *  @property purposes the project-specific activity-purpose mapping. This is
 *  still stored by the generator, but activity type conversion is
 *  performed through [actiToppAdapter].
 *  @property strategy the algorithm that creates actiTopp schedules. The default
 *  uses the standard actiTopp implementation with reusable plan generation
 *  @property maxCommute the upper bound for commute and education distances.
 *  Larger distances are clamped before they are passed to actiTopp
 *  because very large values may destabilize generation.
 *  @property actiToppAdapter adapter used to translate between project-specific
 *  domain values and actiTopp values.
 *  @see HouseholdPlanGeneration
 *  @see ActiToppAdapter
 *  @see ACTHousehold
 */

class ActiToppNGGenerator<in S, in T>(
    val purposes: ChoiceModelPurposes,
    val strategy: HouseholdPlanGeneration = StandardHouseholdPlanGeneration.fromModels(
        models = AllChoiceModels.create(PlanGenerationParameters()),

    ) {
        ReusablePlanGeneration(it)
    },
    val maxCommute: Distance = 150.kilometers,
    private val actiToppAdapter: ActiToppAdapter,
) : GenerateHouseholdActivitySchedule<S, T>
    where S : MinimumHouseholdAttributes,
          S : HasNumberOfCars,
          T : MinimumPersonAttributes,
          T : HasCommuteDistance,
          T : HasEducationDistance,
          T : HasEmployment {

    constructor(purposes: ChoiceModelPurposes, converter: (RegionType) -> ZoneRegionType) : this(
        purposes,
        actiToppAdapter = StandardActiToppAdapter(purposes, converter = converter),
    )

    /** Generates a schedule for *all* members of the supplied [household]. */
    override fun generate(household: ISurveyHousehold<S, T>): List<PreliminaryActivitySchedule> {
        val actHH = convert(household)
        val output = strategy.generateSchedules(actHH)
        return output.entries.map { (_, v) -> finish(v) }
    }

    /**
     * Completes a raw [MobilityPlan] by converting its activities to the
     * project‑specific model.
     */
    fun finish(mobilityPlan: MobilityPlan): PreliminaryActivitySchedule {
        val finishedActivities = mobilityPlan.finish()
        return PreliminaryActivitySchedule(
            finishedActivities.map {
                RawActivity(
                    location = StandardLocation.LOCATIONUNKNOWN,
                    startTime = it.startTime!!.sinceStart,
                    endTime = it.endTime!!.sinceStart,

                    type = actiToppAdapter.decodeActivityType(it.activityType),
                )
            }.toMutableList(),
        )
    }

    /**
     * Converts a survey household to an actiTopp [ACTHousehold] and creates
     * a corresponding [ActitoppPerson] for each member.
     *
     * @return the populated actiTopp household.
     */
    fun convert(household: ISurveyHousehold<S, T>): ACTHousehold {
        val actHousehold = ActiToppHousehold(
            numMinorsUpTo10 = household.numberOfChilds,
            numMinorsBelow18 = household.numberOfYouths,
            areaType = actiToppAdapter.encodeRegionType(household.attributes.location.regionType),
            numberOfCars = household.attributes.amountOfCars,
        )
        household.members.forEach {
            ActitoppPerson(actHousehold, it.actitoppAttributes())
        }
        return actHousehold
    }

    @Suppress("MagicNumber") // TODO this may be relevant to fix, age 10 is magic
    val ISurveyHousehold<*, *>.numberOfChilds get() = members.count { it.age <= 10 }

    @Suppress("MagicNumber") // TODO this may be relevant to fix, age 10 is magic
    val ISurveyHousehold<*, *>.numberOfYouths get() = members.count { it.age in 10..<18 }

    /**
     * Builds a [PersonAttributes] instance from a [SurveyPerson].
     *
     * Commute distances are truncated to [maxCommute] to keep actiTopp stable.
     */
    fun SurveyPerson<T>.actitoppAttributes(): PersonAttributes = PersonAttributes(
        gender = actiToppAdapter.encodeSex(sex),
        employment = actiToppAdapter.encodeEmployment(employment),
        age = age,
        commuteDistanceWork = min(attributes.distanceWork.inKilometers, maxCommute.inKilometers),
        commuteDistanceEducation = min(attributes.distanceEducation.inKilometers, maxCommute.inKilometers),
        isAllowedToWork = true, // TODO cross check with modellierer where this field comes from.
    )
}
