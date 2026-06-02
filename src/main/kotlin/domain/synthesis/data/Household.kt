package domain.synthesis.data

import Mutable
import domain.jackson.BinaryWritable
import domain.jackson.Simplifiable
import domain.shared.location.StandardLocation
import domain.shared.location.toDTO
import domain.synthesis.parser.binary.LocationUtils.encodeLocation
import domain.synthesis.parser.binary.ZonedRoadAccessLocationDTO
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.CurrencyUnit
import kotlinx.serialization.Serializable
import utils.Decodable
import utils.Encodable
import utils.EnumDecodable
import utils.Identifiable
import utils.random.StochasticActor
import java.io.DataOutputStream
import kotlin.random.Random

@Serializable
@JvmInline
value class HouseholdId(val value: Long) : Comparable<HouseholdId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: HouseholdId): Int = value.compareTo(other.value)

    /**
     * Robin: I added a method to iterate over ids, I want to use this feature for generating autoincrementing ids
     * in the test cases
     *
     * @return the next higher id.
     */
    fun next(): HouseholdId = HouseholdId(value + 1)
}
interface HasStandardLocation {
    val location: StandardLocation
}
interface IHousehold :
    Identifiable<HouseholdId>,
    StochasticActor,
    Simplifiable<HouseholdBinaryRecord>,
    HasStandardLocation {
    val householdNumber: Long
    val surveyYear: Int
    override val location: StandardLocation
    val domCode: Int
    val type: Int
    val incomePerMonth: Currency
    val economicStatus: EconomicStatus
    val members: Set<IPerson>
    val cars: Set<IPrivateCar>

    override fun simplify(): HouseholdBinaryRecord = HouseholdBinaryRecord(
        id.value,
        householdNumber,
        surveyYear,
        domCode,
        type,
        incomePerMonth.toDouble(CurrencyUnit.EUROS),
        economicStatus.code,
        location.toDTO(),

    )
}

@Mutable
abstract class Household(override val id: HouseholdId, seed: Long) : IHousehold {

    final override val random: Random by lazy { Random(id.value + seed) }

    abstract override val members: Set<Person>
    abstract override val cars: Set<PrivateCar>
}

data class HouseholdBinaryRecord(
    val id: Long,
    val householdNumber: Long,
    val surveyYear: Int,
    val domCode: Int,
    val type: Int,
    val incomePerMonth: Double,
    val economicStatusCode: Int,
    val location: ZonedRoadAccessLocationDTO,
) : BinaryWritable {
    override fun writeTo(outStream: DataOutputStream) {
        outStream.run {
            writeLong(id)
            writeLong(householdNumber)
            writeInt(surveyYear)
            writeInt(domCode)
            writeInt(type)
            writeDouble(incomePerMonth)
            writeInt(economicStatusCode)
            encodeLocation(location)
        }
    }
}

/**
 * The economic status as taken from the original mobiTopp codebase
 */
enum class EconomicStatus(override val code: Int) : Encodable {
    VERY_LOW(1),
    LOW(2),
    MIDDLE(3),
    HIGH(4),
    VERY_HIGH(5),
    ;

    override val description: String = name

    companion object : EnumDecodable<EconomicStatus>(EconomicStatus::class)
}

enum class HouseholdType(override val code: Int) : Encodable {

    SINGLE_HH_WITH_CHILDREN(1),
    SINGLE_HH(2),
    COUPLE_WITH_CHILDREN(3),
    COUPLE_WITHOUT_CHILDREN(4),
    OTHER_MULTI_PERSON_HH(5),
    UNDEFINED(-1),
    ;

    override val description: String = name

    companion object : Decodable<HouseholdType> {

        val validTypes = setOf(
            SINGLE_HH_WITH_CHILDREN,
            SINGLE_HH,
            COUPLE_WITH_CHILDREN,
            COUPLE_WITHOUT_CHILDREN,
            OTHER_MULTI_PERSON_HH,
        )

        private val mapping = HouseholdType.entries.associateBy(HouseholdType::code).toMutableMap()

        private val toSet = HouseholdType.entries.toSet()
        override fun values(): Set<HouseholdType> = toSet

        override fun decode(i: Int): HouseholdType = mapping[i] ?: UNDEFINED.also {
            println("Code $i is an undefined Household Type.")
            mapping[i] = it // So that later occurences do not get printed
        }
    }
}
