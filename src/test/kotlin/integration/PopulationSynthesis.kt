package integration

import GenerateFromFlatInput
import GenerateHouseholds
import domain.shared.behavior.AttractivenessFromCsv
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelPurposes
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
import domain.synthesis.AssignmentStep
import domain.synthesis.HouseholdAssignmentStep
import domain.synthesis.SynthesisSteps
import domain.synthesis.assignAmountOfCars
import domain.synthesis.assignEconomicStatus
import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.HouseholdFactory
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.behavior.activitygeneration.ActiToppNGGenerator
import domain.synthesis.behavior.cars.amount.standardAssignmentByRegionSize
import domain.synthesis.behavior.cars.generation.SamplingCarGeneration
import domain.synthesis.behavior.cars.ownership.UnfilteredSeniority
import domain.synthesis.behavior.discreteChoice.TicketCharacteristics
import domain.synthesis.behavior.discreteChoice.TransitPassParameters
import domain.synthesis.behavior.discreteChoice.YesTransitPass
import domain.synthesis.behavior.discreteChoice.transitPassChoiceModel
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
import domain.synthesis.results.LegacyActivityOutput
import domain.synthesis.results.LegacyCarOutput
import domain.synthesis.results.LegacyFixedDestinationOutput
import domain.synthesis.results.LegacyHouseholdOutput
import domain.synthesis.results.LegacyOpportunitiesOutput
import domain.synthesis.results.LegacyPersonOutput
import domain.synthesis.results.OpportunityOutput
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.EnumeratedDiscreteModelBuilder
import edu.kit.ifv.units.meters
import org.locationtech.jts.geom.Geometry
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.random.Random

@Suppress("SpacingAroundColon") // Seems to be a detekt version thing
class AssignmentStrategy<I, C, O>(val model: FixedChoiceModel<O, C>, private val situation: (I) -> C) :
    AssignmentStep<I, O> {

    context(random: Random)
    override fun assign(input: I): O = context(situation(input)) {
        model.select()
    }

    companion object {
        fun <I, C, O> viaChoiceModel(model: FixedChoiceModel<O, C>, situation: (I) -> C): AssignmentStrategy<I, C, O> =
            AssignmentStrategy(model, situation)

        fun <I, C, O, P> viaChoiceModel(
            modelStructure: EnumeratedDiscreteModelBuilder<O, C, P>,
            parameters: P,
            situation: (I) -> C,
        ) = viaChoiceModel(modelStructure.build(parameters), situation)
    }
}

fun interface AssignTransitCardOwnership<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> :
    HouseholdAssignmentStep<S, T, Boolean>

@Suppress("SpacingAroundColon")
class AssignByDiscreteChoice(
    val model: FixedChoiceModel<Boolean, TicketCharacteristics> =
        transitPassChoiceModel.build(YesTransitPass).fixed(setOf(true, false)),
) : AssignTransitCardOwnership<MaximumHouseholdAttributes, MaximumPersonAttributes> {

    constructor(
        parameters: TransitPassParameters,
        model: EnumeratedDiscreteModelBuilder<Boolean, TicketCharacteristics, TransitPassParameters> =
            transitPassChoiceModel,
    ) : this(model.build(parameters))

    context(household: ISurveyHousehold<MaximumHouseholdAttributes, MaximumPersonAttributes>)
    override fun assignForPerson(person: SurveyPerson<MaximumPersonAttributes>): Boolean =
        context(TicketCharacteristics(household, person), Random(person.personId)) {
            model.select()
        }
}

@Suppress("SpacingAroundColon")
object AlwaysAssignTransitPass : AssignTransitCardOwnership<MinimumHouseholdAttributes, MinimumPersonAttributes> {

    context(household: ISurveyHousehold<MinimumHouseholdAttributes, MinimumPersonAttributes>)
    override fun assignForPerson(person: SurveyPerson<MinimumPersonAttributes>): Boolean = true
}

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

