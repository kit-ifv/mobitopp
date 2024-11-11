package synthesis

import domain.data.Sex
import utils.csv.DefaultCsvParser
import java.io.File
import kotlin.io.path.Path
import kotlin.math.abs


data class SurveyInfo(
    val id: Int,
    val size: Int,
    val sex: Sex,
    val age: Int
)

fun parseSurvey(file: File): Sequence<SurveyInfo> {
    val parser = DefaultCsvParser { row ->
        SurveyInfo(
            row("ID").toInt(),
            row("size").toInt(),
            row("sex") { Sex.decode(it.toInt()) },
            age = row("year").toInt() - row("birthyear").toInt()
        )
    }

    return parser.parse(file)
}

class Temp(val vector: IntArray, var scalar: Double = 1.0)

class ObserverBoys(val observedIndex: Int, val vectors: List<Temp>, val expected: Double) {
    fun sum(): Double {
        return vectors.sumOf { it.vector[observedIndex] * it.scalar }
    }

    operator fun times(factor: Number): Unit {
        vectors.forEach { it.scalar *= factor.toDouble()}
    }
    val difference get() = abs(expected - sum()) / expected
    fun optimize() {
        this * (expected / sum())
    }
    override fun toString() = "actual: ${sum()} expected: $expected"
}
fun main() {
    val result = parseSurvey(Path("src/test/resources/synthesis/SurveyPopulation.csv").toFile())
    val a = result.toList()
    val households = a.groupBy { it.id }
        .mapValues { SurveyHousehold(it.value.first().id, it.value.map { person -> SurveyPerson(person.sex, person.age) }) }
//    val uniques = households.values.toSet()
//    val eqD = uniques.equivalenceClasses { hh1, hh2 -> hh1.representative == hh2.representative }.sortByValues { a, b -> b.size.compareTo(a.size)}
//    val vectors = eqD.keys.associateWith { Temp(it.representative.toVector()) }
//
//    val observers = (0..<27).map{ObserverBoys(it, vectors.values.filter{vec -> vec.vector[it] != 0})}
//    val observer = observers[0]
//    println(observer.sum())
//    observer * 2
//    println(observer.sum())
//
//
//
//
//    val householdDistribution = households.values.groupBy { it.members.size }.toSortedMap()
//    println(eqD)
}