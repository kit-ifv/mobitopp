import domain.shared.enums.LegacyActivityType
import domain.shared.enums.legacyChoiceModelPurposes
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import domain.synthesis.algorithms.TrivialSynthesis
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.household.MinimumHouseholdAttributesImpl
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.HouseholdFactory
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SmallestSurveyPerson
import domain.synthesis.behavior.SurveyHousehold
import domain.synthesis.behavior.activityGeneration.TrivialActivityGeneration
import domain.synthesis.behavior.cars.amount.AlwaysAssignFixedNumber
import domain.synthesis.behavior.cars.generation.TrivialCarGeneration
import domain.synthesis.behavior.cars.ownership.UnfilteredSeniority
import domain.synthesis.behavior.economicstatus.AlwaysAssignSameStatus
import domain.synthesis.behavior.fixedDestinations.SimpleGroupLocator
import domain.synthesis.behavior.fixedDestinations.UseClosestLocation
import domain.synthesis.behavior.householdlocation.AssignAroundZoneCentroid
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.HouseholdType
import domain.synthesis.data.Sex
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.meters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.io.path.Path
import kotlin.test.Test

private interface MinimalSurveyInformation : MinimumPersonAttributes {
    override val age: Int
    override val sex: Sex
}

private data class MinimalSurveyInstantiation(override val age: Int, override val sex: Sex) : MinimalSurveyInformation

private fun Collection<MinimalSurveyInformation>.toSurveyHouseholds():
    List<SurveyHousehold<MinimumHouseholdAttributes, MinimumPersonAttributes>> {
    return map {
        SurveyHousehold(
            surveyHouseholdId = -1,

            members = listOf(
                SmallestSurveyPerson(personId = -1, attributes = it),

            ),
            attributes = MinimumHouseholdAttributesImpl(
                income = 1.euros,
                type = HouseholdType.SINGLE_HH
            )

        )
    }
}

class PopulationSynthesisKtTest {
    private val child = MinimalSurveyInstantiation(10, Sex.MALE)
    private val working = MinimalSurveyInstantiation(30, Sex.MALE)
    private val senior = MinimalSurveyInstantiation(99, Sex.FEMALE)

    private inner class TrivialTestGeneration :
        GenerateHouseholds<MinimumHouseholdAttributes, MinimumPersonAttributes> {

        override fun generateSurveyHouseholds():
            Collection<ISurveyHousehold<MinimumHouseholdAttributes, MinimumPersonAttributes>> {
            return listOf(child, working, senior).toSurveyHouseholds()
        }
    }

    @Test
    @Suppress("LongMethod") // This method may be long, it is the entire execution of a population synthesis
    fun runWithDebug() {
        val bielefeld = StandardLocation.fromWGS(BIELEFELD)
        val itzehoe = StandardLocation.fromWGS(ITZEHOE)
        val schweinfurt = StandardLocation.fromWGS(SCHWEINFURT)
        val zones = listOf(TEST_ZONE)
        val populationSynthesis = PopulationSynthesis.configure(
            surveyPopulation = TrivialTestGeneration(),
            zones = zones
        ) {
            outputDirectory = Path("src/test/resources/tempOutput")
            surveyHouseholds = surveyPopulation
            // TODO make this a code based attractiveness model instead of parsing a file.
            attractivenessModel = attractivenessFromFile {
                path = Path("src/test/resources/synthesis/attractivities.csv")
                purposes = legacyChoiceModelPurposes
            }
        }

        val primarySchools: List<StandardLocation> =
            populationSynthesis.generateLocations(LegacyActivityType.EDUCATION_PRIMARY) { zone, _, _ ->
                zone.generateLocations(amount = 1)
            }

        require(primarySchools.isNotEmpty()) {
            "Somehow no primary schools are generated"
        }

        val work = LegacyActivityType.WORK
        val workLocations = populationSynthesis.generateLocations(work) { zone, _, _ ->
            zone.generateLocations(amount = 1)
        }

        populationSynthesis.execute {
            refactoredPopsyn({ it }) {
                TrivialSynthesis(
                    surveyHouseholds.map
                        { HouseholdFactory.createFrom(it) },
                    zones
                )
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
                p1.age,
                child.age
            )

            assertEquals(
                p2.age,
                working.age
            )
            assertEquals(
                p3.age,
                senior.age
            )
            assertFalse(hh1.locationIsAssigned())
            assertFalse(hh2.locationIsAssigned())
            assertFalse(hh3.locationIsAssigned())
            assignLocations {
                AssignAroundZoneCentroid(100.meters)
            }
            assertEquals(hh1.attributes.location.zoneID, TEST_ZONE.id)
            assertEquals(hh2.attributes.location.zoneID, TEST_ZONE.id)
            assertEquals(hh3.attributes.location.zoneID, TEST_ZONE.id)

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
                    filter = { true }
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

                    val locations: List<StandardLocation> = listOf(bielefeld, itzehoe, schweinfurt)
                    assignmentStrategy = SimpleGroupLocator { persons ->

                        persons.zip(locations) { p, l -> l }
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
            assignCars(generationStrategy = TrivialCarGeneration(), assignStrategy = UnfilteredSeniority())
            assertEquals(hh1.cars.size, hh1.amountOfCars)
            assertEquals(hh2.cars.size, hh2.amountOfCars)
            assertEquals(hh3.cars.size, hh3.amountOfCars)
        }
    }
}

private fun Zone.generateLocations(amount: Int): List<StandardLocation> {
    return (0 until amount).map {
        StandardLocation(centroid.position, this, RoadAccess.INVALID)
    }
}
