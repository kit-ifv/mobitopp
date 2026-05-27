package domain.synthesis.behavior.activityGeneration

import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.datastructure.schedule.RawActivity
import domain.shared.enums.ActivityType
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
import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import edu.kit.ifv.mobitopp.actitoppNG.ActiToppHousehold
import edu.kit.ifv.mobitopp.actitoppNG.ActitoppPerson
import edu.kit.ifv.mobitopp.actitoppNG.AllChoiceModels
import edu.kit.ifv.mobitopp.actitoppNG.Household
import edu.kit.ifv.mobitopp.actitoppNG.HouseholdPlanGeneration
import edu.kit.ifv.mobitopp.actitoppNG.PersonAttributes
import edu.kit.ifv.mobitopp.actitoppNG.PlanGenerationParameters
import edu.kit.ifv.mobitopp.actitoppNG.StandardHouseholdPlanGeneration
import edu.kit.ifv.mobitopp.actitoppNG.enums.AreaType
import edu.kit.ifv.mobitopp.actitoppNG.enums.Gender
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
 * The generator translates a survey household into the actiTopp model, runs the
 * chosen [HouseholdPlanGeneration] strategy and converts the resulting
 * [MobilityPlan] back to a list of [PreliminaryActivitySchedule].
 *
 * **Key responsibilities**
 * - Map household‑level attributes (cars, region, etc.) to actiTopp.
 * - Convert each person’s attributes (gender, employment, commute distances) to
 *   actiTopp’s [PersonAttributes].
 * - Guard against unrealistic commute distances via the [maxCommute] limit.
 *
 * @param S the type that provides household‑level attributes. Must implement
 *          both [MinimumHouseholdAttributes] and [HasNumberOfCars].
 * @param T the type that provides person‑level attributes. Must implement
 *          [MinimumPersonAttributes], [HasCommuteDistance],
 *          [HasEducationDistance] and [HasEmployment].
 * @property purposes an object that maps actiTopp activity types to the
 *          project‑specific [ChoiceModelPurposes].
 * @property strategy the algorithm that creates actiTopp schedules. The
 *          default uses a standard implementation based on all choice models.
 * @property maxCommute the upper bound for commute and education distances.
 *          Distances larger than this are clamped to avoid breaking actiTopp
 *          (default: 150km).
 *
 * @property unknownSexResolution a function to convert unknown Sex to an actiTopp
 *          Gender.
 * @property converter a function that translates a survey [RegionType] into a
 *          [ZoneRegionType] required by actiTopp.
 *
 * @see HouseholdPlanGeneration
 * @see ACTHousehold
 */
