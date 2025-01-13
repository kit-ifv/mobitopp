import domain.enums.LegacyActivityType
import domain.enums.Regiostar17
import domain.location.Location
import org.junit.jupiter.api.Assertions.*
import synthesis.AssignAroundZoneCentroid
import synthesis.IPU
import synthesis.OECDAssigner
import synthesis.SamplingCarGeneration
import synthesis.activityGeneration.ActitoppGenerator
import synthesis.carownership.AssignViaRegionType
import synthesis.discreteChoice.carChoiceModel
import synthesis.discreteChoice.carOwnershipCityParameters
import synthesis.discreteChoice.carOwnershipRuralArea
import synthesis.discreteChoice.carOwnershipSmallCity
import synthesis.discreteChoice.carOwnershipUrbanAreaParameters
import synthesis.discreteChoice.transitPassDiscreteChoiceModel
import synthesis.fixedDestinations.DebugZoneAssigner
import synthesis.fixedDestinations.GREEDY_BY_DISTANCE
import synthesis.fixedDestinations.UseBandwidthLocation
import synthesis.fixedDestinations.UseClosestLocation
import synthesis.fixedDestinations.primarySchool
import synthesis.fixedDestinations.secondarySchool
import synthesis.fixedDestinations.work
import synthesis.toSurveyHouseholds
import usecases.steps.legacyData.defaultZoneCsvParser
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
            surveyPopulation = TrivialTestGeneration.generateArtificialPopulation()

        ) {

            outputDirectory = Path("src/test/resources/tempOutput")
            zones = listOf(TEST_ZONE)
            surveyHouseholds = surveyPopulation.toSurveyHouseholds()
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
                IPU { vectors, observers ->
                    var counter = 0
                    while (observers.maxBy { it.difference }.difference >= 0.01 && counter < 100) {
                        observers.forEach { it.optimize() }
                        counter++
                    }
                    vectors
                }
            }
            assignLocations {
                AssignAroundZoneCentroid(100.0)
            }


            assignEconomicStatus {
                strategy = OECDAssigner.fromPath(
                    Path("src/test/resources/synthesis/economical-status-oecd2017.csv")
                )
            }

            assignAmountOfCars {
                AssignViaRegionType.create {
                    model = carChoiceModel
                    cityParameters = carOwnershipCityParameters
                    smallTownParameters = carOwnershipSmallCity
                    urbanAreaParameters = carOwnershipUrbanAreaParameters
                    ruralAreaParameters = carOwnershipRuralArea
                }

            }

            assignTransitCardOwnership {
                choiceModel = transitPassDiscreteChoiceModel
            }


            fixedDestinations {
                primarySchool {
                    activityType = LegacyActivityType.EDUCATION_PRIMARY
                    assignmentStrategy = UseClosestLocation(primarySchools)
                }
                secondarySchool {
                    activityType = LegacyActivityType.EDUCATION_SECONDARY
                    assignmentStrategy = UseBandwidthLocation(primarySchools, attractivenessModel)
                }
                work {
                    activityType = LegacyActivityType.WORK
                    assignmentStrategy = distanceBasedCommunity {
                        communityMapping = Path("src/test/resources/synthesis/zone-to-community.csv")
                        commuterFile = Path("src/test/resources/synthesis/commuters-rastatt.csv")
                        strategy = GREEDY_BY_DISTANCE
                        locationInZone = DebugZoneAssigner
                    }
                }
            }
//        generateCars (TrivialCarGeneration::generateCars)
            generateCars (strategy = SamplingCarGeneration)
            assignActivities {
                ActitoppGenerator()
            }
            generateActivitiesViaActitopp()
            writeLegacyOutput()
            println("Finished")

        }
    }
}