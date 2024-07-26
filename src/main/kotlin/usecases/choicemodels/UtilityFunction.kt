package usecases.choicemodels

import datastructure.StationaryAction
import domain.data.Person
import domain.enums.StandardMode
import domain.location.ZoneLocation

interface UtilityFunction<T, P> {
    fun calculateUtility(choice: T, params: P): Double
}

// interface ChoiceModel<T, P> {
// //    fun calculateProbabilities(choices: List<T>, params: P, utilityFunction: UtilityFunction<T, P>): Map<T, Double>
//    fun Set<T>.select(params: P): T
// }

data class LegacyModeChoiceParameters(
    val person: Person,
    val previous: StationaryAction,
    val next: StationaryAction,
    val from: ZoneLocation,
    val to: ZoneLocation
)

interface LegacyModeChoice : UtilityFunction<StandardMode, LegacyModeChoiceParameters>
