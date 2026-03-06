import domain.shared.behavior.AttractivenessFromCsv
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.areatype.ZoneRegionType
import domain.shared.enums.legacyChoiceModelPurposes
import domain.shared.location.Location
import domain.shared.location.Zone
import domain.synthesis.behavior.AssignAroundZoneCentroid
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.OECDAssigner
import domain.synthesis.behavior.RawSurveyInfo
import domain.synthesis.behavior.SamplingCarGeneration
import domain.synthesis.behavior.SurveyInfo
import domain.synthesis.behavior.activityGeneration.ActiToppNGGenerator
import domain.synthesis.behavior.carownership.standardAssignmentByRegionSize
import domain.synthesis.behavior.discreteChoice.TicketCharacteristics
import domain.synthesis.behavior.discreteChoice.TransitPassParameters
import domain.synthesis.behavior.discreteChoice.YesTransitPass
import domain.synthesis.behavior.discreteChoice.transitPassChoiceModel
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.fixedDestinations.BandwidthLocator
import domain.synthesis.behavior.fixedDestinations.UseClosestLocation
import domain.synthesis.behavior.fixedDestinations.communityBased.CommunityBasedGroupLocator
import domain.synthesis.behavior.fixedDestinations.communityBased.CommuterDemandsMatrix
import domain.synthesis.behavior.fixedDestinations.communityBased.CommuterDistance
import domain.synthesis.behavior.fixedDestinations.primarySchool
import domain.synthesis.behavior.fixedDestinations.secondarySchool
import domain.synthesis.behavior.fixedDestinations.work
import domain.synthesis.behavior.householdgeneration.IPU
import domain.synthesis.behavior.householdgeneration.Rule
import domain.synthesis.behavior.randomCoordinate
import domain.synthesis.behavior.toSurveyHouseholds
import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import domain.synthesis.results.LegacyActivityOutput
import domain.synthesis.results.LegacyCarOutput
import domain.synthesis.results.LegacyFixedDestinationOutput
import domain.synthesis.results.LegacyHouseholdOutput
import domain.synthesis.results.LegacyOpportunitiesOutput
import domain.synthesis.results.LegacyPersonOutput
import domain.synthesis.results.OpportunityOutput
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.EnumeratedDiscreteModelBuilder
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.kilometers
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.toCurrency
import utils.csv.DefaultCsvParser
import utils.csv.Row
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.random.Random
import kotlin.text.toDouble
import kotlin.text.toInt

fun String.toBooleanNumeric(): Boolean = when (this) {
    "1" -> true
    "0" -> false
    "-1" -> false // TODO thi
    else -> throw IllegalArgumentException("Invalid binary string for Boolean conversion: $this")
}

data class SurveyColumns(
    var ID: String = "ID",
    var year: String = "year",
    var areatype: String = "areatype",
    var size: String = "size",
    var personnumber: String = "personnumber",
    var sex: String = "sex",
    var birthyear: String = "birthyear",
    var employmenttype: String = "employmenttype",
    var commuterticket: String = "commuterticket",
    var hhincome: String = "hhincome",
    var hhincomeClass: String = "hhincome_class",
    var type: String = "type",
    var cars: String = "cars",
    var bicycle: String = "bicycle",
    var licence: String = "licence",
    var distanceWork: String = "distance_work",
    var distanceEducation: String = "distance_education",
)

fun parseSurvey(path: Path, lambda: SurveyColumns.() -> Unit): List<RawSurveyInfo> {
    val surveyColumns = SurveyColumns()
    surveyColumns.apply(lambda)
    return parseSurvey(path, surveyColumns).toList()
}

