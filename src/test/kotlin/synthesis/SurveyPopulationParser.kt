package synthesis

import domain.data.Sex
import utils.collections.HouseholdRepresentative
import utils.collections.PersonRepresentative
import utils.collections.equivalenceClasses
import utils.collections.sortByValues
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

data class SurveyHousehold(val id: Int, val members: List<SurveyPerson>) {
    val representative = toRepresentative()
    private fun toRepresentative(): HouseholdRepresentative  {
        val memberCount =  members.map { it.representative }.groupingBy { it }.eachCount().map{(element, count) -> Pair(count, element)}.toSet()
        return HouseholdRepresentative(memberCount)
    }
}

data class SurveyPerson(
    val sex: Sex,
    val age: Int
) {
    val representative = toRepresentative()
    private fun toRepresentative(): PersonRepresentative {
        return PersonRepresentative.fromData(sex, age)
    }
}
fun verifyVector(intArray: IntArray) : Boolean {
    if(intArray[0] + intArray[1] + intArray[2] + intArray[3] + intArray[4] != 1) return false
    if(intArray[0] == 1) {
        if(intArray.sum() != 2) return false
        assert(intArray.sum() == 2)
    }

    if(intArray[1] == 1) {
        if(intArray.sum() != 3) return false
    }
    if(intArray[2] == 1) {
        if(intArray.sum() != 4) return false
        assert(intArray.sum() == 4)
    }
    if(intArray[3] == 1) {
        if(intArray.sum() != 5) return false
        assert(intArray.sum() == 5)
    }
    if(intArray[4] == 1) {
        if(intArray.sum() < 6) return false
        assert(intArray.sum() >= 6)
    }
    return true
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