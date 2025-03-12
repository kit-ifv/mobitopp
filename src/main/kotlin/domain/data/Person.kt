package domain.data

import Mutable
import datastructure.Action
import datastructure.Schedule
import domain.enums.Mode
import domain.location.Location
import domain.resources.Subscribable
import modeling.events.Agent
import modeling.events.Event
import units.Currency
import units.UnitIntervalValue
import utils.Decodable
import utils.Encodable
import utils.ID
import utils.Identifiable
import utils.random.SeededActor

typealias PersonId = ID<Person>

const val ADULT_AGE_GER = 18

@Mutable
abstract class Person(
    final override val id: PersonId,
    val household: MutableHousehold,
    seed: Long,
) : SeededActor<Person>(seed),
    Identifiable<PersonId>,
    Agent<Person> { // TODO merge Agent and Stochastic Actor, or agent should just be wrapper in simulation

    abstract val age: Int
    abstract val employment: Employment
    abstract val sex: Sex
    abstract val graduation: Graduation
    abstract val income: Currency
    abstract val hasBike: Boolean
    abstract val hasCommuterTicket: Boolean
    abstract val hasLicense: Boolean
    abstract val memberships: Map<Subscribable<Person>, Boolean>
    abstract val eMobilityAcceptance: UnitIntervalValue
    abstract val chargingInfluence: ChargingInfluence
    abstract val schedule: Schedule // = Schedule(TrackableModel(BlockModel()))

    var inTransit: Boolean = false // TODO simulation attribute, how to handle?

    final override var location: Location = household.location

    init {
        addAsMember()
    }

    /* TODO it would be smart to separate the person class into attributes, and simulation state so that equality tests
        are easier to write, because currently I am uncertain which simulation states should be considered for equality
        checks.
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Person) return false
        return id == other.id &&
            household.id == other.household.id && // Compare over household id to avoid infinite loop
            age == other.age &&
            employment == other.employment &&
            sex == other.sex &&
            graduation == other.graduation &&
            income == other.income &&
            hasBike == other.hasBike &&
            hasCommuterTicket == other.hasCommuterTicket &&
            hasLicense == other.hasLicense &&
            // memberships &&// TODO no Idea how to incorporate memberships in equals check
            eMobilityAcceptance == other.eMobilityAcceptance &&
            chargingInfluence == other.chargingInfluence
    }

    private fun addAsMember() {
        this.household.members.add(this)
    }

    final override var nextEvent: Event<Person>? = null
    final override val entity: Person by lazy { this }

    // TODO keep for old tests to be functional, remove in the future! ->
    private val plannedActivityList: MutableList<PlannedActivity> = mutableListOf()
    val plannedActivities: List<PlannedActivity>
        get() = plannedActivityList

    fun addActivity(plannedActivity: PlannedActivity) {
        plannedActivityList.add(plannedActivity)

        schedule.addWithPrecedingLeg(
            plannedActivity.toActivity()
        )
    }
    // TODO <- remove until here

    val isAdult: Boolean
        get() = (age >= ADULT_AGE_GER)

    fun sharedResources() = memberships.keys.flatMap { it.availableResourcesFor(this) }.toSet()
    override fun hashCode(): Int {
        return id.hashCode()
        // TODO cannot use hash with reference to delegates that have certain properties not set, if the person is immediately added to the household
//        var result = id.hashCode()
//        result = 31 * result + household.hashCode()
//        result = 31 * result + age
//        result = 31 * result + employment.hashCode()
//        result = 31 * result + sex.hashCode()
//        result = 31 * result + graduation.hashCode()
//        result = 31 * result + income.hashCode()
//        result = 31 * result + hasBike.hashCode()
//        result = 31 * result + hasCommuterTicket.hashCode()
//        result = 31 * result + hasLicense.hashCode()
//        result = 31 * result + eMobilityAcceptance.hashCode()
//        result = 31 * result + chargingInfluence.hashCode()
//        return result
    }
}

fun Person.lastTransportMode(action: Action): Mode? {
    return schedule.pastLegs().lastOrNull { it < action }?.transportType
}

fun Schedule.location(): Location? {
    return present?.startLocation ?: past.lastOrNull()?.endLocation
}

fun Person.locationBySchedule() = schedule.location() ?: household.location

fun Person.getBestCar(): PrivateCar? {
    return household.cars.filter { it.state == PrivateCar.CarState.PARKED && (it.location == location) }
        .maxByOrNull { if (it.mainUser == this) 1 else 0 }
}

/**
 * An enum for the sex of a person. As this class is only applicable to a person the enum resides in the same source
 * code file as the person. (Refactor Idea maybe make an inner class)
 */
enum class Sex(private val code: Int) : Encodable {
    MALE(1),
    FEMALE(2);

    fun isFemale(): Boolean {
        return this == FEMALE
    }

    fun isMale(): Boolean {
        return this == MALE
    }

    override fun encode(): Int {
        return this.code
    }

    companion object : Decodable<Sex> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
        override fun values(): Set<Sex> = Sex.entries.toSet()
    }
}

/**
 * An enum for the employment of a person. As this class is only applicable to a person the enum resides in the same
 * source code file as the person. (Refactor Idea maybe make an inner class)
 */
enum class Employment(private val code: Int) : Encodable {
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

    override fun encode(): Int {
        return this.code
    }

    companion object : Decodable<Employment> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
        override fun values(): Set<Employment> = Employment.entries.toSet()
    }
}

enum class Graduation(private val code: Int) : Encodable { // TODO split into school and higher education
    UNDEFINED(-1),
    OTHER(0),
    NOT_HIGH_SCHOOL(1),
    HIGH_SCHOOL_GRADUATE(2),
    SOME_COLLEGE_CREDIT_NO_DEGREE(3),
    ASSOCIATE_TECHNICAL_SCHOOL_DEGREE(4),
    BACHELOR_DEGREE(5),
    MASTER_DEGREE(6);

    override fun encode() = this.code

    companion object : Decodable<Graduation> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
        override fun values(): Set<Graduation> = Graduation.entries.toSet()
    }
}

enum class ChargingInfluence(private val code: Int) : Encodable {
    ALWAYS(0),
    ONLY_WHEN_BATTERY_LOW(1),
    NEVER(2);

    override fun encode() = this.code

    companion object : Decodable<ChargingInfluence> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
        override fun values(): Set<ChargingInfluence> = ChargingInfluence.entries.toSet()
    }
}
