package domain.synthesis.attributes.person

import edu.kit.ifv.units.Distance

/**
 * This interface annotates the information about a survey person, so that the information of the distance to
 * the work location is known in the survey.
 */
interface HasCommuteDistance {
    val distanceWork: Distance
}