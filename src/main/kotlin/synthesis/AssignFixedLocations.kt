package synthesis

import domain.enums.ActivityType
import domain.location.DistanceMetric
import domain.location.Location

data class PersonWithSchedule(
    private val person: SurveyPerson,
    private val household: SurveyHousehold,
    val homeLocation: Location,
    private val activitySchedule: ActivitySchedule
)


class StepAssign() {

    fun ActivityType.assign(personWithSchedule: PersonWithSchedule, distanceMetric: DistanceMetric, potentialLocations: Collection<Location>) {
        potentialLocations.map {distanceMetric.evaluate(it, personWithSchedule.homeLocation)}
    }
}