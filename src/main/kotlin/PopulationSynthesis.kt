import datastructure.Activity
import domain.data.Employment
import domain.data.Sex
import domain.data.Zone
import domain.enums.ActivityType
import domain.enums.areatype.ZoneRegionType
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import modeling.discreteChoice.utility.EnumeratedDiscreteModelBuilder
import modeling.models.FixedChoicesModel
import modeling.models.fixed
import synthesis.ActivityOutput
import synthesis.AssignAroundZoneCentroid
import synthesis.AssignHouseholdLocations
import synthesis.CarOutput
import synthesis.DetermineEconomicStatus
import synthesis.FixedDestinationElements
import synthesis.FixedDestinationOutput
import synthesis.GenerateCars
import synthesis.GroupAssignHouseholdLocations
import synthesis.HouseholdOutput
import synthesis.OECDAssigner
import synthesis.OpportunitiesOutput
import synthesis.OpportunityOutput
import synthesis.PersonOutput
import synthesis.RawSurveyInfo
import synthesis.SamplingCarGeneration
import synthesis.SurveyHousehold
import synthesis.SurveyInfo
import synthesis.SynthesisCar
import synthesis.activityGeneration.ActiToppNGGenerator
import synthesis.activityGeneration.ActitoppGenerator
import synthesis.activityGeneration.GenerateHouseholdActivitySchedule
import synthesis.carownership.CarOwnershipAssignStrategy
import synthesis.carownership.standardAssignmentByRegionSize
import synthesis.discreteChoice.TicketAlternative
import synthesis.discreteChoice.TicketSituation
import synthesis.discreteChoice.TransitPassParameters
import synthesis.discreteChoice.YesTransitPass
import synthesis.discreteChoice.transitPassChoiceModel
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson
import synthesis.fixedDestinations.AssignFixedDestinationBuilder
import synthesis.fixedDestinations.BandwidthLocator
import synthesis.fixedDestinations.UseClosestLocation
import synthesis.fixedDestinations.communityBased.CommunityBasedGroupLocator
import synthesis.fixedDestinations.communityBased.CommuterDemandsMatrix
import synthesis.fixedDestinations.communityBased.CommuterDistance
import synthesis.fixedDestinations.primarySchool
import synthesis.fixedDestinations.secondarySchool
import synthesis.fixedDestinations.work
import synthesis.householdgeneration.HouseholdSynthesis
import synthesis.householdgeneration.IPU
import synthesis.householdgeneration.Rule
import synthesis.randomCoordinate
import synthesis.toSurveyHouseholds
import units.CurrencyUnit
import units.kilometers
import units.meters
import units.toCurrency
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
import usecases.LegacyActivityType
import usecases.legacyChoiceModelPurposes
import utils.csv.DefaultCsvParser
import java.nio.file.Path
import kotlin.io.path.Path

fun String.toBooleanNumeric(): Boolean = when (this) {
    "1" -> true
    "0" -> false
    else -> throw IllegalArgumentException("Invalid binary string for Boolean conversion: $this")
}

fun parseSurvey(path: Path): Sequence<RawSurveyInfo> {
    val parser = DefaultCsvParser { row ->
        RawSurveyInfo(
            householdId = row("ID").toInt(),
            year = row("year").toInt(),
            areaType = row("areatype").toInt(),
            householdSize = row("size").toInt(),
            personNumber = row("personnumber").toInt(),
            sex = row("sex") { Sex.decode(it.toInt()) },
            birthyear = row("birthyear").toInt(),
            employment = row("employmenttype") { Employment.decode(it.toInt()) },
            hasCommuterTicket = row("commuterticket").toBooleanNumeric(),
            householdIncome = row("hhincome") { it.toDouble().toCurrency(CurrencyUnit.EUROS) },
            householdIncomeClass = row("hhincome_class").toInt(),
            type = row("type").toInt(),
            cars = row("cars").toInt(),
            hasBicycle = row("bicycle").toBooleanNumeric(),
            hasLicence = row("licence").toBooleanNumeric(),
            distanceWork = row("distance_work") { it.toDouble().kilometers },
            distanceEducation = row("distance_education") { it.toDouble().kilometers },
        )
    }

    return parser.parse(path)
}

fun interface AssignTransitCardOwnership<T> {
    fun assignFor(person: SynthesisPerson<out T>): Boolean
}

class AssignByDiscreteChoice(
    val model: FixedChoicesModel<TicketAlternative, Boolean> =
        transitPassChoiceModel.build(YesTransitPass).fixed(setOf(true, false))
) : AssignTransitCardOwnership<SurveyInfo> {

    constructor(
        parameters: TransitPassParameters,
        model: EnumeratedDiscreteModelBuilder<Boolean, TicketAlternative, TransitPassParameters> =
            transitPassChoiceModel
    ) : this(model.build(parameters).fixed(setOf(true, false)))

    override fun assignFor(person: SynthesisPerson<out SurveyInfo>): Boolean {
        return model.filterAndSelect(TicketSituation(person.household, person))
    }
}

object AlwaysAssignTransitPass : AssignTransitCardOwnership<Any> {
    override fun assignFor(person: SynthesisPerson<out Any>): Boolean {
        return true
    }
}

