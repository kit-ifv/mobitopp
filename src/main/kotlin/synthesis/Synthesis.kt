package synthesis

import datastructure.Activity
import domain.data.Employment
import domain.data.Sex
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import synthesis.fixedDestinations.ZoneNumber
import units.Coordinate
import units.Currency
import units.Distance
import utils.Decodable
import utils.collections.equivalenceClasses
import utils.collections.sortByValues
import utils.csv.DefaultCsvParser
import utils.units.AbsoluteTime
import utils.units.sinceStart
import java.nio.file.Path
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.abs
import kotlin.time.Duration


data class ActivitySchedule(private val activities: MutableList<Activity>) : MutableList<Activity> by activities {
    companion object {
        operator fun invoke(
            decoder: Decodable<ActivityType> = LegacyActivityType.Companion,
            lambda: ScheduleBuilder.() -> Unit
        ): ActivitySchedule {
            val builder = ScheduleBuilder(decoder)
            builder.lambda()
            return builder.build()
        }
    }

    override fun toString(): String {
        return activities.toString()
    }

    class ScheduleBuilder(private val decoder: Decodable<ActivityType>) {
        val activities: MutableList<Activity> = mutableListOf()


        fun home(start: Duration, end: Duration) {
            extracted(start, end, "HOME")
        }

        fun work(start: Duration, end: Duration) {
            extracted(start, end, "WORK")
        }


        fun education(start: Duration, end: Duration) {
            extracted(start, end, "EDUCATION")
        }

        fun shopping(start: Duration, end: Duration) {
            extracted(start, end, "SHOPPING")
        }

        fun leisure(start: Duration, end: Duration) {
            extracted(start, end, "LEISURE")
        }

        private fun extracted(start: Duration, end: Duration, type: String) {
            activities.add(Activity.fromTimes(start, end, decoder.decode(type)))
        }

        fun build(): ActivitySchedule {
            return ActivitySchedule(activities)
        }
    }
}

fun Activity.Companion.fromTimes(start: AbsoluteTime, end: AbsoluteTime, type: ActivityType): Activity {
    require(start <= end) { "Cannot create activity where start time is larger than end time: [start=$start , end=$end]" }

    return fromDuration(LOCATIONUNKNOWN, start, end - start, type)
}

fun Activity.Companion.fromTimes(start: Duration, end: Duration, type: ActivityType): Activity {
    return fromTimes(start.sinceStart, end.sinceStart, type)
}

fun interface Matcher {
    fun matches(surveyHousehold: SurveyHousehold): Int
}

fun Boolean.toInt() = if (this) 1 else 0


interface Rule {
    val target: Int
    val name: String
    fun check(surveyHousehold: SurveyHousehold): Int

    fun appliesTo(surveyHousehold: SurveyHousehold): Boolean = check(surveyHousehold) != 0

    fun verify(output: Collection<SurveyHousehold>): Double {
        return target.toDouble() - output.sumOf { check(it) }
    }

    fun filter(target: Collection<SurveyHousehold>): List<SurveyHousehold> {
        return target.filter { appliesTo(it) }
    }
}

fun interface CountRule {
    fun matches(surveyHousehold: SurveyHousehold): Int
}

fun interface CheckRule {
    fun matches(surveyHousehold: SurveyHousehold): Boolean
}

class ZoneRule(override val name: String, override val target: Int, val matcher: CountRule) : Rule {

    override fun check(surveyHousehold: SurveyHousehold): Int {
        return matcher.matches(surveyHousehold)
    }

    override fun toString(): String {
        return "[$name] expected = $target"
    }
}

class ZoneCheckRule(override val name: String, override val target: Int, val matcher: CheckRule) : Rule {
    override fun check(surveyHousehold: SurveyHousehold): Int {
        return matcher.matches(surveyHousehold).toInt()
    }

    override fun toString(): String {
        return "[$name] expected = $target"
    }
}