class PopulationSynthesis<AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
    private val outputDirectory: Path,
    val zones: List<AREA>,
    val surveyHouseholds: Collection<ISurveyHousehold<S, T>>,
    val attractivenessModel: AttractivenessModel,
) {

    val opportunities: MutableList<OpportunityOutput> = mutableListOf()
    fun execute(lambda: SynthesisSteps<AREA, S, T>.() -> Unit) {
        SynthesisSteps(
            zones,
            surveyHouseholds,
            attractivenessModel,
            outputDirectory,
            opportunities,
        ).apply(lambda)
    }
// TODO disabled for now because of location rewrite.
//    @Suppress("UnusedParameter") // TODO reenable the parameter once a fix is found to accept the more generic AREA type
//    fun generateLocations(
//        activityType: ActivityType,
//        generationFunction: (AREA, AttractivenessModel, ActivityType) -> List<Location>,
//    ): List<Location> {
//        // TODO reenable generation and put more thought into how the locations are generated.
//        val generatedLocations = zones.flatMap { generationFunction(it, attractivenessModel, activityType) }
//        opportunities.addAll(generatedLocations.map { OpportunityOutput(it, attractivenessModel, activityType) })
//        return generatedLocations
//    }

    companion object {
        class SynthesisConfiguration<AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
            surveyPopulationGenerator: GenerateHouseholds<S, T>,
        ) {
            var surveyPopulation = surveyPopulationGenerator.generateSurveyHouseholds()
            lateinit var outputDirectory: Path
            lateinit var zones: List<AREA>
            lateinit var attractivenessModel: AttractivenessModel

            inner class AttractivenessModelParser {

                var path = attractivenessModelPath

                @Deprecated("This parameter does nothing")
                var activityTypes: Set<ActivityType> = emptySet()
                lateinit var purposes: ChoiceModelPurposes
                fun build(): AttractivenessModel = AttractivenessFromCsv(
                    path = path,
                    work = purposes.work,
                    privateVisit = purposes.privateVisit,
                    activityTypes = purposes.allActivityTypes,
                )
            }

            fun attractivenessFromFile(lambda: AttractivenessModelParser.() -> Unit): AttractivenessModel {
                val attractivenessModel = AttractivenessModelParser()
                attractivenessModel.lambda()
                return attractivenessModel.build()
            }
        }

        fun <AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> configure(
            surveyPopulation: GenerateHouseholds<S, T>,
            zones: List<AREA>,
            lambda: SynthesisConfiguration<AREA, S, T>.() -> Unit,
        ): PopulationSynthesis<AREA, S, T> {
            val config = SynthesisConfiguration<AREA, S, T>(surveyPopulation).apply(lambda)

            return PopulationSynthesis(
                config.outputDirectory,
                zones,
                config.surveyPopulation,
                config.attractivenessModel,
            )
        }
    }
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
            activityTypes = setOf(LegacyActivityType.EDUCATION_PRIMARY)
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
        refactoredPopsyn({ it }) {
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
//            transitPassDiscreteChoiceModel.select( {TicketSituation(it,household, person )}, parameters)
//            choiceModel = transitPassDiscreteChoiceModel
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
                    demands = CommuterDemandsMatrix.parse(
                        Path("src/test/resources/synthesis/zone-to-community.csv"),
                        Path("src/test/resources/synthesis/commuters-rastatt.csv"),
                    ),
                    strategy = CommuterDistance(),
                    potentialLocations = works,
                )
            }
        }

        assignCars(
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

fun <C, T> SynthesisSteps<*, C, T>.writeLegacyOutput()
        where C : MaximumHouseholdAttributes, T : MaximumPersonAttributes {
    LegacyHouseholdOutput<C>().writeCSVToFile(
        outputDirectory.resolve("household.csv"),
        households,
    )
    LegacyPersonOutput<C, T>().writeCSVToFile(outputDirectory.resolve("person.csv"), people)
    LegacyFixedDestinationOutput.writeCSVToFile(
        outputDirectory.resolve("fixeddestination.csv"),
        fixedDestinations,
    )
    LegacyActivityOutput.writeCSVToFile(
        outputDirectory.resolve("activity.csv"),
        activities.map { it.key to it.value },
    )
    LegacyCarOutput.writeCSVToFile(outputDirectory.resolve("car.csv"), cars)
    LegacyOpportunitiesOutput.writeCSVToFile(
        outputDirectory.resolve("opportunities.csv"),
        opportunities,
    )
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
