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
import edu.kit.ifv.mobitopp.actitoppNG.Household
import edu.kit.ifv.mobitopp.actitoppNG.PersonAttributes
import edu.kit.ifv.mobitopp.actitoppNG.StandardHouseholdPlanGeneration
import edu.kit.ifv.mobitopp.actitoppNG.enums.AreaType
import edu.kit.ifv.mobitopp.actitoppNG.enums.Gender
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import utils.units.sinceStart
import java.lang.Double.min

typealias ACTHousehold = Household
typealias ActitoppEmployment = edu.kit.ifv.mobitopp.actitoppNG.enums.Employment
typealias ActitoppActivityType = edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType

class ActiToppNGGenerator<in S, in T>(
    val purposes: ChoiceModelPurposes,
    val converter: (RegionType) -> ZoneRegionType,

) :
    GenerateHouseholdActivitySchedule<S, T>
    where S : MinimumHouseholdAttributes,
          S : HasNumberOfCars,
          T : MinimumPersonAttributes,
          T : HasCommuteDistance,
          T : HasEducationDistance,
          T : HasEmployment {
    val strategy = StandardHouseholdPlanGeneration() // TODO change to Parallel once implemented.
    override fun generate(household: ISurveyHousehold<S, T>): List<PreliminaryActivitySchedule> {
        val actHH = convert(household)
        val output = strategy.generateSchedules(actHH)
        return output.entries.map { (_, v) -> finish(v) }
    }

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

    fun SurveyPerson<T>.actitoppAttributes(maxCommute: Double = 150.0): PersonAttributes {
        return PersonAttributes(
            gender = sex.toGender(),
            employment = employment.toActitoppEmployment(),
            age = age,
            commuteDistanceWork = min(attributes.distanceWork.inKilometers, maxCommute),
            commuteDistanceEducation = min(attributes.distanceEducation.inKilometers, maxCommute),
            isAllowedToWork = true, // TODO cross check with modellierer
        )
    }

    private fun Sex.toGender(): Gender {
        return when (this) {
            Sex.MALE -> Gender.MALE
            Sex.FEMALE -> Gender.FEMALE
            Sex.UNKNOWN -> Gender.FEMALE // TODO cros check so that this case can be handled
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