fun <T> Collection<T>.pickWithReplacement(
    amount: Int,
    random: Random = Random(1)
): List<T> { // Initialize Random with a specific seed for reproducibility
    val inputList = toList()  // Convert the set to a list to enable indexing
    return List(amount) { inputList[random.nextInt(inputList.size)] }  // Sample with replacement

}

fun interface HouseholdSynthesis {
    fun synthesize(
        surveyHouseholds: Collection<SurveyHousehold>,
        targets: Collection<Zone>,
        conditions: Map<Zone, List<Rule>>
    ): Map<Zone, List<SynthesisHouseholdBuilder>>
}
typealias HouseholdEquivalence = Map<SurveyHousehold, Set<SurveyHousehold>>

class IPU(val algorithm: (vectors: Collection<ScalableVector>, Collection<Observer>) -> Collection<ScalableVector>) :
    HouseholdSynthesis {
    private var overflowCounter: Double = 0.0
    val convertNumbersToHousehold: (HouseholdEquivalence, SurveyHousehold, Double) -> List<SurveyHousehold> =
        { e, h, d ->
            val set = e.getOrElse(h) { throw NoSuchElementException("Somehow this happened") }
            overflowCounter += d - d.toInt()
            if (overflowCounter >= 1.0) {
                overflowCounter--
                set.pickWithReplacement(d.toInt() + 1)
            } else {
                set.pickWithReplacement(d.toInt())
            }


        }

    override fun synthesize(
        surveyHouseholds: Collection<SurveyHousehold>,
        targets: Collection<Zone>,
        conditions: Map<Zone, List<Rule>>
    ): Map<Zone, List<SynthesisHouseholdBuilder>> {
        val uniques = surveyHouseholds.toSet()
        //TODO equivalnece classes should be determined based on the rules
        val eqD = uniques.equivalenceClasses { hh1, hh2 -> hh1.representative == hh2.representative }
            .sortByValues { a, b -> b.size.compareTo(a.size) }

        val results = targets.associateWith { synZone ->
//            println("Working on $synZone")
            val rulesForZone = conditions.getOrDefault(synZone, emptyList())
            require(rulesForZone.isNotEmpty()) {
                "Cannot run IPU for target zone ${synZone.id}, no rules found. Rules are present for ${conditions.keys.map { it.id }}"
            }
            val internal = synZone.calculate(eqD, rulesForZone)
            val output = internal.flatMap {
                convertNumbersToHousehold(eqD, it.first, it.second)
            }
            val check = rulesForZone.associateWith { it.filter(output) }
            val otherCheck = rulesForZone.associateWith { it.verify(output) }
            output.map { it.toBuilder() }
        }
        return results
    }

    private fun Zone.calculate(
        surveyHouseholds: HouseholdEquivalence,
        rules: List<Rule>
    ): Collection<Pair<SurveyHousehold, Double>> {
        //TODO toVector should depend on the underlying ruleset instead of a hardcoded implementation.
        val vectorMapping = surveyHouseholds.keys.associateWith { rules.vectorize(it) }
        val vectors = vectorMapping.values
        // TODO maybe assign rule -> Observer so that higher order logic may interact with these.
        val observers = rules.withIndex().map { rule ->
            Observer(
                rule.value.name,
                rule.index,
                vectors.filter { it.appliesTo(rule) },
                rule.value.target
            )
        }

        val output = algorithm(vectors, observers)
        return vectorMapping.entries.map { it.key to it.value.scalar }
    }

    fun ScalableVector.appliesTo(indexRule: IndexedValue<Rule>): Boolean {
        return this.vector[indexRule.index] != 0
    }

}

class Observer(val name: String, val observedIndex: Int, val vectors: List<ScalableVector>, val expected: Int) {
    fun sum(): Double {
        return vectors.sumOf { it.vector[observedIndex] * it.scalar }
    }

    operator fun times(factor: Number): Unit {
        vectors.forEach { it.scalar *= factor.toDouble() }
    }

