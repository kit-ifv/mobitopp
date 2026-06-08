package edu.kit.ifv.integration
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.LegacyActivityType
import edu.kit.ifv.domain.shared.enums.areatype.RegionType
import edu.kit.ifv.domain.shared.enums.areatype.ZoneRegionType
import edu.kit.ifv.domain.shared.enums.legacyChoiceModelPurposes
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.StandardLocationImpl
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.attributes.HasGeometricEmbedding
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.domain.synthesis.AttractivenessModelParser
import edu.kit.ifv.domain.synthesis.GenerateFromFlatInput
import edu.kit.ifv.domain.synthesis.PopulationSynthesis
import edu.kit.ifv.domain.synthesis.SeededProvider
import edu.kit.ifv.domain.synthesis.assignAmountOfCars
import edu.kit.ifv.domain.synthesis.assignEconomicStatus
import edu.kit.ifv.domain.synthesis.assignTransitCardOwnership
import edu.kit.ifv.domain.synthesis.attributes.household.MaximumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MaximumPersonAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.HouseholdFactory
import edu.kit.ifv.domain.synthesis.behavior.activitygeneration.ActiToppNGGenerator
import edu.kit.ifv.domain.synthesis.behavior.cars.amount.standardAssignmentByRegionSize
import edu.kit.ifv.domain.synthesis.behavior.cars.generation.SamplingCarGeneration
import edu.kit.ifv.domain.synthesis.behavior.cars.ownership.UnfilteredSeniority
import edu.kit.ifv.domain.synthesis.behavior.economicstatus.OECDAssigner
import edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.UseClosestLocation
import edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.bandwidth.BandwidthLocator
import edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.communitybased.CommunityBasedGroupLocator
import edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.communitybased.CommuterDemandsMatrix
import edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.communitybased.CommuterDistance
import edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.primarySchool
import edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.secondarySchool
import edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.work
import edu.kit.ifv.domain.synthesis.behavior.householdlocation.AssignAroundPoint
import edu.kit.ifv.domain.synthesis.behavior.transitpass.AssignByDiscreteChoice
import edu.kit.ifv.domain.synthesis.behavior.transitpass.YesTransitPass
import edu.kit.ifv.domain.synthesis.behavior.transitpass.transitPassChoiceModel
import edu.kit.ifv.domain.synthesis.results.OpportunityOutput
import edu.kit.ifv.domain.synthesis.results.legacy.writeLegacyOutput
import edu.kit.ifv.units.meters
import org.locationtech.jts.geom.Geometry
import kotlin.io.path.Path
import kotlin.random.Random

fun <
    AREA,
    S : MinimumHouseholdAttributes,
    T : MinimumPersonAttributes,
    > PopulationSynthesis<AREA, S, T>.generateLocations(
    activityType: ActivityType,
    attractivenessModel: AttractivenessModel,
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
    val attractivenessModel = AttractivenessModelParser.parse(attractivenessModelPath, legacyChoiceModelPurposes)

    val populationSynthesis = PopulationSynthesis.configure(
        surveyPopulation = GenerateFromFlatInput.fromPath("src/test/resources/synthesis/SurveyPopulation.csv"),
        zones = emptyList<Zone<ExampleZoneAttributes>>(),
    ) {
        outputDirectory = Path("src/test/resources/tempOutput")
    }

    val primarySchools: List<StandardLocation> =
        populationSynthesis.generateLocations(LegacyActivityType.EDUCATION_PRIMARY, attractivenessModel) { zone, _, _ ->
            zone.generateLocations(amount = 1)
        }

    val works: List<StandardLocation> =
        populationSynthesis.generateLocations(LegacyActivityType.WORK, attractivenessModel) { zone, _, _ ->
            zone.generateLocations(amount = 1)
        }
    require(primarySchools.isNotEmpty()) {
        "Somehow no primary schools are generated"
    }
    populationSynthesis.execute(
        randomProvider = SeededProvider(42L, personRandomSpawner = { l, pers ->
            Random(l + pers.hashCode())
        }),
    ) {
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
            generationStrategy = SamplingCarGeneration(randomGenerator = { Random(it.hashCode()) }),
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
