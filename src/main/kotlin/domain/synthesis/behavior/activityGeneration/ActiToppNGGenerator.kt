package domain.synthesis.behavior.activityGeneration

import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.datastructure.schedule.RawActivity
import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.ZoneRegionType
import domain.shared.location.LOCATIONUNKNOWN
import domain.synthesis.behavior.SurveyWithCommute
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.employment
import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import edu.kit.ifv.mobitopp.actitoppNG.ActiToppHousehold
import edu.kit.ifv.mobitopp.actitoppNG.ActitoppPerson
import edu.kit.ifv.mobitopp.actitoppNG.Household
import edu.kit.ifv.mobitopp.actitoppNG.PersonAttributes
import edu.kit.ifv.mobitopp.actitoppNG.StandardHouseholdPlanGeneration
import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.enums.AreaType
import edu.kit.ifv.mobitopp.actitoppNG.enums.Gender
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import utils.units.sinceStart
import java.lang.Double.min

typealias ACTHousehold = Household
typealias ActitoppEmployment = edu.kit.ifv.mobitopp.actitoppNG.enums.Employment

/**
 * Actitopp is still using the zone region type numbers :(
 * @param converter A converter to get from the region type to the zone region type used by actitopp.
 */
class ActiToppNGGenerator(
    val purposes: ChoiceModelPurposes,
    val converter: (RegionType) -> ZoneRegionType,

    ) :
    GenerateHouseholdActivitySchedule<SurveyWithCommute> {
    val strategy = StandardHouseholdPlanGeneration()
    override fun generate(
        household: SynthesisHousehold<out SurveyWithCommute>,
    ): Map<SynthesisPerson<out SurveyWithCommute>, PreliminaryActivitySchedule> {
        val (actHH, mapping) = convert(household)

        val output = strategy.generateSchedules(actHH)
        return output.entries.associate { (k, v) -> mapping[k]!! to finish(v) }
    }

    fun finish(mobilityPlan: MobilityPlan): PreliminaryActivitySchedule {
        val finishedActivities = mobilityPlan.finish()
        return PreliminaryActivitySchedule(
            finishedActivities.map {
                RawActivity(
                    location = LOCATIONUNKNOWN,
                    startTime = it.startTime!!.sinceStart,
                    endTime = it.endTime!!.sinceStart,

                    type = it.activityType.toReengineeredType(purposes)
                )
            }.toMutableList()
        )
    }

    fun convert(household: SynthesisHousehold<out SurveyWithCommute>):
        Pair<ACTHousehold, Map<ActitoppPerson, SynthesisPerson<out SurveyWithCommute>>> {
        val actHousehold = ActiToppHousehold(
            numMinorsUpTo10 = household.numberOfChilds,
            numMinorsBelow18 = household.numberOfYouths,
            areaType = converter(household.location.regionType()).toAreaType(),
            numberOfCars = household.amountOfCars
        )
        val mapping = household.members.associateBy {
            ActitoppPerson(actHousehold, it.actitoppAttributes())
        }
        return actHousehold to mapping
    }

    fun SynthesisPerson<out SurveyWithCommute>.actitoppAttributes(maxCommute: Double = 150.0): PersonAttributes {
        return PersonAttributes(
            gender = sex.toGender(),
            employment = employment.toActitoppEmployment(),
            age = age,
            commuteDistanceWork = min(info.distanceWork.inKilometers, maxCommute),
            commuteDistanceEducation = min(info.distanceEducation.inKilometers, maxCommute),
            isAllowedToWork = true, // TODO cross check with modellierer
        )
    }

    private fun Sex.toGender(): Gender {
        return when (this) {
            Sex.MALE -> Gender.MALE
            Sex.FEMALE -> Gender.FEMALE
        }
    }

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
            Employment.EDUCATION -> ActitoppEmployment.DEFINITELY_UNKNOWN // TODO cross check with modellierer
            Employment.HOMEKEEPER -> ActitoppEmployment.HOUSEKEEPER
            Employment.RETIRED -> ActitoppEmployment.RETIRED
            Employment.INFANT -> ActitoppEmployment.DEFINITELY_UNKNOWN
            Employment.NONE -> ActitoppEmployment.UNOCCUPIED
        }
    }
}

fun ActivityType.toReengineeredType(purposes: ChoiceModelPurposes):
        domain.shared.enums.ActivityType {
    return when (this) {
        ActivityType.WORK -> purposes.work
        ActivityType.EDUCATION -> purposes.education
        ActivityType.LEISURE -> purposes.leisure
        ActivityType.SHOPPING -> purposes.shopping
        ActivityType.TRANSPORT -> purposes.service
        ActivityType.HOME -> purposes.home
    }
}

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
