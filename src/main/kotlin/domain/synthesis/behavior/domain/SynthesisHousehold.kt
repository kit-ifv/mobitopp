package domain.synthesis.behavior.domain

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.LocatedHousehold
import domain.synthesis.behavior.SynthesisCar
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.HouseholdType
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.euros
import java.util.concurrent.atomic.AtomicInteger

/**
 * A Synthesis household consists of multiple synthesis persons. The fields are variable to be modified within the
 * usage of the household
 */
class SynthesisHousehold<out S: MinimumHouseholdAttributes, out T> constructor(
    override val surveyHouseholdId: Long = 0,
    override val attributes: S,
    override val members: List<SynthesisPerson<T>>,
) : LocatedHousehold<S, T>, List<SynthesisPerson<T>> by members {
    val id = getNextId()

    // Whatever the type T is of my household class, the members must be at least that type or better

    override lateinit var location: StandardLocation

    fun locationIsAssigned() = ::location.isInitialized
    lateinit var economicStatus: EconomicStatus
    fun economicStatusIsAssigned() = ::economicStatus.isInitialized
    var amountOfCars = 0
    val cars: MutableList<SynthesisCar> = mutableListOf()

//    operator fun get(index: Int): SynthesisPerson<T> = members[index]


    companion object {
        private val counter = AtomicInteger(0)

        private fun getNextId(): Int {
            return counter.getAndIncrement()
        }
    }
}

class ConstructionHH<S : MinimumHouseholdAttributes, T>() : ISurveyHousehold<S, T> {
    override var surveyHouseholdId: Long = 0
    override var income: Currency = 0.euros
    override var type: HouseholdType = HouseholdType.UNDEFINED
    override val members: MutableList<SynthesisPerson<T>> = mutableListOf()

    override lateinit var attributes: S
    var location: StandardLocation? = null

    fun addPerson(start: SynthesisPerson<T>, lambda: SynthesisPersonInfo<out T>.() -> Unit = {}) {
        val infoBlock = SynthesisPersonInfo<T>(start).apply(lambda)
        members.add(
            SynthesisPerson(
                location!!,
                infoBlock.age,
                infoBlock.sex,
                infoBlock.information!!,
                infoBlock.personId
            )
        )

    }


    companion object {
        fun <S : MinimumHouseholdAttributes, T> fromSynthesisHousehold(hh: SynthesisHousehold<S, T>): ConstructionHH<S, T> {
            return ConstructionHH<S, T>().apply {
                surveyHouseholdId = hh.surveyHouseholdId
                income = hh.income
                type = hh.type
                location = hh.location
                attributes = hh.attributes
                hh.members.forEach { member ->
                    addPerson(member)
                }
            }
        }
    }
}
