package application.steps.model

import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.share

// fun <S, C> S.scalePopulation(
//    percent: UnitIntervalValue = 1.share()
// ) where S : ModelExecution<C>, C : HouseholdContext {
//
//    var counter: Double = 0.0
//
//    val percentLabel = round(percent.toDouble() * 1000) / 10.0
//    addStep(
//
//        FilterStep<HouseholdBuilder, Household, HouseholdId> (
//            name = "Scale population to $percentLabel%",
//            repository = mobitopp.householdRepository,
//        ) {
//            counter += percent.toDouble()
//            if (counter >= 1.0) {
//                counter -= 1.0
//                true
//            } else {
//                false
//            }
//        }
//
//    )
// }

fun <T> scaleFilter(percent: UnitIntervalValue = 1.share()): (T) -> Boolean {
    var counter: Double = 0.0

    return {
        counter += percent.toDouble()
        if (counter >= 1.0) {
            counter -= 1.0
            true
        } else {
            false
        }
    }
}