    val difference get() = abs(expected - sum()) / expected
    fun optimize() {
        this * (expected / sum())
    }

    override fun toString() = "[$name] difference = $difference"
}

fun List<Rule>.vectorize(surveyHousehold: SurveyHousehold): ScalableVector {
    return ScalableVector(map { it.check(surveyHousehold) }.toIntArray())
}

data class ScalableVector(val vector: IntArray, var scalar: Double = 1.0)
interface Sth
data class SynZone(val id: ZoneId, val centroid: Location = LOCATIONUNKNOWN) : Sth {
    constructor(id: Int) : this(ZoneId(id.toLong()))
    constructor(id: Int, coordinate: Coordinate) : this(ZoneId(id.toLong()), Location(coordinate, null, null))
}

data class SynRegion(val id: Int) : Sth

data class SurveyData(val id: Int) {
}

fun interface ConvertToInternalInfo {
    fun convert(): SurveyInfo
}

/**
 * This is the class that holds the data extract from the survey population csv. The file merges household and
 * person information.
 */
interface SurveyInfo {
    val householdId: Int
    val sex: Sex
    val age: Int
    val householdIncome: Currency
    val hasLicence: Boolean
    val employment: Employment
}

interface PersonInfo {
    val personId: Int
    val sex: Sex
    val age: Int
    val employment: Employment
    val hasLicence: Boolean
    var hasTransitPass: Boolean

}

/**
 * All the information from the survey file, including all irrelevant information
 */
data class RawSurveyInfo(
    override val householdId: Int,
    val year: Int,
    val areaType: Int, //TODO what is this?
    val householdSize: Int, //TODO remove. If I determine household size later over the household object, this info is useless
    val personNumber: Int,
    override val sex: Sex,
    val birthyear: Int,
    override val employment: Employment,
    val hasCommuterTicket: Boolean,
    override val householdIncome: Currency,
    val householdIncomeClass: Int, // TODO what is this?
    val type: Int, //TODO what even is this?
    val cars: Int,
    val hasBicycle: Boolean,
    override val hasLicence: Boolean,
    val distanceWork: Distance,
    val distanceEducation: Distance
) : SurveyInfo {
    override val age = year - birthyear
}

/**
 * @param converter provide a converter to determine the household income, as the reported incomes can be inaccurate.
 */
fun Sequence<SurveyInfo>.toSurveyHouseholds(converter: (List<Currency>) -> Currency = { it.first() }): Map<Int, SurveyHousehold> {
    return groupBy { it.householdId }
        .mapValues { line ->
            val income = converter(line.value.map { it.householdIncome })
//            val income = line.value.first().householdIncome
//            require(line.value.all { it.householdIncome == income}) {
//                "The input file contains mismatched information for the income of the household, the code will only proceed if all incomes are equal $line"
//            }
            SurveyHousehold(
                line.value.first().householdId,
                income,
                line.value.map { person ->
                    SurveyPerson.create(
                        person
                    )
                })
        }
}

fun main() {

    SynRegion(1)
    SynZone(1)
}

fun SurveyHousehold.amount(sex: Sex, ageCode: Int): Int {
    return members.filter { it.sex == sex && it.groupCode == ageCode }.size
}

