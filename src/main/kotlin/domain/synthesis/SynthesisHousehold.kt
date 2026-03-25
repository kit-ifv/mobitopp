package domain.synthesis

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.behavior.cars.SynthesisCar
import domain.synthesis.SynthesisPerson
import domain.synthesis.data.EconomicStatus
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.atomic.AtomicInteger

/**
 * A Synthesis household consists of multiple synthesis persons. The fields are variable to be modified within the
 * usage of the household
 */
class SynthesisHousehold<S: MinimumHouseholdAttributes, T : MinimumPersonAttributes> constructor(
    override val surveyHouseholdId: Long = 0,
    override val attributes: S,
    override val members: MutableList<SynthesisPerson<S, T>> = mutableListOf(),
) : ISurveyHousehold<S, T>, List<SynthesisPerson<S, T>> by members {
    val id = getNextId()

    // Whatever the type T is of my household class, the members must be at least that type or better

    lateinit var economicStatus: EconomicStatus
    fun economicStatusIsAssigned() = ::economicStatus.isInitialized
    var amountOfCars = 0
    val cars: MutableList<SynthesisCar> = mutableListOf()

    @TestOnly
    fun locationIsAssigned() = attributes.location != StandardLocation.Companion.LOCATIONUNKNOWN
    fun addMember(member: SurveyPerson<T>) {
        members.add(SynthesisPerson(this, age = member.age, sex = member.sex, attributes = member.attributes))
    }

    fun addMembers(members: Collection<SurveyPerson<T>>) = members.forEach { addMember(it) }

    companion object {
        private val counter = AtomicInteger(0)

        private fun getNextId(): Int {
            return counter.getAndIncrement()
        }
    }

    override val size: Int
        get() = members.size
}