class ActiToppNGGenerator<in S, in T>(
    val purposes: ChoiceModelPurposes,
    val strategy: HouseholdPlanGeneration = StandardHouseholdPlanGeneration.fromModels(
        models = AllChoiceModels.create(PlanGenerationParameters()),

        ) {
        ReusablePlanGeneration(it)
    },
    val maxCommute: Distance = 150.kilometers,
    val unknownSexResolution: (Sex) -> Gender = { Gender.FEMALE },
    val converter: (RegionType) -> ZoneRegionType,


    ) :
    GenerateHouseholdActivitySchedule<S, T>
        where S : MinimumHouseholdAttributes,
              S : HasNumberOfCars,
              T : MinimumPersonAttributes,
              T : HasCommuteDistance,
              T : HasEducationDistance,
              T : HasEmployment {
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

                    type = it.activityType.toReengineeredType(purposes)
                )
            }.toMutableList()
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
            areaType = converter(household.attributes.location.regionType).toAreaType(),
            numberOfCars = household.attributes.amountOfCars
        )
        household.members.forEach {
            ActitoppPerson(actHousehold, it.actitoppAttributes())
        }
        return actHousehold
    }

    /**
     * Builds a [PersonAttributes] instance from a [SurveyPerson].
     *
     * Commute distances are truncated to [maxCommute] to keep actiTopp stable.
     */
    fun SurveyPerson<T>.actitoppAttributes(): PersonAttributes {
        return PersonAttributes(
            gender = sex.toGender(),
            employment = employment.toActitoppEmployment(),
            age = age,
            commuteDistanceWork = min(attributes.distanceWork.inKilometers, maxCommute.inKilometers),
            commuteDistanceEducation = min(attributes.distanceEducation.inKilometers, maxCommute.inKilometers),
            isAllowedToWork = true, // TODO cross check with modellierer where this field comes from.
        )
    }

    /**
     * Maps a survey [Sex] to actiTopp’s [Gender] enumeration.

     */
    private fun Sex.toGender(): Gender {
        return when (this) {
            Sex.MALE -> Gender.MALE
            Sex.FEMALE -> Gender.FEMALE
            else -> unknownSexResolution(this)
        }
    }

    /**
     * Translates a survey [Employment] value into the corresponding
     * actiTopp [ActitoppEmployment] enum.
     *
     * Several cases are mapped to `DEFINITELY_UNKNOWN`; confirm these
     * defaults with the modelling team and remove this comment afterwards
     */
    @Suppress("CyclomaticComplexMethod") // I would rather have a concise mapping, instead of cutting this down
    private fun Employment.toActitoppEmployment(): ActitoppEmployment {
        return when (this) {
            Employment.UNKNOWN -> ActitoppEmployment.DEFINITELY_UNKNOWN
            Employment.FULLTIME -> ActitoppEmployment.FULLTIME
            Employment.PARTTIME -> ActitoppEmployment.PARTTIME
            Employment.MARGINAL -> ActitoppEmployment.MARGINAL
            Employment.UNEMPLOYED -> ActitoppEmployment.UNOCCUPIED
            Employment.STUDENT -> ActitoppEmployment.STUDENT
            Employment.STUDENT_PRIMARY -> ActitoppEmployment.STUDENT_PRIMARY
            Employment.STUDENT_SECONDARY -> ActitoppEmployment.STUDENT_SECONDARY
            Employment.STUDENT_TERTIARY -> ActitoppEmployment.STUDENT_TERTIARY
            Employment.EDUCATION -> ActitoppEmployment.DEFINITELY_UNKNOWN
            Employment.HOMEKEEPER -> ActitoppEmployment.HOUSEKEEPER
            Employment.RETIRED -> ActitoppEmployment.RETIRED
            Employment.INFANT -> ActitoppEmployment.DEFINITELY_UNKNOWN
            Employment.NONE -> ActitoppEmployment.UNOCCUPIED
        }
    }
}

/**
 * Converts an actiTopp [ActitoppActivityType] back to the model’s [ActivityType]
 * using the supplied [purposes] mapping.
 *
 * @return the activity type defined in the project’s choice model.
 */
fun ActitoppActivityType.toReengineeredType(purposes: ChoiceModelPurposes): ActivityType {
    return when (this) {
        ActitoppActivityType.WORK -> purposes.work
        ActitoppActivityType.EDUCATION -> purposes.education
        ActitoppActivityType.LEISURE -> purposes.leisure
        ActitoppActivityType.SHOPPING -> purposes.shopping
        ActitoppActivityType.TRANSPORT -> purposes.service
        ActitoppActivityType.HOME -> purposes.home
    }
}

/**
 * Maps a [ZoneRegionType] (used by the re‑engineered zone model) to the
 * corresponding actiTopp [AreaType].
 */
fun ZoneRegionType.toAreaType(): AreaType {
    return when (this) {
        ZoneRegionType.RURAL -> AreaType.RURAL
        ZoneRegionType.PROVINCIAL -> AreaType.PROVINCIAL
        ZoneRegionType.CITYOUTSKIRT -> AreaType.CITYOUTSKIRT
        ZoneRegionType.METROPOLITAN -> AreaType.METROPOLITAN
        ZoneRegionType.CONURBATION -> AreaType.CONURBATION
        ZoneRegionType.DEFAULT -> AreaType.UNKNOWN
    }
}
