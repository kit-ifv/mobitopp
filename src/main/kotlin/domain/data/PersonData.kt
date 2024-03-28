package domain.data

import Buildable
import Builder
import Decodable
import Encodable
import units.Currency
import units.UnitIntervalValue
import utils.ID
import utils.Identifiable
import utils.registerId

typealias PersonId = ID<PersonData>

/**
 * A person in for the simulation. Certain properties can be assumed to be known during the simulation
 * @property age The age in years
 * @property sex the Sex of the person
 * @property employment the employment state
 * @property hasLicense whether the individual is allowed to operate motor vehicles (maybe refactor if different licence
 * types become interesting)
 * @property hasCommuterTicket whether a PT ticket is present
 */
interface PersonData: Identifiable<PersonId> {
    // These values can reasonably be expected for any Person to be present in the simulation
    val personId: Long
    val householdData: HouseholdData
    val age: Int
    val employment: Employment
    val sex: Sex
    val graduation: Graduation
    val income: Currency
    val hasBike: Boolean
    val hasCommuterTicket: Boolean
    val hasLicense: Boolean

    val memberships: Map<String, Boolean>

    val isAdult: Boolean
        get() = (age >= 18)

}

@Buildable
interface EMobilityPersonData: PersonData {
    val eMobilityAcceptance: UnitIntervalValue
    val chargingInfluence: ChargingInfluence
}

@Suppress("LongParameterList")
class EMobilityPersonDataBuilder(
    var personId: Long? = null,
    var eMobilityAcceptance: UnitIntervalValue? = null,
    var chargingInfluence: ChargingInfluence? = null,
    var householdData: HouseholdData? = null,
    var age: Int? = null,
    var employment: Employment? = null,
    var sex: Sex? = null,
    var graduation: Graduation? = null,
    var income: Currency? = null,
    var hasBike: Boolean? = null,
    var hasCommuterTicket: Boolean? = null,
    var hasLicense: Boolean? = null,
    var memberships: MutableMap<String, Boolean> = mutableMapOf()
): Builder<EMobilityPersonData> {

    override fun build(): EMobilityPersonData {
        return object:EMobilityPersonData {
            override val personId = this@EMobilityPersonDataBuilder.personId!!
            override val eMobilityAcceptance = this@EMobilityPersonDataBuilder.eMobilityAcceptance!!
            override val chargingInfluence = this@EMobilityPersonDataBuilder.chargingInfluence!!
            override val householdData = this@EMobilityPersonDataBuilder.householdData!!
            override val age = this@EMobilityPersonDataBuilder.age!!
            override val employment = this@EMobilityPersonDataBuilder.employment!!
            override val sex = this@EMobilityPersonDataBuilder.sex!!
            override val graduation = this@EMobilityPersonDataBuilder.graduation!!
            override val income = this@EMobilityPersonDataBuilder.income!!
            override val hasBike = this@EMobilityPersonDataBuilder.hasBike!!
            override val hasCommuterTicket = this@EMobilityPersonDataBuilder.hasCommuterTicket!!
            override val hasLicense = this@EMobilityPersonDataBuilder.hasLicense!!
            override val memberships: Map<String, Boolean> = this@EMobilityPersonDataBuilder.memberships
            override val id: ID<PersonData> = registerId(personId)

            init {
                this.householdData.addMember(this)
            }

        }
    }

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
    companion object: Decodable<Sex> {
        override fun decode(i: Int) = entries.first {it.code == i}
        override fun decode(s: String) = valueOf(s)
    }

}

/**
 * An enum for the employment of a person. As this class is only applicable to a person the enum resides in the same
 * source code file as the person. (Refactor Idea maybe make an inner class)
 */
enum class Employment(private val code: Int): Encodable {
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

    companion object: Decodable<Employment> {
        override fun decode(i: Int) = entries.first {it.code == i}
        override fun decode(s: String) = valueOf(s)
    }

}


enum class Graduation(private val code: Int): Encodable { //TODO split into school and higher education
    UNDEFINED(-1),
    OTHER(0),
    NOT_HIGH_SCHOOL(1),
    HIGH_SCHOOL_GRADUATE(2),
    SOME_COLLEGE_CREDIT_NO_DEGREE(3),
    ASSOCIATE_TECHNICAL_SCHOOL_DEGREE(4),
    BACHELOR_DEGREE(5),
    MASTER_DEGREE(6);

    override fun encode() = this.code

    companion object: Decodable<Graduation> {
        override fun decode(i: Int) = entries.first {it.code == i}
        override fun decode(s: String) = valueOf(s)
    }
}

enum class ChargingInfluence(private val code: Int): Encodable {
    ALWAYS(0),
    ONLY_WHEN_BATTERY_LOW(1),
    NEVER(2);

    override fun encode() = this.code

    companion object: Decodable<ChargingInfluence> {
        override fun decode(i: Int) = entries.first {it.code == i}
        override fun decode(s: String) = valueOf(s)
    }
}
