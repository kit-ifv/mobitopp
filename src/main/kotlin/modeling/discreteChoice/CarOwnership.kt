package modeling.discreteChoice

import domain.data.EconomicStatus
import synthesis.SurveyHousehold

data class CarOwnershipParameters(val household: SurveyHousehold) {
    val size = household.members.size
    val economicStatus: EconomicStatus = TODO("Write a class where the household has an economic status assigned")
    val numDrivingLicence = 0 //household.members.count { it.hasDrivingLicence() } // TODO, doesent work with survey person, requires licencs
    val numberOfWorkers = 0 // household.members.count {it.works()} // TODO work is not in survey person, requires schedule
    val isWg = false //TODO
    val isOnlyRetired = false //TODO
    val isOnlyUnemployed = false //TODO
    val amountOfChildren = 0 // TODO
    val amountOfYouth = 0 // TODO
    fun evaluate(): Double = 0.0

}

data class CarOwnershipParameterse(
    val base: Double = 0.0
)
fun calculateUtilityCarOwnership(parameterObject: CarOwnershipParameterse) {

}

fun main() {


    val nested = NestedLogit.root<Attempt, Unit>{
        nest(lambda = 0.001) {
            add(Attempt.ONE) { _, _ -> 0.0}
            add(Attempt.TWO) { _, _ -> 0.0}

        }
        add(Attempt.THREE) { _, _ -> 0.0}
    }

    val nest = NestedLogit.root<Int, CarOwnershipParameters> {
        add(0) {_, p -> p.size * 0.1

        }
        nest(lambda = 0.9) {
            add(1) {_,_ -> 0.0}
            nest(lambda = 0.1) {
                add(2) {_, p -> 0.0}
                add(3) {_, p -> 0.0}
                add(4) {_, p -> 0.0}

            }
        }

    }
    val result = nest.calculateProbabilities(CarOwnershipParameters(SurveyHousehold(1, emptyList())))
    println(result)
    println(nested.calculateProbabilities(setOf(Attempt.ONE)))
    println(nested)
}

