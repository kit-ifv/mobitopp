package domain.synthesis.behavior.domain

import domain.shared.location.Location
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SynthesisCar
import domain.synthesis.data.EconomicStatus
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.euros
import java.util.concurrent.atomic.AtomicInteger

/**
 * A Synthesis household consists of multiple synthesis persons. The fields are variable to be modified within the
 * usage of the household
 */
class SynthesisHousehold<T>(
    override val surveyHouseholdId: Int = 0,
    override var income: Currency = 0.euros,

) : ISurveyHousehold<T> {
    val id = getNextId()

    // Whatever the type T is of my household class, the members must be at least that type or better
    override var members: MutableList<SynthesisPerson<out T>> = mutableListOf()
    lateinit var location: Location

    fun locationIsAssigned() = ::location.isInitialized
    lateinit var economicStatus: EconomicStatus
    fun economicStatusIsAssigned() = ::economicStatus.isInitialized
    var amountOfCars = 0
    val cars: MutableList<SynthesisCar> = mutableListOf()

    operator fun get(index: Int): SynthesisPerson<out T> = members[index]

    companion object {
        private val counter = AtomicInteger(0)

        private fun getNextId(): Int {
            return counter.getAndIncrement()
        }
    }
}
