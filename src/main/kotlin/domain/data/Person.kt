package domain.data

import Mutable
import units.Currency
import units.UnitIntervalValue
import utils.Encodable
import utils.EnumDecodable
import utils.ID
import utils.Identifiable
import utils.collections.ClearableList
import utils.random.SeededActor
import utils.random.StochasticActor

typealias PersonId = ID<Person>

const val ADULT_AGE_GER = 18

@Suppress("ComplexInterface")
interface IPerson : Identifiable<PersonId>, StochasticActor {
    val household: IHousehold
    val age: Int
    val employment: Employment
    val sex: Sex
    val graduation: Graduation
    val income: Currency
    val hasBike: Boolean
    val hasCommuterTicket: Boolean
    val hasLicense: Boolean
    val sharingMemberships: List<ISharingProvider>
    val eMobilityAcceptance: UnitIntervalValue
    val chargingInfluence: ChargingInfluence
}

val IPerson.isAdult: Boolean
    get() = (age >= ADULT_AGE_GER)

@Mutable
abstract class Person(
    final override val id: PersonId,
    override val household: MutableHousehold,
    seed: Long,
) : SeededActor<Person>(seed), IPerson {
    // Agent<Person> TODO merge Agent and Stochastic Actor, or agent should just be wrapper in simulation

    abstract override val sharingMemberships: List<SharingProvider>

    abstract val plannedActivities: ClearableList<PlannedActivity>

//    val plannedActivities: List<PlannedActivity> //public view of activities
//        get() = plannedActivityList
//
//    internal abstract val plannedActivityList: MutableList<PlannedActivity>

    init {
        addAsMember()
    }

    private fun addAsMember() {
        this.household.members.add(this)
    }

    fun clearPlannedActivities() = this.plannedActivities.clear()
}

/**
 * An enum for the sex of a person. As this class is only applicable to a person the enum resides in the same source
 * code file as the person. (Refactor Idea maybe make an inner class)
 */
enum class Sex(override val code: Int) : Encodable {
    MALE(1),
    FEMALE(2);

    fun isFemale(): Boolean {
        return this == FEMALE
    }

    fun isMale(): Boolean {
        return this == MALE
    }

    override val description: String = name

    companion object : EnumDecodable<Sex>(Sex::class)
}

/**
 * An enum for the employment of a person. As this class is only applicable to a person the enum resides in the same
 * source code file as the person. (Refactor Idea maybe make an inner class)
 */
enum class Employment(override val code: Int) : Encodable {
    UNKNOWN(-1),
    FULLTIME(1),
    PARTTIME(2),
    MARGINAL(22),
    UNEMPLOYED(3),
    STUDENT(4),
    STUDENT_PRIMARY(40),
    STUDENT_SECONDARY(41),
    STUDENT_TERTIARY(42),
    EDUCATION(5),
    HOMEKEEPER(6),
    RETIRED(7),
    INFANT(8),
    NONE(9);

    override val description: String = name

    companion object : EnumDecodable<Employment>(Employment::class)
}

enum class Graduation(override val code: Int) : Encodable { // TODO split into school and higher education
    UNDEFINED(-1),
    OTHER(0),
    NOT_HIGH_SCHOOL(1),
    HIGH_SCHOOL_GRADUATE(2),
    SOME_COLLEGE_CREDIT_NO_DEGREE(3),
    ASSOCIATE_TECHNICAL_SCHOOL_DEGREE(4),
    BACHELOR_DEGREE(5),
    MASTER_DEGREE(6);

    override val description: String = name

    companion object : EnumDecodable<Graduation>(Graduation::class)
}

enum class ChargingInfluence(override val code: Int) : Encodable {
    ALWAYS(0),
    ONLY_WHEN_BATTERY_LOW(1),
    NEVER(2);

    override val description: String = name

    companion object : EnumDecodable<ChargingInfluence>(ChargingInfluence::class)
}
