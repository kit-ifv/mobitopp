import datastructure.Activity
import domain.data.Employment
import domain.data.Sex
import domain.data.Zone
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.Location
import modeling.discreteChoice.KnownDiscreteChoiceModel
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

import synthesis.activityGeneration.ActitoppGenerator
import synthesis.activityGeneration.GenerateActivitySchedule
import synthesis.activityGeneration.generateActivitiesViaActitopp
import synthesis.carownership.AssignViaRegionType
import synthesis.carownership.CarOwnershipAssignStrategy
import synthesis.discreteChoice.TicketSituation
import synthesis.discreteChoice.TransitPassParameters
import synthesis.discreteChoice.YesTransitPass
import synthesis.discreteChoice.carChoiceModel
import synthesis.discreteChoice.carOwnershipCityParameters
import synthesis.discreteChoice.carOwnershipRuralArea
import synthesis.discreteChoice.carOwnershipSmallCity
import synthesis.discreteChoice.carOwnershipUrbanAreaParameters
import synthesis.discreteChoice.transitPassDiscreteChoiceModel
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson
import synthesis.fixedDestinations.AssignStepBuilder
import synthesis.fixedDestinations.DebugZoneAssigner
import synthesis.fixedDestinations.GREEDY_BY_DISTANCE
import synthesis.fixedDestinations.UseBandwidthLocation
import synthesis.fixedDestinations.UseClosestLocation
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
import units.toCurrency
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
// import usecases.steps.legacyData.defaultZoneCsvParser
import utils.csv.DefaultCsvParser
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

fun String.toBooleanNumeric(): Boolean = when (this) {
    "1" -> true
    "0" -> false
    else -> throw IllegalArgumentException("Invalid binary string for Boolean conversion: $this")
}

fun parseSurvey(file: Path): Sequence<RawSurveyInfo> {
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

    return parser.parse(file.toFile())
}

fun interface AssignTransitCardOwnership<T> {
    fun assignFor(person: SynthesisPerson<out T>): Boolean
}

