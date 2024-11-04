package domain.data

import Buildable
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
import utils.random.StochasticActor
import kotlin.random.Random

typealias PersonId = ID<Person>

const val ADULT_AGE_GER = 18

/**
 * A person in for the simulation. Certain properties can be assumed to be known during the simulation
 * @property age The age in years
 * @property sex the Sex of the person
 * @property employment the employment state
 * @property hasLicense whether the individual is allowed to operate motor vehicles (maybe refactor if different licence
 * types become interesting)
 * @property hasCommuterTicket whether a PT ticket is present
 */
@Suppress("ComplexInterface")
@Buildable
data class Person(
    // These values can reasonably be expected for any Person to be present in the simulation
    val personId: Long,
    val household: Household,
    val age: Int,
    val employment: Employment,
    val sex: Sex,
    val graduation: Graduation,
    val income: Currency,
    val hasBike: Boolean,
    val hasCommuterTicket: Boolean,
    val hasLicense: Boolean,
    val memberships: Map<Subscribable<Person>, Boolean>,
    val eMobilityAcceptance: UnitIntervalValue,
    val chargingInfluence: ChargingInfluence,
    override val id: PersonId,
    override val random: Random,
) : Identifiable<PersonId>, Agent<Person>, StochasticActor { // TODO merge Agent and Stochastic Actor
    override var location: Location = household.location

    init {
        this.household.addMember(this)
    }

    override var nextEvent: Event<Person>? = null
    override val entity: Person = this
    lateinit var schedule: Schedule // = Schedule(TrackableModel(BlockModel()))

    private val plannedActivityList: MutableList<PlannedActivity> = mutableListOf()
    val plannedActivities: List<PlannedActivity>
        get() = plannedActivityList

    fun addActivity(plannedActivity: PlannedActivity) {
        plannedActivityList.add(plannedActivity)

        schedule.addWithPrecedingLeg(
            plannedActivity.toActivity()
        )
    }

    val isAdult: Boolean
        get() = (age >= ADULT_AGE_GER)

    var inTransit: Boolean = false

    fun sharedResources() = memberships.keys.flatMap { it.availableResourcesFor(this) }.toSet()
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
