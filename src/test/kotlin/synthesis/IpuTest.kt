package synthesis

import domain.enums.LegacyActivityType
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
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
            .mapValues {
                SurveyHousehold(
                    it.value.first().id,
                    it.value.map { person -> SurveyPerson(person.sex, person.age) })
            }
        val uniques = households.values.toSet()
        val eqD = uniques.equivalenceClasses { hh1, hh2 -> hh1.representative == hh2.representative }
            .sortByValues { a, b -> b.size.compareTo(a.size) }
        val vectors = eqD.keys.associateWith { Temp(it.representative.toVector()) }

        val observers = target.targets().withIndex().map {
            ObserverBoys(
                it.index,
                vectors.values.filter { vec -> vec.vector[it.index] != 0 },
                it.value.toDouble()
            )
        }
        val householdSizeObservers = observers.subList(0, 5)
        val ageObservers = observers.subList(5, 26)
        var counter = 0
        var epsilon = 0.01
        while (observers.maxBy { it.difference }.difference >= epsilon) {
            counter++
            householdSizeObservers.forEach { it.optimize() }
            println("${observers.maxByOrNull { it.difference }?.difference} HH $counter")

            val sorted = ageObservers.sortedByDescending { it.difference }
            sorted.forEach { it.optimize() }
            val sortedSum = sorted.sumOf { it.expected }
            val sortedActual = sorted.sumOf { it.sum() }

            ageObservers.forEach { it * (sortedSum / sortedActual) }
            println("${observers.maxByOrNull { it.difference }?.difference} Pe $counter")
        }
    }
    @Test
    fun runIPU() {

         val attractivenessTypes = setOf(
            LegacyActivityType.WORK,
            LegacyActivityType.EDUCATION_PRIMARY,
            LegacyActivityType.EDUCATION_SECONDARY,
            LegacyActivityType.EDUCATION_TERTIARY,
        )
        val targets = ZoneTarget.fromFile(Path("src/test/resources/synthesis/ZoneTargets.csv").toFile()).toList()

        val result = parseSurvey(Path("src/test/resources/synthesis/SurveyPopulation.csv").toFile())

        val attractiveness: AttractivenessModel =
            AttractivenessFromCsv(
                file = Path("src/test/resources/synthesis/attractivities.csv").toFile(), activityTypes =
                attractivenessTypes
            )


        val zones = targets.map{it.toSynZone()}
        val rules = targets.associate { it.toSynZone() to it.improvedTargets() }
        val ipu = IPU { vectors, observers ->
            var counter = 0
            while(observers.maxBy { it.difference }.difference >= 0.01 && counter < 100) {
                observers.forEach {it.optimize()}
                counter++
            }
            println("Finished after $counter iterations ${observers.joinToString(", ")}" )
            vectors
        }
        val households = result.toSurveyHouseholds()
        val syntheticHouseholds = ipu.synthesize(households.values, zones, rules) // assign location in this step
        val locatedHouseholds = AssignAroundCentroid(100.0).assign(syntheticHouseholds)

        val betterZones = zones.toLocatableZones()

        val works = betterZones.flatMap { TrivialLocation().generateLocations(it, attractivenessTypes.first(), attractiveness) }

        println(locatedHouseholds.sumOf { it.members.size })

        val schedules = locatedHouseholds.generateSchedules(TrivialActivityScheduleGeneration())


        return
        println(syntheticHouseholds)
    }


}