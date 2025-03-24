import domain.data.EconomicStatus
import domain.data.Sex
import domain.location.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import synthesis.AlwaysAssignSameStatus
import synthesis.AssignAroundZoneCentroid
import synthesis.SmallestSurveyPerson
import synthesis.SurveyHousehold
import synthesis.TrivialCarGeneration
import synthesis.activityGeneration.TrivialActivityGeneration
import synthesis.carownership.AlwaysAssignFixedNumber
import synthesis.fixedDestinations.AssignedLocation
import synthesis.fixedDestinations.SimpleGroupLocator
import synthesis.fixedDestinations.UseClosestLocation
import synthesis.householdgeneration.TrivialSynthesis
import units.euros
import units.meters
import usecases.LegacyActivityType
import kotlin.io.path.Path
import kotlin.test.Test

private interface MinimalSurveyInformation {
    val age: Int
    val sex: Sex
}

private data class MinimalSurveyInstantiation(override val age: Int, override val sex: Sex) : MinimalSurveyInformation

private fun Collection<MinimalSurveyInformation>.toSurveyHouseholds(): List<SurveyHousehold<MinimalSurveyInformation>> {
    return map {
        SurveyHousehold(
            -1,
            0.euros,
            members = listOf(
                SmallestSurveyPerson(personId = -1, age = 10, sex = Sex.MALE, information = it)
            )
        )
    }
}

class PopulationSynthesisKtTest {
    private val child = MinimalSurveyInstantiation(10, Sex.MALE)
    private val working = MinimalSurveyInstantiation(30, Sex.MALE)
    private val senior = MinimalSurveyInstantiation(99, Sex.FEMALE)

    private inner class TrivialTestGeneration : GenerateArtificialPopulation<MinimalSurveyInformation> {
        override fun generateArtificialPopulation(): Collection<MinimalSurveyInformation> {
            return listOf(child, working, senior)
        }
    }