fun readRawSurveyInfo(row: Row, surveyColumns: SurveyColumns): RawSurveyInfo {
    return RawSurveyInfo(
        householdId = row(surveyColumns.ID).toLong(),
        year = row(surveyColumns.year).toInt(),
        areaType = row(surveyColumns.areatype).toInt(),
        householdSize = row(surveyColumns.size).toInt(),
        personNumber = row(surveyColumns.personnumber).toInt(),
        sex = row(surveyColumns.sex) { Sex.decode(it.toInt()) },
        birthyear = row(surveyColumns.birthyear).toInt(),
        employment = row(surveyColumns.employmenttype) { Employment.decode(it.toInt()) },
        hasCommuterTicket = row(surveyColumns.commuterticket).toBooleanNumeric(),
        householdIncome = row(surveyColumns.hhincome) { it.toDouble().toCurrency(CurrencyUnit.EUROS) },
        householdIncomeClass = row(surveyColumns.hhincomeClass).toInt(),
        typeCode = row(surveyColumns.type).toInt(),
        cars = row(surveyColumns.cars).toInt(),
        hasBicycle = row(surveyColumns.bicycle).toBooleanNumeric(),
        hasLicence = row(surveyColumns.licence).toBooleanNumeric(),
        distanceWork = row(surveyColumns.distanceWork) { it.toDouble().kilometers },
        distanceEducation = row(surveyColumns.distanceEducation) { it.toDouble().kilometers },
    )
}

fun parseSurvey(path: Path, surveyColumns: SurveyColumns = SurveyColumns()): Sequence<RawSurveyInfo> {
    val parser = DefaultCsvParser { row ->
        readRawSurveyInfo(row, surveyColumns)
    }

    return parser.parse(path)
}

@Suppress("SpacingAroundColon") // Seems to be a detekt version thing
fun interface AssignmentStep<in I, out O> {
    context(random: Random)
    fun assign(input: I): O
}

@Suppress("SpacingAroundColon") // Seems to be a detekt version thing
class AssignmentStrategy<I, C, O>(
    val model: FixedChoiceModel<O, C>,
    private val situation: (I) -> C,
) : AssignmentStep<I, O> {

    context(random: Random)
    override fun assign(input: I): O {
        return context(situation(input)) {
            model.select()
        }
    }

    companion object {
        fun <I, C, O> viaChoiceModel(
            model: FixedChoiceModel<O, C>,
            situation: (I) -> C,
        ): AssignmentStrategy<I, C, O> = AssignmentStrategy(model, situation)

        fun <I, C, O, P> viaChoiceModel(
            modelStructure: EnumeratedDiscreteModelBuilder<O, C, P>,
            parameters: P,
            situation: (I) -> C,
        ) = viaChoiceModel(modelStructure.build(parameters), situation)
    }
}

@Suppress("SpacingAroundColon") // Seems to be a detekt version thing
fun interface AssignTransitCardOwnership<T> : AssignmentStep<SynthesisPerson<out T>, Boolean> {
    fun assignFor(person: SynthesisPerson<out T>): Boolean

    context(random: Random)
    override fun assign(input: SynthesisPerson<out T>): Boolean =
        assignFor(input)
}

class AssignByDiscreteChoice(
    val model: FixedChoiceModel<Boolean, TicketCharacteristics> =
        transitPassChoiceModel.build(YesTransitPass).fixed(setOf(true, false)),
) : AssignTransitCardOwnership<SurveyInfo> {

    constructor(
        parameters: TransitPassParameters,
        model: EnumeratedDiscreteModelBuilder<Boolean, TicketCharacteristics, TransitPassParameters> =
            transitPassChoiceModel,
    ) : this(model.build(parameters))

    override fun assignFor(person: SynthesisPerson<out SurveyInfo>): Boolean {
        return context(TicketCharacteristics(person.household, person), Random(person.personId)) {
            model.select()
        }
    }
}

object AlwaysAssignTransitPass : AssignTransitCardOwnership<Any> {
    override fun assignFor(person: SynthesisPerson<out Any>): Boolean {
        return true
    }
}

