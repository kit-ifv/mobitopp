package synthesis.domain

import domain.data.EconomicStatus
import domain.location.Location
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
    lateinit var economicStatus: EconomicStatus
    var amountOfCars = 0


}