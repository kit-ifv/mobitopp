package synthesis

import utils.collections.equivalenceClasses
import utils.collections.sortByValues
import kotlin.io.path.Path
import kotlin.test.Test

class IpuTest {

    @Test
    fun example() {
        val targets = ZoneTarget.fromFile(Path("src/test/resources/synthesis/ZoneTargets.csv").toFile()).toList()
        val target = targets[0]
        val result = parseSurvey(Path("src/test/resources/synthesis/SurveyPopulation.csv").toFile())
        val households = result.groupBy { it.id }
            .mapValues { SurveyHousehold(it.value.first().id, it.value.map { person -> SurveyPerson(person.sex, person.age) }) }
        val uniques = households.values.toSet()
        val eqD = uniques.equivalenceClasses { hh1, hh2 -> hh1.representative == hh2.representative }.sortByValues { a, b -> b.size.compareTo(a.size)}
        val vectors = eqD.keys.associateWith { Temp(it.representative.toVector()) }

        val observers = target.targets().withIndex().map{ObserverBoys(it.index, vectors.values.filter{vec -> vec.vector[it.index] != 0}, it.value.toDouble())}
        val householdSizeObservers = observers.subList(0, 5)
        val ageObservers = observers.subList(5, 26)
        var counter = 0
        var epsilon = 0.01
        while(observers.maxBy{it.difference}.difference >= epsilon) {
            counter++
            householdSizeObservers.forEach { it.optimize() }
            println("${observers.maxByOrNull { it.difference }?.difference} HH $counter")

            val sorted = ageObservers.sortedByDescending { it.difference }
            sorted.forEach { it.optimize() }
            val sortedSum = sorted.sumOf { it.expected }
            val sortedActual = sorted.sumOf {it.sum()}

            ageObservers.forEach { it * (sortedSum / sortedActual) }
            println("${observers.maxByOrNull { it.difference }?.difference} Pe $counter")
        }

//        repeat(10) {
//            counter++
//            householdSizeObservers.forEach { it.optimize() }
//            println("${observers.maxByOrNull { it.difference }?.difference} HH $counter")
//
//            val sorted = ageObservers.sortedByDescending { it.difference }
//            sorted.forEach { it.optimize() }
//            val sortedSum = sorted.sumOf { it.expected }
//            val sortedActual = sorted.sumOf {it.sum()}
//
//            ageObservers.forEach { it * (sortedSum / sortedActual) }
//            println("${observers.maxByOrNull { it.difference }?.difference} Pe $counter")
//
//
//
//        }

    }
}