    @Test
    @Suppress("LongMethod") // This method may be long, it is the entire execution of a population synthesis
    fun runWithDebug() {
        val bielefeld = Location(BIELEFELD, null, null)
        val itzehoe = Location(ITZEHOE, null, null)
        val schweinfurt = Location(SCHWEINFURT, null, null)
        val populationSynthesis = PopulationSynthesis.configure(
            surveyPopulation = TrivialTestGeneration()

        ) {
            outputDirectory = Path("src/test/resources/tempOutput")
            zones = listOf(TEST_ZONE)
            rules = emptyList()
            surveyHouseholds = surveyPopulation.toSurveyHouseholds()
            // TODO make this a code based attractiveness model instead of parsing a file.
            attractivenessModel = attractivenessFromFile {
                file = Path("src/test/resources/synthesis/attractivities.csv")
                activityTypes = setOf(LegacyActivityType.EDUCATION_PRIMARY)
            }
        }

        val primarySchools: List<Location> =
            populationSynthesis.generateLocations(LegacyActivityType.EDUCATION_PRIMARY, amount = 1)

        require(primarySchools.isNotEmpty()) {
            "Somehow no primary schools are generated"
        }

        val work = LegacyActivityType.WORK
        val workLocations = populationSynthesis.generateLocations(work, amount = 1)

        populationSynthesis.execute {
            synthesis(mapOf(TEST_ZONE to emptyList())) {
                TrivialSynthesis()
            }
            val test = householdsByZone
            assertTrue(TEST_ZONE in test.keys)
            val hh = test[TEST_ZONE]!!
            assertEquals(hh.size, 3)
            val hh1 = hh[0]
            val p1 = hh1.members.first()
            val hh2 = hh[1]
            val p2 = hh2.members.first()
            val hh3 = hh[2]
            val p3 = hh3.members.first()
            assertEquals(
                p1.info,
                child
            )

            assertEquals(
                p2.info,
                working
            )
            assertEquals(
                p3.info,
                senior
            )
            assertFalse(hh1.locationIsAssigned())
            assertFalse(hh2.locationIsAssigned())
            assertFalse(hh3.locationIsAssigned())
            assignLocations {
                AssignAroundZoneCentroid(100.meters)
            }
            assertEquals(hh1.location.requireZone(), TEST_ZONE)
            assertEquals(hh2.location.requireZone(), TEST_ZONE)
            assertEquals(hh3.location.requireZone(), TEST_ZONE)

            assertFalse(hh1.economicStatusIsAssigned())
            assertFalse(hh2.economicStatusIsAssigned())
            assertFalse(hh3.economicStatusIsAssigned())

            assignEconomicStatus {
                AlwaysAssignSameStatus(economicStatus = EconomicStatus.MIDDLE)
            }

            assertEquals(hh1.economicStatus, EconomicStatus.MIDDLE)
            assertEquals(hh2.economicStatus, EconomicStatus.MIDDLE)
            assertEquals(hh3.economicStatus, EconomicStatus.MIDDLE)
            // Assign Amount of Cars.

            assertEquals(hh1.amountOfCars, 0)
            assertEquals(hh2.amountOfCars, 0)
            assertEquals(hh3.amountOfCars, 0)

            assignAmountOfCars {
                AlwaysAssignFixedNumber(1)
            }
            assertEquals(hh1.amountOfCars, 1)
            assertEquals(hh2.amountOfCars, 1)
            assertEquals(hh3.amountOfCars, 1)

            assertEquals(p1.hasTransitPass, false)
            assertEquals(p2.hasTransitPass, false)
            assertEquals(p3.hasTransitPass, false)
            // Assign Transit Card Ownership
            assignTransitCardOwnership {
                AlwaysAssignTransitPass
            }
            assertEquals(p1.hasTransitPass, true)
            assertEquals(p2.hasTransitPass, true)
            assertEquals(p3.hasTransitPass, true)
            // Assign preliminary activity schedule
            assertTrue(p1.plannedActivities.isEmpty())
            assertTrue(p2.plannedActivities.isEmpty())
            assertTrue(p3.plannedActivities.isEmpty())
            assignActivities {
                TrivialActivityGeneration()
            }
            assertEquals(p1.plannedActivities.size, 5)
            assertEquals(p2.plannedActivities.size, 5)
            assertEquals(p3.plannedActivities.size, 5)

            // Assign fixed Destinations
            assertTrue(p1.fixedDestinations.isEmpty())
            assertTrue(p2.fixedDestinations.isEmpty())
            assertTrue(p3.fixedDestinations.isEmpty())

            assignFixedDestinations {
                forActivity {
                    activityType = work
                    filter = { it.plannedActivities.any { it.type == activityType } }
                    assignmentStrategy = UseClosestLocation(workLocations)
                }
                /* We can also assign a fixed location for stuff that is not even included in the activity plan, like
                here where no agent has a "PICK_UP_PARCEL" activity.
                 */
                assertTrue(p1.plannedActivities.none { it.type == LegacyActivityType.PICK_UP_PARCEL })
                assertTrue(p2.plannedActivities.none { it.type == LegacyActivityType.PICK_UP_PARCEL })
                assertTrue(p3.plannedActivities.none { it.type == LegacyActivityType.PICK_UP_PARCEL })
                forActivity {
                    activityType = LegacyActivityType.PICK_UP_PARCEL
                    filter = { true }
                    /*
                    We can code within the execution block, if we so desire.
                     */

                    val locations: List<Location> = listOf(bielefeld, itzehoe, schweinfurt)
                    assignmentStrategy = SimpleGroupLocator { persons ->

                        persons.zip(locations) { p, l -> AssignedLocation(p, l) }
                    }
                }
            }

            assertEquals(p1.fixedDestinations.size, 2)
            assertEquals(p2.fixedDestinations.size, 2)
            assertEquals(p3.fixedDestinations.size, 2)
            assertEquals(p1.fixedDestinations[LegacyActivityType.PICK_UP_PARCEL], bielefeld)
            assertEquals(p2.fixedDestinations[LegacyActivityType.PICK_UP_PARCEL], itzehoe)
            assertEquals(p3.fixedDestinations[LegacyActivityType.PICK_UP_PARCEL], schweinfurt)
            assertEquals(p1.fixedDestinations[LegacyActivityType.WORK], workLocations.first())
            assertEquals(p2.fixedDestinations[LegacyActivityType.WORK], workLocations.first())
            assertEquals(p3.fixedDestinations[LegacyActivityType.WORK], workLocations.first())

            assertTrue(hh1.cars.isEmpty())
            assertTrue(hh2.cars.isEmpty())
            assertTrue(hh3.cars.isEmpty())
            generateCars(strategy = TrivialCarGeneration)
            assertEquals(hh1.cars.size, hh1.amountOfCars)
            assertEquals(hh2.cars.size, hh2.amountOfCars)
            assertEquals(hh3.cars.size, hh3.amountOfCars)
        }
    }
}
