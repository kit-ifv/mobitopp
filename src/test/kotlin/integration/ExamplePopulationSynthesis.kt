package integration

import domain.shared.behavior.AttractivenessModel
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.areatype.RegionType
import domain.shared.enums.areatype.ZoneRegionType
import domain.shared.enums.legacyChoiceModelPurposes
import domain.shared.location.StandardLocation
import domain.shared.location.StandardLocationImpl
import domain.shared.location.zone.Zone
import domain.shared.location.zone.attributes.HasGeometricEmbedding
import domain.shared.location.zone.attributes.HasRegionType
import domain.synthesis.GenerateFromFlatInput
import domain.synthesis.PopulationSynthesis
import domain.synthesis.assignAmountOfCars
import domain.synthesis.assignEconomicStatus
import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.HouseholdFactory
import domain.synthesis.behavior.activitygeneration.ActiToppNGGenerator
import domain.synthesis.behavior.cars.amount.standardAssignmentByRegionSize
import domain.synthesis.behavior.cars.generation.SamplingCarGeneration
import domain.synthesis.behavior.cars.ownership.UnfilteredSeniority
import domain.synthesis.behavior.economicstatus.OECDAssigner
import domain.synthesis.behavior.fixeddestinations.UseClosestLocation
import domain.synthesis.behavior.fixeddestinations.bandwidth.BandwidthLocator
import domain.synthesis.behavior.fixeddestinations.communitybased.CommunityBasedGroupLocator
import domain.synthesis.behavior.fixeddestinations.communitybased.CommuterDemandsMatrix
import domain.synthesis.behavior.fixeddestinations.communitybased.CommuterDistance
import domain.synthesis.behavior.fixeddestinations.primarySchool
import domain.synthesis.behavior.fixeddestinations.secondarySchool
import domain.synthesis.behavior.fixeddestinations.work
import domain.synthesis.behavior.householdlocation.AssignAroundPoint
import domain.synthesis.behavior.transitpass.AssignByDiscreteChoice
import domain.synthesis.behavior.transitpass.YesTransitPass
import domain.synthesis.behavior.transitpass.transitPassChoiceModel
import domain.synthesis.results.OpportunityOutput
import domain.synthesis.results.legacy.writeLegacyOutput
import edu.kit.ifv.units.meters
import org.locationtech.jts.geom.Geometry
import kotlin.io.path.Path

fun <
    AREA,
    S : MinimumHouseholdAttributes,
    T : MinimumPersonAttributes,
    > PopulationSynthesis<AREA, S, T>.generateLocations(
    activityType: ActivityType,
    generationFunction: (AREA, AttractivenessModel, ActivityType) -> List<StandardLocation>,
): List<StandardLocation> {
    // TODO reenable generation and put more thought into how the locations are generated.
    val generatedLocations = zones.flatMap { generationFunction(it, attractivenessModel, activityType) }
    opportunities.addAll(
        generatedLocations.map {
            OpportunityOutput(
                it,
                attractivenessModel.attractivenessFor(it.attributes.zoneId, activityType),
                activityType,
            )
        },
    )
    return generatedLocations
}

private val attractivenessModelPath = Path("src/test/resources/synthesis/attractivities.csv")

private class ExampleZoneAttributes(override val geometry: Geometry, override val regionType: RegionType) :
    HasGeometricEmbedding,
    HasRegionType

@Suppress(
    "LongMethod",
    "MagicNumber",
) // I agree that the method is long, but right now I don't know how to simplify without breaking the read flow
fun examplePopulationSynthesis() {
    val populationSynthesis = PopulationSynthesis.configure(
        surveyPopulation = GenerateFromFlatInput.fromPath("src/test/resources/synthesis/SurveyPopulation.csv"),
        zones = emptyList<Zone<ExampleZoneAttributes>>(),
    ) {
        outputDirectory = Path("src/test/resources/tempOutput")
        attractivenessModel = attractivenessFromFile {
            path = attractivenessModelPath
        }
    }

    val primarySchools: List<StandardLocation> =
        populationSynthesis.generateLocations(LegacyActivityType.EDUCATION_PRIMARY) { zone, _, _ ->
            zone.generateLocations(amount = 1)
        }

    val works: List<StandardLocation> =
        populationSynthesis.generateLocations(LegacyActivityType.WORK) { zone, _, _ ->
            zone.generateLocations(amount = 1)
        }
    require(primarySchools.isNotEmpty()) {
        "Somehow no primary schools are generated"
    }
    populationSynthesis.execute {
        synthesize {
            TrivialSynthesis(
                surveyHouseholds.map {
                    HouseholdFactory(
                        MaximumHouseholdAttributes::copy,
                        MaximumPersonAttributes::copy,
                    )
                        .createFrom(it)
                },
                zones,

            )
        }

        assignLocations {
            AssignAroundPoint(100.meters)
        }

        assignEconomicStatus {
            OECDAssigner.default()
        }

        assignAmountOfCars {
            standardAssignmentByRegionSize
        }

        assignTransitCardOwnership {
            AssignByDiscreteChoice(
                parameters = YesTransitPass,
                model = transitPassChoiceModel,
            )
        }

        assignFixedDestinations {
            primarySchool {
                activityType = LegacyActivityType.EDUCATION_PRIMARY
                assignmentStrategy = UseClosestLocation(primarySchools)
            }
            secondarySchool {
                activityType = LegacyActivityType.EDUCATION_SECONDARY
                assignmentStrategy =
                    BandwidthLocator(primarySchools, attractivenessModel, LegacyActivityType.EDUCATION_SECONDARY)
            }
            work {
                activityType = LegacyActivityType.WORK
                assignmentStrategy = CommunityBasedGroupLocator(
                    demands = CommuterDemandsMatrix.Companion.parse(
                        Path("src/test/resources/synthesis/zone-to-community.csv"),
                        Path("src/test/resources/synthesis/commuters-rastatt.csv"),
                    ),
                    strategy = CommuterDistance(),
                    potentialLocations = works,
                )
            }
        }

        spawnCars(
            generationStrategy = SamplingCarGeneration(),
            assignStrategy = UnfilteredSeniority(),
        )
        assignActivities {
            ActiToppNGGenerator(legacyChoiceModelPurposes) {
                ZoneRegionType.DEFAULT
            }
        }
        // TODO reenable sharing memberships. MAybe in restatt
//        assignSharingMemberships {
//            provider("Stadtmobil") {
//                AssignmentStrategy.viaChoiceModel(
//                    modelStructure = TODO(),
//                    parameters = TODO()
//                ) {
//                    it.household
//                }
//            }
//        }
        writeLegacyOutput()
        println("Finished")
    }
}

fun main() {
    examplePopulationSynthesis()
}

@Suppress("MagicNumber") // These magic numbers are ok
private fun <Z> Zone<Z>.generateLocations(
    amount: Int,
): List<StandardLocation> where Z : HasGeometricEmbedding, Z : HasRegionType {
    return (0 until amount).map {
        StandardLocationImpl(
            position = attributes.randomPoint(),
            zone = this,
        )
    }
//    return (0..<amount).map { LocationOld(centroid.coordinate.randomCoordinate(100.meters, this.random), this, null) }
}
