
import domain.shared.behavior.AttractivenessFromCsv
import domain.shared.behavior.AttractivenessModel
import domain.shared.datastructure.schedule.Activity
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.areatype.ZoneRegionType
import domain.shared.enums.legacyChoiceModelPurposes
import domain.shared.location.LOCATIONUNKNOWN
import domain.shared.location.Location
import domain.shared.location.Zone
import domain.synthesis.behavior.AssignAroundZoneCentroid
import domain.synthesis.behavior.AssignHouseholdLocations
import domain.synthesis.behavior.DetermineEconomicStatus
import domain.synthesis.behavior.GenerateCars
import domain.synthesis.behavior.GroupAssignHouseholdLocations
import domain.synthesis.behavior.OECDAssigner
import domain.synthesis.behavior.RawSurveyInfo
import domain.synthesis.behavior.SamplingCarGeneration
import domain.synthesis.behavior.SurveyHousehold
import domain.synthesis.behavior.SurveyInfo
import domain.synthesis.behavior.SynthesisCar
import domain.synthesis.behavior.activityGeneration.ActiToppNGGenerator
import domain.synthesis.behavior.activityGeneration.GenerateHouseholdActivitySchedule
import domain.synthesis.behavior.carownership.CarOwnershipAssignStrategy
import domain.synthesis.behavior.carownership.standardAssignmentByRegionSize
import domain.synthesis.behavior.discreteChoice.TicketCharacteristics
import domain.synthesis.behavior.discreteChoice.TransitPassParameters
import domain.synthesis.behavior.discreteChoice.YesTransitPass
import domain.synthesis.behavior.discreteChoice.transitPassChoiceModel
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.fixedDestinations.AssignFixedDestinationBuilder
import domain.synthesis.behavior.fixedDestinations.BandwidthLocator
import domain.synthesis.behavior.fixedDestinations.UseClosestLocation
import domain.synthesis.behavior.fixedDestinations.communityBased.CommunityBasedGroupLocator
import domain.synthesis.behavior.fixedDestinations.communityBased.CommuterDemandsMatrix
import domain.synthesis.behavior.fixedDestinations.communityBased.CommuterDistance
import domain.synthesis.behavior.fixedDestinations.primarySchool
import domain.synthesis.behavior.fixedDestinations.secondarySchool
import domain.synthesis.behavior.fixedDestinations.work
import domain.synthesis.behavior.householdgeneration.HouseholdSynthesis
import domain.synthesis.behavior.householdgeneration.IPU
import domain.synthesis.behavior.householdgeneration.Rule
import domain.synthesis.behavior.randomCoordinate
import domain.synthesis.behavior.toSurveyHouseholds
import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import domain.synthesis.results.ActivityOutput
import domain.synthesis.results.CarOutput
import domain.synthesis.results.FixedDestinationElements
import domain.synthesis.results.FixedDestinationOutput
import domain.synthesis.results.HouseholdOutput
import domain.synthesis.results.OpportunitiesOutput
import domain.synthesis.results.OpportunityOutput
import domain.synthesis.results.PersonOutput
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.EnumeratedDiscreteModelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import units.CurrencyUnit
import units.kilometers
import units.meters
import units.toCurrency
import utils.collections.addProgressBar
import utils.collections.defaultProgressBarBuilder
import utils.collections.stepBy
import utils.csv.DefaultCsvParser
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.Path
import kotlin.random.Random