class PopulationSynthesis<AREA, T : Any>(
    private val outputDirectory: Path,
    val zones: List<AREA>,
    val surveyHouseholds: Collection<ISurveyHousehold<T>>,
    val attractivenessModel: AttractivenessModel,
) {

    val opportunities: MutableList<OpportunityOutput> = mutableListOf()
    fun execute(lambda: SynthesisSteps<T>.() -> Unit) {
        SynthesisSteps(zones, surveyHouseholds, attractivenessModel, outputDirectory, opportunities).apply(lambda)
    }

    @Suppress("UnusedParameter") // TODO reenable the parameter once a fix is found to accept the more generic AREA type
    fun generateLocations(
        activityType: ActivityType,
        amount: Int = 1,
        generationFunction: (Zone, AttractivenessModel, ActivityType) -> List<Location> = { zone, _, _ ->
            zone.generateLocations(amount)
        },
    ): List<Location> {
        // TODO reenable generation and put more thought into how the locations are generated.
        val generatedLocations = zones.flatMap { generationFunction(it, attractivenessModel, activityType) }
        opportunities.addAll(generatedLocations.map { OpportunityOutput(it, attractivenessModel, activityType) })
        return generatedLocations
    }

    /**
     * Spawn a single location in the zone if the attractiveness is higher than 0.0
     */
    fun generateFilteredLocations(activityType: ActivityType): List<Location> {
        return generateLocations(activityType, 1) { zone, model, act ->
            if (model.attractivenessFor(zone.id, act) > 0.0) zone.generateLocations(1) else emptyList()
        }
    }

    companion object {
        class SynthesisConfiguration<AREA, T>(surveyPopulationGenerator: GenerateArtificialPopulation<T>) {
            val surveyPopulation = surveyPopulationGenerator.generateArtificialPopulation()
            lateinit var outputDirectory: Path
            lateinit var zones: List<AREA>
            lateinit var surveyHouseholds: Collection<ISurveyHousehold<T>>
            lateinit var attractivenessModel: AttractivenessModel

            inner class AttractivenessModelParser {

                var path = attractivenessModelPath

                @Deprecated("This parameter does nothing")
                var activityTypes: Set<ActivityType> = emptySet()
                lateinit var purposes: ChoiceModelPurposes
                fun build(): AttractivenessModel {
                    return AttractivenessFromCsv(
                        path = path,
                        purposes = purposes
                    )
                }
            }

            fun attractivenessFromFile(lambda: AttractivenessModelParser.() -> Unit): AttractivenessModel {
                val attractivenessModel = AttractivenessModelParser()
                attractivenessModel.lambda()
                return attractivenessModel.build()
            }
        }

        fun <AREA, T : Any> configure(
            surveyPopulation: GenerateArtificialPopulation<T>,
            zones: List<AREA>,
            lambda: SynthesisConfiguration<AREA, T>.() -> Unit,
        ): PopulationSynthesis<AREA, T> {
            val config = SynthesisConfiguration<AREA, T>(surveyPopulation).apply(lambda)

            return PopulationSynthesis(
                config.outputDirectory,
                zones,
                config.surveyHouseholds,
                config.attractivenessModel,
            )
        }
    }
}

fun interface GenerateArtificialPopulation<T> {
    fun generateArtificialPopulation(): Collection<T>

    companion object {
        fun fromFile(fileString: String) = fromFile(Path(fileString))
        fun fromFile(file: Path) = GenerateArtificialPopulation {
            parseSurvey(file).toList()
        }
    }
}

private val attractivenessModelPath = Path("src/test/resources/synthesis/attractivities.csv")