data class ZoneTarget(
    val zoneId: ZoneId,
    val numHH1: Int,
    val numHH2: Int,
    val numHH3: Int,
    val numHH4: Int,
    val numHH5: Int,
    val ageGroup0female: Int,
    val ageGroup1female: Int,
    val ageGroup2female: Int,
    val ageGroup3female: Int,
    val ageGroup4female: Int,
    val ageGroup5female: Int,
    val ageGroup6female: Int,
    val ageGroup7female: Int,
    val ageGroup8female: Int,
    val ageGroup9female: Int,
    val ageGroup10female: Int,

    val ageGroup0male: Int,
    val ageGroup1male: Int,
    val ageGroup2male: Int,
    val ageGroup3male: Int,
    val ageGroup4male: Int,
    val ageGroup5male: Int,
    val ageGroup6male: Int,
    val ageGroup7male: Int,
    val ageGroup8male: Int,
    val ageGroup9male: Int,
    val ageGroup10male: Int,

    ) {


    fun improvedTargets(): List<Rule> {
        return listOf(
            ZoneCheckRule("HHSize == 1", numHH1) { it.members.size == 1 },
            ZoneCheckRule("HHSize == 2", numHH2) { it.members.size == 2 },
            ZoneCheckRule("HHSize == 3", numHH3) { it.members.size == 3 },
            ZoneCheckRule("HHSize == 4", numHH4) { it.members.size == 4 },
            ZoneCheckRule("HHSize == 5", numHH5) { it.members.size >= 5 },

            ZoneRule("#(Female, AgeGroup 0)", ageGroup0female) { it.amount(Sex.FEMALE, 0) },
            ZoneRule("#(Female, AgeGroup 1)", ageGroup1female) { it.amount(Sex.FEMALE, 1) },
            ZoneRule("#(Female, AgeGroup 2)", ageGroup2female) { it.amount(Sex.FEMALE, 2) },
            ZoneRule("#(Female, AgeGroup 3)", ageGroup3female) { it.amount(Sex.FEMALE, 3) },
            ZoneRule("#(Female, AgeGroup 4)", ageGroup4female) { it.amount(Sex.FEMALE, 4) },
            ZoneRule("#(Female, AgeGroup 5)", ageGroup5female) { it.amount(Sex.FEMALE, 5) },
            ZoneRule("#(Female, AgeGroup 6)", ageGroup6female) { it.amount(Sex.FEMALE, 6) },
            ZoneRule("#(Female, AgeGroup 7)", ageGroup7female) { it.amount(Sex.FEMALE, 7) },
            ZoneRule("#(Female, AgeGroup 8)", ageGroup8female) { it.amount(Sex.FEMALE, 8) },
            ZoneRule("#(Female, AgeGroup 9)", ageGroup9female) { it.amount(Sex.FEMALE, 9) },
            ZoneRule("#(Female, AgeGroup 10)", ageGroup10female) { it.amount(Sex.FEMALE, 10) },

            ZoneRule("#(Male, AgeGroup 0)", ageGroup0male) { it.amount(Sex.MALE, 0) },
            ZoneRule("#(Male, AgeGroup 1)", ageGroup1male) { it.amount(Sex.MALE, 1) },
            ZoneRule("#(Male, AgeGroup 2)", ageGroup2male) { it.amount(Sex.MALE, 2) },
            ZoneRule("#(Male, AgeGroup 3)", ageGroup3male) { it.amount(Sex.MALE, 3) },
            ZoneRule("#(Male, AgeGroup 4)", ageGroup4male) { it.amount(Sex.MALE, 4) },
            ZoneRule("#(Male, AgeGroup 5)", ageGroup5male) { it.amount(Sex.MALE, 5) },
            ZoneRule("#(Male, AgeGroup 6)", ageGroup6male) { it.amount(Sex.MALE, 6) },
            ZoneRule("#(Male, AgeGroup 7)", ageGroup7male) { it.amount(Sex.MALE, 7) },
            ZoneRule("#(Male, AgeGroup 8)", ageGroup8male) { it.amount(Sex.MALE, 8) },
            ZoneRule("#(Male, AgeGroup 9)", ageGroup9male) { it.amount(Sex.MALE, 9) },
            ZoneRule("#(Male, AgeGroup 10)", ageGroup10male) { it.amount(Sex.MALE, 10) },
        )
    }

    fun targets(): List<Int> {
        return listOf(
            numHH1,
            numHH2,
            numHH3,
            numHH4,
            numHH5,
            ageGroup0female,
            ageGroup1female,
            ageGroup2female,
            ageGroup3female,
            ageGroup4female,
            ageGroup5female,
            ageGroup6female,
            ageGroup7female,
            ageGroup8female,
            ageGroup9female,
            ageGroup10female,

            ageGroup0male,
            ageGroup1male,
            ageGroup2male,
            ageGroup3male,
            ageGroup4male,
            ageGroup5male,
            ageGroup6male,
            ageGroup7male,
            ageGroup8male,
            ageGroup9male,

            )
    }

    companion object {
        fun fromFile(file: Path): Sequence<ZoneTarget> {
            val offset = 4
            val parser = DefaultCsvParser { row ->
                ZoneTarget(
                    zoneId = row.valueAt(0) { ZoneNumber.parse(it).toZoneId() },

                    numHH1 = row.valueAt(1).toInt(),
                    numHH2 = row.valueAt(2).toInt(),
                    numHH3 = row.valueAt(3).toInt(),
                    numHH4 = row.valueAt(4).toInt(),
                    numHH5 = row.valueAt(5).toInt(),

                    ageGroup0female = row.valueAt(6 + offset).toInt(),
                    ageGroup1female = row.valueAt(7 + offset).toInt(),
                    ageGroup2female = row.valueAt(8 + offset).toInt(),
                    ageGroup3female = row.valueAt(9 + offset).toInt(),
                    ageGroup4female = row.valueAt(10 + offset).toInt(),
                    ageGroup5female = row.valueAt(11 + offset).toInt(),
                    ageGroup6female = row.valueAt(12 + offset).toInt(),
                    ageGroup7female = row.valueAt(13 + offset).toInt(),
                    ageGroup8female = row.valueAt(14 + offset).toInt(),
                    ageGroup9female = row.valueAt(15 + offset).toInt(),
                    ageGroup10female = row.valueAt(16 + offset).toInt(),

                    ageGroup0male = row.valueAt(17 + offset).toInt(),
                    ageGroup1male = row.valueAt(18 + offset).toInt(),
                    ageGroup2male = row.valueAt(19 + offset).toInt(),
                    ageGroup3male = row.valueAt(20 + offset).toInt(),
                    ageGroup4male = row.valueAt(21 + offset).toInt(),
                    ageGroup5male = row.valueAt(22 + offset).toInt(),
                    ageGroup6male = row.valueAt(23 + offset).toInt(),
                    ageGroup7male = row.valueAt(24 + offset).toInt(),
                    ageGroup8male = row.valueAt(25 + offset).toInt(),
                    ageGroup9male = row.valueAt(26 + offset).toInt(),
                    ageGroup10male = row.valueAt(27 + offset).toInt(),

                    )
            }
            return parser.parse(file.toFile())
        }
    }
}


