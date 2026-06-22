package edu.kit.ifv.domain.synthesis
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.domain.synthesis.behavior.cars.SynthesisCar
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.atomic.AtomicInteger

/**
 * A Synthesis household consists of multiple synthesis persons. The fields are variable to be modified within the
 * usage of the household
 */
class SynthesisHousehold<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> constructor(
    override val surveyHouseholdId: Long = 0,
    override val attributes: S,
    override val members: MutableList<SynthesisPerson<S, T>> = mutableListOf(),
) : ISurveyHousehold<S, T>,
    List<SynthesisPerson<S, T>> by members {
    val id = getNextId()

    // Whatever the type T is of my household class, the members must be at least that type or better

    val cars: MutableList<SynthesisCar> = mutableListOf()
    fun hasCars() = cars.isNotEmpty()

    @TestOnly
    fun locationIsAssigned() = attributes.location != StandardLocation.Companion.LOCATIONUNKNOWN
    fun addMember(member: SurveyPerson<T>, copyLambda: (T) -> T) {
        members.add(
            SynthesisPerson(
                this,
                age = member.age,
                sex = member.sex,
                attributes = copyLambda(
                    member
                        .attributes,
                ),
            ),
        )
    }

    /**
     * Add members inserts the target readonly members into this household, requires a copy function to create a new
     * attribute instance for each member, so that no duplicate access problems occur.
     */
    fun addMembers(members: Collection<SurveyPerson<T>>, copyLambda: (T) -> T) =
        members.forEach { addMember(it, copyLambda) }

    companion object {
        private val counter = AtomicInteger(0)

        private fun getNextId(): Int = counter.getAndIncrement()
    }

    override val size: Int
        get() = members.size
}