class SynthesisSteps<AREA, T : Any>(
    val zones: List<AREA>,
    val surveyHouseholds: Collection<SurveyHousehold<T>>,
    val attractivenessModel: AttractivenessModel,
    val outputDirectory: Path,
    val opportunities: List<OpportunityOutput>
) {

    lateinit var householdsByZone: Map<AREA, List<SynthesisHousehold<out T>>>
    val households get() = householdsByZone.flatMap { it.value }
    val people get() = households.flatMap { it.members }
    var activities: List<Map<SynthesisPerson<*>, Collection<Activity>>> = listOf() // TODO currently there is no generation of activities.
    var cars = listOf<SynthesisCar>()
    var fixedDestinations: List<FixedDestinationElements> = emptyList()

    /**
     * Within the scope of this step, the fixed destinations for the agents are generated. The structure of the assign
     * strategy is created in the [AssignFixedDestinationBuilder] class, which provides some convenience methods for
     * frequently assigned fixed destinations.
     */
    fun assignFixedDestinations(lambda: AssignFixedDestinationBuilder<AREA, T>.() -> Unit) {
        val fixedDestinationBuilder = AssignFixedDestinationBuilder<AREA, T>(attractivenessModel)
        fixedDestinationBuilder.apply(lambda)
        val allFixedDestinations = fixedDestinationBuilder.steps.flatMap { it.generateFixedDestinations(people) }
        allFixedDestinations.forEach { it.person.fixedDestinations[it.activityType] = it.location }
        fixedDestinations = allFixedDestinations
    }

    // TODO speaking type parameter names
    fun synthesis(
        randsums: Map<AREA, List<Rule<Any>>>,
        lambda: () -> HouseholdSynthesis<AREA, T>
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
    fun assignLocationsForAll(lambda: () -> GroupAssignHouseholdLocations<AREA, SynthesisHousehold<out T>>) {
        val strategy = lambda()

        householdsByZone.entries.forEach { (zone, households) ->
            strategy.generateLocations(zone, households).forEach {
                it.first.location = it.second
            }
        }
    }

    fun assignLocations(lambda: () -> AssignHouseholdLocations<in AREA, SynthesisHousehold<out T>>) {
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
        households.forEach { household -> household.amountOfCars = strategy.determineNumberOfCars(household) }
    }

    fun assignTransitCardOwnership(lambda: () -> AssignTransitCardOwnership<in T>) {
        val strategy = lambda()
        households.forEach { hh ->
            hh.members.forEach {
                it.hasTransitPass = strategy.assignFor(it)
            }
        }
    }

    fun generateCars(strategy: GenerateCars<in T>) {
        households.forEach { it.cars += strategy.generate(it) }
        cars = households.flatMap { it.cars }
    }

//    fun assignActivities(lambda: () -> GenerateActivitySchedule<in T>) {
//        val strategy = lambda()
//        people.forEach {
//            it.plannedActivities = strategy.generate(it)
//        }
//    }

    fun assignActivities(lambda: () -> GenerateHouseholdActivitySchedule<in T>) {
        val strategy = lambda()
        households.forEach { h ->
            val output = strategy.generate(h)
            output.entries.forEach {(k, v) ->
                k.plannedActivities = v
            }
        }

        activities = households.map { it.members.associateWith { it.plannedActivities } }

    }
}

class PopulationSynthesis<AREA, T : Any>(
    private val outputDirectory: Path,
    val zones: List<AREA>,
    val surveyHouseholds: Collection<SurveyHousehold<T>>,
    val rules: List<Rule<Any>>,
    val attractivenessModel: AttractivenessModel,
) {
    val opportunities: MutableList<OpportunityOutput> = mutableListOf()
    fun execute(lambda: SynthesisSteps<AREA, T>.() -> Unit) {
        SynthesisSteps(zones, surveyHouseholds, attractivenessModel, outputDirectory, opportunities).apply(lambda)
    }

    @Suppress("UnusedParameter") // TODO reenable the parameter once a fix is found to accept the more generic AREA type
    fun generateLocations(
        activityType: ActivityType,
        amount: Int = 10,
        generationFunction: (AREA, AttractivenessModel, ActivityType) -> List<Location> = { _, _, _ -> listOf(
            LOCATIONUNKNOWN) }
    ): List<Location> {
        // TODO reenable generation and put more thought into how the locations are generated.
        val generatedLocations = zones.flatMap { generationFunction(it, attractivenessModel, activityType) }
        opportunities.addAll(generatedLocations.map { OpportunityOutput(it, attractivenessModel, activityType) })
        return generatedLocations
    }

    companion object {
        class SynthesisConfiguration<AREA, T>(surveyPopulationGenerator: GenerateArtificialPopulation<T>) {
            val surveyPopulation = surveyPopulationGenerator.generateArtificialPopulation()
            lateinit var outputDirectory: Path
            lateinit var zones: List<AREA>
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

        fun <AREA, T : Any> configure(
            surveyPopulation: GenerateArtificialPopulation<T>,
            zones: List<AREA>,
            lambda: SynthesisConfiguration<AREA, T>.() -> Unit
        ): PopulationSynthesis<AREA, T> {
            val config = SynthesisConfiguration<AREA, T>(surveyPopulation).apply(lambda)

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
            parseSurvey(file).toList() }
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

            ActiToppNGGenerator(legacyChoiceModelPurposes){
                ZoneRegionType.DEFAULT
            }
        }
        writeLegacyOutput()
        println("Finished")
    }
}

fun SynthesisSteps<out Any, out SurveyInfo>.writeLegacyOutput() {
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
    generationFunction: (Zone, AttractivenessModel, ActivityType) -> Int = { _, _, _ -> 10 }
): List<Location> {
    return filter { attractivenessModel.attractivenessFor(it.id, activityType) > 0.0 }.flatMap {
        it.generateLocations(generationFunction(it, attractivenessModel, activityType))
    }
}

@Suppress("MagicNumber") // These magic numbers are ok
private fun Zone.generateLocations(amount: Int): List<Location> {
    return (0..<amount).map { Location(centroid.coordinate.randomCoordinate(100.meters, this.random), this, null) }
}
