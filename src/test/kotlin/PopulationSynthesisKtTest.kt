import domain.enums.LegacyActivityType
import domain.location.Location
import synthesis.AssignAroundZoneCentroid
import synthesis.TrivialCarGeneration
import kotlin.io.path.Path
import kotlin.test.Test

object TrivialTestGeneration: GenerateArtificialPopulation<Any> {
    override fun generateArtificialPopulation(): Collection<Any> {
        return emptySet()
    }

}

class PopulationSynthesisKtTest {

    @Test
    fun runWithDebug() {

        val populationSynthesis = PopulationSynthesis.configure(
            surveyPopulation = TrivialTestGeneration

        ) {

            outputDirectory = Path("src/test/resources/tempOutput")
            zones = listOf(TEST_ZONE)
            surveyHouseholds = TODO() // Cannot use "surveyPopulation.toSurveyHouseholds()"
//            parseSurvey(Path("src/test/resources/synthesis/SurveyPopulation.csv")).toSurveyHouseholds().values
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
        populationSynthesis.execute {
            householdsByZone = mapOf(TEST_ZONE to listOf())
            synthesis(populationSynthesis.randsums) {
                TODO("Don't have a trivial ipu implementation yet")
            }
//                IPU { vectors, observers ->
//                    var counter = 0
//                    while (observers.maxBy { it.difference }.difference >= 0.01 && counter < 100) {
//                        observers.forEach { it.optimize() }
//                        counter++
//                    }
//                    vectors
//                }
//            }
            assignLocations {
                AssignAroundZoneCentroid(100.0)
            }


            assignEconomicStatus {
                TODO("OECDAssigner is too strong of an assigmnent strategy and I need a default implementation fo")
//                strategy = OECDAssigner.fromPath(
//                    Path("src/test/resources/synthesis/economical-status-oecd2017.csv")
//                )
            }

            assignAmountOfCars {
                TODO("AssignViaRegionType is too strong and requires survey Info")
//                AssignViaRegionType.create {
//                    model = carChoiceModel
//                    cityParameters = carOwnershipCityParameters
//                    smallTownParameters = carOwnershipSmallCity
//                    urbanAreaParameters = carOwnershipUrbanAreaParameters
//                    ruralAreaParameters = carOwnershipRuralArea
//                }

            }

            assignTransitCardOwnership {
                TODO("Assignment via transit card is too strong, write an any implementation as default")
//                choiceModel = transitPassDiscreteChoiceModel
            }


            fixedDestinations {
                TODO("Similar reason, there are no algorithms for fixed destinations")
//                primarySchool {
//                    activityType = LegacyActivityType.EDUCATION_PRIMARY
//                    assignmentStrategy = UseClosestLocation(primarySchools)
//                }
//                secondarySchool {
//                    activityType = LegacyActivityType.EDUCATION_SECONDARY
//                    assignmentStrategy = UseBandwidthLocation(primarySchools, attractivenessModel)
//                }
//                work {
//                    activityType = LegacyActivityType.WORK
//                    assignmentStrategy = distanceBasedCommunity {
//                        communityMapping = Path("src/test/resources/synthesis/zone-to-community.csv")
//                        commuterFile = Path("src/test/resources/synthesis/commuters-rastatt.csv")
//                        strategy = GREEDY_BY_DISTANCE
//                        locationInZone = DebugZoneAssigner
//                    }
//                }
            }
//        generateCars (TrivialCarGeneration::generateCars)
            generateCars (strategy = TrivialCarGeneration)
            assignActivities {
                TODO("Cannot use actitopp, need a generic implem,entation")
//                ActitoppGenerator()
            }


        }
    }
}