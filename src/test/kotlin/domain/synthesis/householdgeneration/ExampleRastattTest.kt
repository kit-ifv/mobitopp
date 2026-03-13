package domain.synthesis.householdgeneration

import domain.shared.location.ZoneId
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.behavior.householdgeneration.Rule
import domain.synthesis.behavior.householdgeneration.ZoneCheckRule
import domain.synthesis.behavior.householdgeneration.ZoneRule
import domain.synthesis.data.Sex
import utils.csv.DefaultCsvParser
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class ExampleRastattTest {

    @Test
    fun generateProperRulesFromInput() {
        val targets = ZoneTarget.fromFile("src/test/resources/synthesis/ZoneTargets.csv").toList()
        assertEquals(targets.size, 47)
    }
}

/**
 * This is the example class building conditions from the input file found in the legacy rastatt code. The targets match
 * the description of src/test/resources/zoneTargets.csv.
 *
 * The Zones match src/test/resources/zones.csv
 * */
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

    fun improvedTargets(): List<Rule<ISurveyHousehold<out Any>>> {
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
        fun fromFile(string: String): Sequence<ZoneTarget> = fromFile(Path.of(string))
        fun fromFile(file: Path): Sequence<ZoneTarget> {
            val offset = 4
            val parser = DefaultCsvParser { row ->
                ZoneTarget(
                    zoneId = row.valueAt(0) { ZoneId(it.toLong()) },

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
            return parser.parse(file)
        }
    }
}

val SurveyPerson<out Any>.groupCode
    get() = when (age) {
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

fun ISurveyHousehold<out Any>.amount(sex: Sex, ageCode: Int): Int {
    return members.filter { it.sex == sex && it.groupCode == ageCode }.size
}
