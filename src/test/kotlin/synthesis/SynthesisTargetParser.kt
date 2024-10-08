package synthesis

import utils.csv.DefaultCsvParser
import java.io.File
import kotlin.io.path.Path


data class Target(
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
}

fun parseTargets(file: File): Sequence<Target> {
    val offset = 4
    val parser = DefaultCsvParser { row ->
        Target(
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

fun main() {
    val result = parseTargets(Path("src/test/resources/synthesis/ZoneTargets.csv").toFile())
    val a = result.toList()
    println(a)
}