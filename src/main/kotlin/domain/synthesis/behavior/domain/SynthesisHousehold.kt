package domain.synthesis.behavior.domain

import domain.shared.location.Location
import domain.synthesis.behavior.SynthesisCar
import domain.synthesis.data.EconomicStatus
import units.Currency
import units.euros

/**
 * A Synthesis household consists of multiple synthesis persons. The fields are variable to be modified within the
 * usage of the household
 */
class SynthesisHousehold<T>(
    var id: Int = 0,
    var income: Currency = 0.euros,

) {

    // Whatever the type T is of my household class, the members must be at least that type or better
    var members: MutableList<SynthesisPerson<out T>> = mutableListOf()
    val size get() = members.size
    lateinit var location: Location

    fun locationIsAssigned() = ::location.isInitialized
    lateinit var economicStatus: EconomicStatus
    fun economicStatusIsAssigned() = ::economicStatus.isInitialized
    var amountOfCars = 0
    val cars: MutableList<SynthesisCar> = mutableListOf()

    operator fun get(index: Int): SynthesisPerson<out T> = members[index]
}