class AssignByDiscreteChoice(
    val parameters: TransitPassParameters,
    val model: KnownDiscreteChoiceModel<Boolean, TicketSituation, TransitPassParameters> = transitPassDiscreteChoiceModel
) : AssignTransitCardOwnership<SurveyInfo> {
    override fun assignFor(person: SynthesisPerson<out SurveyInfo>): Boolean {
        return model.select({ TicketSituation(it, person.household, person) }, parameters)
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
    val activities = listOf<Activity>() // TODO currently there is no generation of activities.
    var cars = listOf<SynthesisCar>()
    var fixedDestinations: List<FixedDestinationElements> = emptyList()

    fun fixedDestinations(lambda: AssignStepBuilder<T>.() -> Unit) {
        val stepBuilder = AssignStepBuilder<T>(zones, attractivenessModel)
        stepBuilder.apply(lambda)
        //TODO fix the name, fix the name shadowing, maybe find a better solution for assignment
        val fixedDestinationse = stepBuilder.steps.flatMap { it.runOther(people) }
        fixedDestinationse.forEach { it.person.fixedDestinations[it.activityType] = it.location }
        fixedDestinations = fixedDestinationse
    }

    // TODO speaking type parameter names
    fun synthesis(
        randsums: Map<Zone, List<Rule<Any>>>,
        lambda: () -> HouseholdSynthesis<T>
    ) {
        val generator = lambda()
        require(randsums.keys.all { it in zones }) {
            "Zone Ids: ${
                randsums.keys.filter { it !in zones }.map { it.id }
            } requested by the marginal sums are not found" +
                "in the configuration. The program will terminate"
        }
        householdsByZone = generator.synthesize(surveyHouseholds, randsums)
    }

    // TODO refactor, use or discard this method
    fun assignLocationsForAll(lambda: () -> GroupAssignHouseholdLocations<in T>) {
        val strategy = lambda()

        householdsByZone.entries.forEach { (zone, households) ->
            strategy.generateLocations(zone, households).forEach {
                it.first.location = it.second
            }
        }
    }

    fun assignLocations(lambda: () -> AssignHouseholdLocations<in T>) {
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

    fun assignActivities(lambda: () -> GenerateActivitySchedule<in T>) {
        val strategy = lambda()
        people.forEach {
            it.plannedActivities = strategy.generate(it)
        }
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

    fun generateLocations(
        activityType: ActivityType,
        amount: Int = 10,
        generationFunction: (Zone, AttractivenessModel, ActivityType) -> Int = { _, _, _ -> amount }
    ): List<Location> {
        val generatedLocations = zones.generateLocations(attractivenessModel, activityType, generationFunction)
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
                var file = Path("src/test/resources/synthesis/attractivities.csv")
                var activityTypes: Set<ActivityType> = emptySet()
                fun build(): AttractivenessModel {
                    return AttractivenessFromCsv(
                        file = file.toFile(),
                        activityTypes =
                        activityTypes
                    )
                }
            }

            fun attractivenessFromFile(lambda: AttractivenessModelParser.() -> Unit): AttractivenessModel {
                val attractivenessModel = AttractivenessModelParser()
                attractivenessModel.lambda()
                return attractivenessModel.build()
            }
        }

        fun <T : Any> configure(
            surveyPopulation: GenerateArtificialPopulation<T>,
            lambda: SynthesisConfiguration<T>.() -> Unit
        ): PopulationSynthesis<T> {
            val config = SynthesisConfiguration(surveyPopulation).apply(lambda)

            return PopulationSynthesis(
                config.outputDirectory,
                config.zones,
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
        fun fromFile(file: File) = fromFile(file.toPath())
        fun fromFile(fileString: String) = fromFile(Path(fileString))
        fun fromFile(file: Path) = GenerateArtificialPopulation { parseSurvey(file).toList() }
    }
}

fun tryout() {
    val populationSynthesis = PopulationSynthesis.configure(
        surveyPopulation = GenerateArtificialPopulation.fromFile("src/test/resources/synthesis/SurveyPopulation.csv")

    ) {
        outputDirectory = Path("src/test/resources/tempOutput")
        zones = emptyList()
        //        zones = defaultZoneCsvParser(regionTypeCodePlan = Regiostar17).parse("src/test/resources/synthesis/zones.csv")
//            .toList()
//            .map { it.build() }
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
            AssignAroundZoneCentroid(100.0)
        }

        assignEconomicStatus {
            OECDAssigner.fromPath(
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
            AssignByDiscreteChoice(
                parameters = YesTransitPass,
                model = transitPassDiscreteChoiceModel
            )
//            transitPassDiscreteChoiceModel.select( {TicketSituation(it,household, person )}, parameters)
//            choiceModel = transitPassDiscreteChoiceModel
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
        generateCars(strategy = SamplingCarGeneration)
        assignActivities {
            ActitoppGenerator()
        }
        generateActivitiesViaActitopp()
        writeLegacyOutput()
        println("Finished")
    }
}

fun SynthesisSteps<RawSurveyInfo>.writeLegacyOutput() {
    HouseholdOutput.writeCSVToFile(outputDirectory.resolve("household.csv"), households)
    PersonOutput.writeCSVToFile(outputDirectory.resolve("person.csv"), people)
    FixedDestinationOutput.writeCSVToFile(outputDirectory.resolve("fixeddestination.csv"), fixedDestinations)
    ActivityOutput.writeCSVToFile(outputDirectory.resolve("activity.csv"), activities)
    CarOutput.writeCSVToFile(outputDirectory.resolve("car.csv"), cars)
    OpportunitiesOutput.writeCSVToFile(outputDirectory.resolve("opportunities.csv"), opportunities)
}

fun main() {
    tryout()
}

fun Collection<Zone>.generateLocations(
    attractivenessModel: AttractivenessModel,
    activityType: ActivityType,
    generationFunction: (Zone, AttractivenessModel, ActivityType) -> Int = { _, _, _ -> 10 }
): List<Location> {
    return filter { attractivenessModel.attractivenessFor(it.id, activityType) > 0.0 }.flatMap {
        it.generateLocations(generationFunction(it, attractivenessModel, activityType))
    }


}

fun Zone.generateLocations(amount: Int): List<Location> {
    return (0..<amount).map { Location(centroid.coordinate.randomCoordinate(100.0), this, null) }
}