@Suppress(
    "LongMethod",
    "MagicNumber"
) // I agree that the method is long, but right now I don't know how to simplify without breaking the read flow
fun examplePopulationSynthesis() {
    val populationSynthesis = PopulationSynthesis.configure(
        surveyPopulation = GenerateArtificialPopulation.fromFile("src/test/resources/synthesis/SurveyPopulation.csv"),
        zones = emptyList<Zone>()
    ) {
        outputDirectory = Path("src/test/resources/tempOutput")

        //        zones = defaultZoneCsvParser(regionTypeCodePlan = Regiostar17).parse("src/test/resources/synthesis/zones.csv")
//            .toList()
//            .map { it.build() }
        surveyHouseholds = surveyPopulation.toSurveyHouseholds()
//            parseSurvey(Path("src/test/resources/synthesis/SurveyPopulation.csv")).toSurveyHouseholds().values
        attractivenessModel = attractivenessFromFile {
            path = attractivenessModelPath
            activityTypes = setOf(LegacyActivityType.EDUCATION_PRIMARY)
        }
    }

    val primarySchools: List<Location> =
        populationSynthesis.generateLocations(LegacyActivityType.EDUCATION_PRIMARY, amount = 1)

    val works: List<Location> =
        populationSynthesis.generateLocations(LegacyActivityType.WORK, amount = 1)
    require(primarySchools.isNotEmpty()) {
        "Somehow no primary schools are generated"
    }
    populationSynthesis.execute {
        // TODO make this a bit more beautiful

//        val targets = ZoneTarget.fromFile(Path("src/test/resources/synthesis/ZoneTargets.csv")).toList()
        val rules: Map<Zone, List<Rule<ISurveyHousehold<out RawSurveyInfo>>>> = emptyMap()

        synthesis(rules) {
            IPU { vectors, observers ->
                var counter = 0
                while (observers.maxBy { it.relativeDifference }.relativeDifference >= 0.01 && counter < 100) {
                    observers.forEach { it.optimize() }
                    counter++
                }
            }
        }

        assignLocations {
            AssignAroundZoneCentroid(100.meters)
        }

        assignEconomicStatus {
            OECDAssigner.fromPath(
                Path("src/main/resources/economical-status-oecd2017.csv")
            )
        }

        assignAmountOfCars {
            standardAssignmentByRegionSize
        }

        assignTransitCardOwnership {
            AssignByDiscreteChoice(
                parameters = YesTransitPass,
                model = transitPassChoiceModel
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
                        Path("src/test/resources/synthesis/commuters-rastatt.csv")
                    ),
                    strategy = CommuterDistance(),
                    potentialLocations = works
                )
            }
        }

        generateCars(strategy = SamplingCarGeneration)
        assignActivities {
            ActiToppNGGenerator(legacyChoiceModelPurposes) {
                ZoneRegionType.DEFAULT
            }
        }
        assignSharingMemberships {
            provider("Stadtmobil") {
                AssignmentStrategy.viaChoiceModel(
                    modelStructure = TODO(),
                    parameters = TODO()
                ) {
                    it.household
                }
            }
        }
        writeLegacyOutput()
        println("Finished")
    }
}

fun SynthesisSteps<out RawSurveyInfo>.writeLegacyOutput() {
    LegacyHouseholdOutput.writeCSVToFile(outputDirectory.resolve("household.csv"), households)
    LegacyPersonOutput.writeCSVToFile(outputDirectory.resolve("person.csv"), people)
    LegacyFixedDestinationOutput.writeCSVToFile(outputDirectory.resolve("fixeddestination.csv"), fixedDestinations)
    val flatActivities = activities.flatMap { it.entries.map { it.key to it.value } }
    LegacyActivityOutput.writeCSVToFile(outputDirectory.resolve("activity.csv"), flatActivities)
    LegacyCarOutput.writeCSVToFile(outputDirectory.resolve("car.csv"), cars)
    LegacyOpportunitiesOutput.writeCSVToFile(outputDirectory.resolve("opportunities.csv"), opportunities)
}

fun main() {
    examplePopulationSynthesis()
}

@Suppress("MagicNumber") // 10 is the number of locations to be generated, no thought is behind that number
private fun Collection<Zone>.generateLocations(
    attractivenessModel: AttractivenessModel,
    activityType: ActivityType,
    generationFunction: (Zone, AttractivenessModel, ActivityType) -> Int = { _, _, _ -> 10 },
): List<Location> {
    return filter { attractivenessModel.attractivenessFor(it.id, activityType) > 0.0 }.flatMap {
        it.generateLocations(generationFunction(it, attractivenessModel, activityType))
    }
}

@Suppress("MagicNumber") // These magic numbers are ok
private fun Zone.generateLocations(amount: Int): List<Location> {
    return (0..<amount).map { Location(centroid.coordinate.randomCoordinate(100.meters, this.random), this, null) }
}