fun String.toBooleanNumeric(): Boolean = when (this) {
    "1" -> true
    "0" -> false
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

fun parseSurvey(path: Path, surveyColumns: SurveyColumns = SurveyColumns()): Sequence<RawSurveyInfo> {
    val parser = DefaultCsvParser { row ->
        RawSurveyInfo(
            householdId = row(surveyColumns.ID).toInt(),
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
            type = row(surveyColumns.type).toInt(),
            cars = row(surveyColumns.cars).toInt(),
            hasBicycle = row(surveyColumns.bicycle).toBooleanNumeric(),
            hasLicence = row(surveyColumns.licence).toBooleanNumeric(),
            distanceWork = row(surveyColumns.distanceWork) { it.toDouble().kilometers },
            distanceEducation = row(surveyColumns.distanceEducation) { it.toDouble().kilometers },
        )
    }

    return parser.parse(path)
}

fun interface AssignTransitCardOwnership<T> {
    fun assignFor(person: SynthesisPerson<out T>): Boolean
}

class AssignByDiscreteChoice(
    val model: FixedChoiceModel<Boolean, TicketCharacteristics> =
        transitPassChoiceModel.build(YesTransitPass).fixed(setOf(true, false))
) : AssignTransitCardOwnership<SurveyInfo> {

    constructor(
        parameters: TransitPassParameters,
        model: EnumeratedDiscreteModelBuilder<Boolean, TicketCharacteristics, TransitPassParameters> =
            transitPassChoiceModel
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

class SynthesisSteps<T : Any>(
    val zones: List<Zone>,
    val surveyHouseholds: Collection<SurveyHousehold<T>>,
    val attractivenessModel: AttractivenessModel,
    val outputDirectory: Path,
    val opportunities: List<OpportunityOutput>
) {

    lateinit var householdsByZone: Map<Zone, List<SynthesisHousehold<out T>>>
    val households get() = householdsByZone.flatMap { it.value }
    val people get() = households.flatMap { it.members }
    var activities: List<Map<SynthesisPerson<*>, Collection<Activity>>> =
        listOf()
    var cars = listOf<SynthesisCar>()
    var fixedDestinations: List<FixedDestinationElements> = emptyList()

    /**
     * Within the scope of this step, the fixed destinations for the agents are generated. The structure of the assign
     * strategy is created in the [AssignFixedDestinationBuilder] class, which provides some convenience methods for
     * frequently assigned fixed destinations.
     */
    fun assignFixedDestinations(lambda: AssignFixedDestinationBuilder<Zone, T>.() -> Unit) {
        val fixedDestinationBuilder = AssignFixedDestinationBuilder<Zone, T>(attractivenessModel)
        fixedDestinationBuilder.apply(lambda)
        val allFixedDestinations = fixedDestinationBuilder.steps.flatMap { it.generateFixedDestinations(people) }
        allFixedDestinations.addProgressBar("Assign Fixed Destinations").forEach { it.person.fixedDestinations[it.activityType] = it.location }
        fixedDestinations = allFixedDestinations
    }

    // TODO speaking type parameter names
    fun synthesis(
        randsums: Map<Zone, List<Rule<Any>>>,
        lambda: () -> HouseholdSynthesis<Zone, T>
    ) {
        val generator = lambda()
        // TODO reenable
//        require(randsums.keys.all { it in zones }) {
//            "Zone Ids: ${
//                randsums.keys.filter { it !in zones }.map { it.id }
//            } requested by the marginal sums are not found" +
//                "in the configuration. The program will terminate"
//        }
        householdsByZone = generator.synthesize(surveyHouseholds, randsums)
    }

    // TODO refactor, use or discard this method
    fun assignLocationsForAll(lambda: () -> GroupAssignHouseholdLocations<Zone, SynthesisHousehold<out T>>) {
        val strategy = lambda()

        householdsByZone.entries.forEach { (zone, households) ->
            strategy.generateLocations(zone, households).forEach {
                it.first.location = it.second
            }
        }
    }

    fun assignLocations(lambda: () -> AssignHouseholdLocations<in Zone, SynthesisHousehold<out T>>) {
        val strategy = lambda()
        householdsByZone.entries.forEach { (zone, households) ->
            households.forEach {
                it.location = strategy.generateLocation(zone, it)
            }
        }
    }

    fun assignEconomicStatus(lambda: () -> DetermineEconomicStatus<in T>) {
        val strategy = lambda()
        households.forEach { it.economicStatus = strategy.determineStatus(it) }
    }

    fun assignAmountOfCars(lambda: () -> CarOwnershipAssignStrategy<in T>) {
        val strategy = lambda()
        households.addProgressBar("Assign car amount").forEach { household -> household.amountOfCars = strategy.determineNumberOfCars(household) }
    }

    fun assignTransitCardOwnership(lambda: () -> AssignTransitCardOwnership<in T>) {
        val strategy = lambda()
        households.addProgressBar("assign Transit Car").forEach { hh ->
            hh.members.forEach {
                it.hasTransitPass = strategy.assignFor(it)
            }
        }
    }

    fun generateCars(strategy: GenerateCars<in T>) {
        households.addProgressBar("Generate Cars").forEach { it.cars += strategy.generate(it) }
        cars = households.flatMap { it.cars }
    }

    fun assignActivities(lambda: () -> GenerateHouseholdActivitySchedule<in T>) {
        val strategy = lambda()
        households.addProgressBar("Generate Activities").forEach { h ->
            val output = strategy.generate(h)
            output.entries.forEach { (k, v) ->
                k.plannedActivities = v
            }
        }

        activities = households.map { it.members.associateWith { it.plannedActivities } }
    }
}

class PopulationSynthesis<T : Any>(
    private val outputDirectory: Path,
    val zones: List<Zone>,
    val surveyHouseholds: Collection<SurveyHousehold<T>>,
    val rules: List<Rule<Any>>,
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

    companion object {
        class SynthesisConfiguration<T>(surveyPopulationGenerator: GenerateArtificialPopulation<T>) {
            val surveyPopulation = surveyPopulationGenerator.generateArtificialPopulation()
            lateinit var outputDirectory: Path
            lateinit var zones: List<Zone>
            lateinit var rules: List<Rule<Any>>
            lateinit var surveyHouseholds: Collection<SurveyHousehold<T>>
            lateinit var attractivenessModel: AttractivenessModel

            inner class AttractivenessModelParser {

                var path = attractivenessModelPath
                var activityTypes: Set<ActivityType> = emptySet()
                fun build(): AttractivenessModel {
                    return AttractivenessFromCsv(
                        path = path,
                        purposes = legacyChoiceModelPurposes
                    )
                }
            }

            fun attractivenessFromFile(lambda: AttractivenessModelParser.() -> Unit): AttractivenessModel {
                val attractivenessModel = AttractivenessModelParser()
                attractivenessModel.lambda()
                return attractivenessModel.build()
            }
        }

        fun < T : Any> configure(
            surveyPopulation: GenerateArtificialPopulation<T>,
            zones: List<Zone>,
            lambda: SynthesisConfiguration<T>.() -> Unit,
        ): PopulationSynthesis<T> {
            val config = SynthesisConfiguration<T>(surveyPopulation).apply(lambda)

            return PopulationSynthesis(
                config.outputDirectory,
                zones,
                config.surveyHouseholds,
                config.rules,
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
        val rules: Map<Zone, List<Rule<Any>>> = emptyMap()
//            targets.associate {
//            zones.first { i -> i.id == it.zoneId } to it.improvedTargets()
//        }

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
                Path("src/test/resources/synthesis/economical-status-oecd2017.csv")
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
//        generateCars (TrivialCarGeneration::generateCars)
        generateCars(strategy = SamplingCarGeneration)
        assignActivities {
            ActiToppNGGenerator(legacyChoiceModelPurposes) {
                ZoneRegionType.DEFAULT
            }
        }
        writeLegacyOutput()
        println("Finished")
    }
}

fun SynthesisSteps<out RawSurveyInfo>.writeLegacyOutput() {
    HouseholdOutput.writeCSVToFile(outputDirectory.resolve("household.csv"), households)
    PersonOutput.writeCSVToFile(outputDirectory.resolve("person.csv"), people)
    FixedDestinationOutput.writeCSVToFile(outputDirectory.resolve("fixeddestination.csv"), fixedDestinations)
    val flatActivities = activities.flatMap { it.entries.map { it.key to it.value } }
    ActivityOutput.writeCSVToFile(outputDirectory.resolve("activity.csv"), flatActivities)
    CarOutput.writeCSVToFile(outputDirectory.resolve("car.csv"), cars)
    OpportunitiesOutput.writeCSVToFile(outputDirectory.resolve("opportunities.csv"), opportunities)
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