data class SurveyPerson<T: SurveyInfo>(
    val personId: Int,
    val information: T
): SurveyInfo by information {

    override val sex: Sex = information.sex
    override val age: Int = information.age
    override val employment: Employment = information.employment
    override val hasLicence: Boolean = information.hasLicence
    val representative = toRepresentative()
    private fun toRepresentative(): PersonRepresentative {
        return PersonRepresentative.fromData(sex, age)
    }

    val groupCode: Int
        get() {
            val groupCode = when (age) {
                in 0..5 -> 0
                in 6..9 -> 1
                in 10..14 -> 2
                in 15..17 -> 3
                in 18..24 -> 4
                in 25..29 -> 5
                in 30..44 -> 6
                in 45..59 -> 7
                in 60..64 -> 8
                in 65..74 -> 9
                in 75..Int.MAX_VALUE -> 10
                else -> throw NoSuchElementException("Negative Age cannot be translated to a group code person=$this")
            }
            return groupCode
        }

    companion object {
        private var idCounter: Int = 0
        fun <T: SurveyInfo> create(
            information: T
        ) = SurveyPerson(idCounter++, information)
//        fun <T: PersonInfo> create(
//            sex: Sex,
//            age: Int,
//            employment: Employment,
//            driverLicence: Boolean
//        ): SurveyPerson<T> {
//            return SurveyPerson(idCounter++, sex, age, employment, driverLicence)
//        }
    }
}