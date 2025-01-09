import datastructure.Activity
import domain.data.Car
import domain.data.Employment
import domain.data.Sex
import domain.data.Zone
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.enums.Regiostar17
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import modeling.discreteChoice.GlobalRandomizer
import synthesis.discreteChoice.carChoiceModel
import synthesis.ActivityOutput
import synthesis.AssignAroundCentroid
import synthesis.AssignEconomicStatus
import synthesis.AssignHouseholdLocations
import synthesis.CarOutput
import synthesis.FixedDestinationElements
import synthesis.FixedDestinationOutput
import synthesis.GenerateCars
import synthesis.HouseholdOutput
import synthesis.HouseholdSynthesis
import synthesis.IPU
import synthesis.OECDAssigner
import synthesis.OpportunitiesOutput
import synthesis.OpportunityOutput
import synthesis.PersonOutput
import synthesis.RawSurveyInfo
import synthesis.Rule
import synthesis.SamplingCarGeneration
import synthesis.SurveyHousehold
import synthesis.SurveyInfo
import synthesis.SynthesisCar
import synthesis.SynthesisHouseholdBuilder
import synthesis.SynthesisPerson
import synthesis.ZoneTarget
import synthesis.carownership.AssignViaRegionType
import synthesis.carownership.CarOwnershipAssignStrategy
import synthesis.discreteChoice.CarOwnershipParameters
import synthesis.discreteChoice.TicketSituation
import synthesis.discreteChoice.YesTransitPass
import synthesis.discreteChoice.carOwnershipCityParameters
import synthesis.discreteChoice.carOwnershipRuralArea
import synthesis.discreteChoice.carOwnershipSmallCity
import synthesis.discreteChoice.carOwnershipUrbanAreaParameters
import synthesis.discreteChoice.transitPassDiscreteChoiceModel
import synthesis.fixedDestinations.CommuterMatrix
import synthesis.fixedDestinations.LocationFinder
import synthesis.fixedDestinations.UseBandwidthLocation
import synthesis.fixedDestinations.UseClosestLocation
import synthesis.generateActivitiesViaActitopp
import synthesis.randomCoordinate

import synthesis.toSurveyHouseholds
import units.Coordinate
import units.CurrencyUnit
import units.GPSCoordinate
import units.kilometers
import units.toCurrency
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
import usecases.steps.legacyData.defaultZoneCsvParser
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
        sex = row("sex"){Sex.decode(it.toInt())},
        birthyear = row("birthyear").toInt(),
        employment = row("employmenttype") {Employment.decode(it.toInt())},
        hasCommuterTicket = row("commuterticket").toBooleanNumeric(),
        householdIncome = row("hhincome"){ it.toDouble().toCurrency(CurrencyUnit.EUROS) },
        householdIncomeClass = row("hhincome_class").toInt(),
        type = row("type").toInt(),
        cars = row("cars").toInt(),
        hasBicycle = row("bicycle").toBooleanNumeric(),
        hasLicence = row("licence").toBooleanNumeric(),
        distanceWork = row("distance_work"){it.toDouble().kilometers},
        distanceEducation = row("distance_education"){it.toDouble().kilometers},
        )
    }

    return parser.parse(file.toFile())
}

class AssignStepBuilder(
    val zones: List<Zone>

) {

    val steps: MutableList<AssignStep> = mutableListOf()

    inner class FixedIn {
        lateinit var activityType: ActivityType
        lateinit var assignmentStrategy: LocationFinder

        inner class StepIN {
            var communityMapping = Path("src/test/resources/synthesis/zone-to-community.csv")
            var commuterFile = Path("src/test/resources/synthesis/commuters-rastatt.csv")
            fun generate(): CommuterMatrix {
                return CommuterMatrix.parse(
                    mappingFile = communityMapping,
                    commuterFile = commuterFile,
                    zoneMapping = zones.associateBy { it.id })
            }
        }

        fun commuterMatrix(lambda: StepIN.() -> Unit): CommuterMatrix {
            return StepIN().apply(lambda).generate()
        }
    }


    fun primarySchool(lambda: FixedIn.() -> Unit) {
        val element = FixedIn()
        element.lambda()
        steps.add(AssignStep(element.activityType, SynthesisPerson<*>::isPrimaryStudent, element.assignmentStrategy))
    }

    fun secondarySchool(lambda: FixedIn.() -> Unit) {
        val element = FixedIn()
        element.lambda()
        steps.add(AssignStep(element.activityType, SynthesisPerson<*>::isPrimaryStudent, element.assignmentStrategy))
    }

    fun work(lambda: FixedIn.() -> Unit) {
        val element = FixedIn()
        element.lambda()
        steps.add(AssignStep(element.activityType, SynthesisPerson<*>::isPrimaryStudent, element.assignmentStrategy))
    }
}


class SynthesisSteps<T: SurveyInfo>(
    val zones: List<Zone>,
    val surveyHouseholds: Collection<SurveyHousehold<T>>,
    val attractivenessModel: AttractivenessModel,
    val outputDirectory: Path,
    val opportunities: List<OpportunityOutput>
) {

    lateinit var householdsByZone: Map<Zone, List<SynthesisHouseholdBuilder<T>>>
    val households get() = householdsByZone.flatMap { it.value }
    val people get() = households.flatMap { it.members }
    val activities = listOf<Activity>() // TODO currently there is no generation of activities.
    var cars = listOf<SynthesisCar>()
    var fixedDestinations: List<FixedDestinationElements> = emptyList()
    fun input(lambda: () -> Unit) {
        //TODO attractiveness Model, Zones, etc.
    }

    fun fixedDestinations(lambda: AssignStepBuilder.() -> Unit) {
        val stepBuilder = AssignStepBuilder(zones)
        stepBuilder.apply(lambda)
        fixedDestinations = stepBuilder.steps.flatMap { it.runOther(people) }
    }

    fun synthesis(randsums: Map<Zone, List<Rule>>, lambda: () -> HouseholdSynthesis<T>) {
        val generator = lambda()
        val synthesisZones = zones.filter { it in randsums.keys }
        require(randsums.keys.all { it in zones }) {
            "Zone Ids: ${
                randsums.keys.filter { it !in zones }.map { it.id }
            } requested by the marginal sums are not found" +
                    "in the configuration. The program will terminate"
        }
        householdsByZone = generator.synthesize(surveyHouseholds, synthesisZones, randsums)
    }
    fun generateSurveyHousholds(lambda: T.() -> Unit) {

    }
    var assignHouseholdLocationStrategy: AssignHouseholdLocations<T> = AssignAroundCentroid(100.0)
    fun assignLocations(lambda: () -> Unit) {
        assignHouseholdLocationStrategy.assign(householdsByZone)
    }

    class Strategy(

    ) {
        lateinit var strategy: AssignEconomicStatus
    }

    fun assignEconomicStatus(lambda: Strategy.() -> Unit) {
        val strat = Strategy().apply(lambda)
        households.forEach { strat.strategy.assign(it) }
    }

    class ChoiceModelData {
        var choiceModel = carChoiceModel
        var parameters = carOwnershipRuralArea
    }

    fun assignAmountOfCars(lambda: ()-> CarOwnershipAssignStrategy) {
//        val input = ChoiceModelData()
//        input.apply(lambda)
//        val model = input.choiceModel
//        val parameters = input.parameters

        val strategy = lambda()
        households.forEach { household -> household.amountOfCars = strategy.assignNumberOfCars(household) }
    }

    class TransitCardChoiceModelData {
        var choiceModel = transitPassDiscreteChoiceModel
        var parameters = YesTransitPass
    }

    fun assignTransitCardOwnership(lambda: TransitCardChoiceModelData.() -> Unit) {
        val input = TransitCardChoiceModelData()
        input.apply(lambda)
        val model = input.choiceModel
        val parameters = input.parameters


        households.forEach {household ->
            household.members.forEach { person ->
                person.hasTransitPass = model.select( {TicketSituation(it,household, person )}, parameters)
            }
        }
    }

    fun generateCars(strategy: GenerateCars<T>) {

        cars = households.flatMap { strategy.generate(it) }

    }

}

class PopulationSynthesis<T: SurveyInfo>(
    private val outputDirectory: Path,
    val zones: List<Zone>,
    val surveyHouseholds: Collection<SurveyHousehold<T>>,
    val randsums: Map<Zone, List<Rule>>,
    val attractivenessModel: AttractivenessModel,
    val surveyData: Collection<T>
) {
    val opportunities : MutableList<OpportunityOutput> = mutableListOf()
    fun execute(lambda: SynthesisSteps<T>.() -> Unit) {
        val s = SynthesisSteps<T>(zones, surveyHouseholds, attractivenessModel, outputDirectory, opportunities).apply(lambda)

    }

    fun generateLocations(
        activityType: ActivityType,
        amount: Int = 10,
        generationFunction: (Zone, AttractivenessModel, ActivityType) -> Int = { _, _, _ -> amount }
    ): List<Location> {
        val generatedLocations =  zones.generateLocations(attractivenessModel, activityType, generationFunction)
        opportunities.addAll(generatedLocations.map { OpportunityOutput(it, attractivenessModel, activityType) })
        return generatedLocations
    }

    companion object {
        class SynthesisConfiguration<T: SurveyInfo>(surveyPopulationGenerator: GenerateArtificialPopulation<T>) {
            val surveyPopulation = surveyPopulationGenerator.generateArtificialPopulation()
            lateinit var outputDirectory: Path
            lateinit var zones: List<Zone>

            lateinit var surveyHouseholds: Collection<SurveyHousehold<T>>
            lateinit var attractivenessModel: AttractivenessModel

            inner class AttractivenessModelParser {
                var file = Path("src/test/resources/synthesis/attractivities.csv")
                var activityTypes: Set<ActivityType> = emptySet()
                fun build(): AttractivenessModel {
                    return AttractivenessFromCsv(
                        file = file.toFile(), activityTypes =
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

        fun <T: SurveyInfo> configure(surveyPopulation: GenerateArtificialPopulation<T>, lambda: SynthesisConfiguration<T>.() -> Unit): PopulationSynthesis<T> {
            val config = SynthesisConfiguration(surveyPopulation).apply(lambda)
            val targets = ZoneTarget.fromFile(Path("src/test/resources/synthesis/ZoneTargets.csv")).toList()
            val rules: Map<Zone, List<Rule>> = targets.associate {
                config.zones.first { i -> i.id == it.zoneId } to it.improvedTargets()
            }

            return PopulationSynthesis(
                config.outputDirectory,
                config.zones,
                config.surveyHouseholds,
                rules,
                config.attractivenessModel,
                config.surveyPopulation
            )
        }
    }
}

fun interface GenerateArtificialPopulation<T: SurveyInfo> {
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
        zones = defaultZoneCsvParser(regionTypeCodePlan = Regiostar17).parse("src/test/resources/synthesis/zones.csv").toList()
            .map { it.build() }
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
            assignHouseholdLocationStrategy = AssignAroundCentroid(100.0)
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
                assignmentStrategy = commuterMatrix {
                    communityMapping = Path("src/test/resources/synthesis/zone-to-community.csv")
                    commuterFile = Path("src/test/resources/synthesis/commuters-rastatt.csv")
                }
            }
        }
//        generateCars (TrivialCarGeneration::generateCars)
        generateCars (strategy = SamplingCarGeneration)

        //generateActivitiesViaActitopp()
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

/*
 * Everything below here is development code and should be properly assigned to the corresponding packages.
 */

class AssignStep(
    private val activityType: ActivityType,
    private val filter: (SynthesisPerson<*>) -> Boolean,
    private val assignFunction: LocationFinder
) {
    fun run(target: Collection<SynthesisPerson<*>>): Map<SynthesisPerson<*>, Pair<ActivityType, Location>> {
        return target.filter(filter).associateWith { activityType to assignFunction.find(it, activityType) }
    }

    fun runOther(target: Collection<SynthesisPerson<*>>): List<FixedDestinationElements> {
        return target.filter(filter)
            .map { FixedDestinationElements(it, activityType, assignFunction.find(it, activityType)) }
    }

}




fun SynthesisPerson<*>.isPrimaryStudent(): Boolean = employment == Employment.STUDENT_PRIMARY
fun SynthesisPerson<*>.isHigherStudent(): Boolean =
    employment == Employment.STUDENT_SECONDARY || employment == Employment.STUDENT_TERTIARY

fun SynthesisPerson<*>.isWorker() = employment == Employment.FULLTIME || employment == Employment.PARTTIME
fun SynthesisPerson<*>.hasEducationActivity() = false // TODO needs schedule information
fun Collection<Zone>.generateLocations(
    attractivenessModel: AttractivenessModel,
    activityType: ActivityType,
    generationFunction: (Zone, AttractivenessModel, ActivityType) -> Int = { _, _, _ -> 10 }
): List<Location> {
    return filter { attractivenessModel.attractivenessFor(it.id, activityType) > 0.0 }.flatMap {
        it.generateLocations(
            generationFunction(it, attractivenessModel, activityType)
        )
    }
}

fun Collection<Zone>.generateLocations(
    attractivenessModel: AttractivenessModel,
    activityTypes: Collection<ActivityType>,
    generationFunction: (Zone, AttractivenessModel, ActivityType) -> Int = { _, _, _ -> 10 }
): List<Location> {
    return filter { zone -> activityTypes.any { attractivenessModel.attractivenessFor(zone.id, it) > 0.0 } }.flatMap {
        it.generateLocations(
            // TODO not entirely accurate to only use the first activity type for generating locations with shared activity type.
            generationFunction(it, attractivenessModel, activityTypes.first())
        )
    }
}

fun Zone.generateLocations(amount: Int): List<Location> {
    return (0..<amount).map { Location(centroid.coordinate.randomCoordinate(100.0), this, null) }
}

fun debugGetSchools(): List<Location> = listOf(LOCATIONUNKNOWN)


val RastattBounds = Rectangle()

data class Rectangle(
    val minCoord: Coordinate = GPSCoordinate.decimalDegree(48.4280418587, 8.0077508),
    val maxCoord: Coordinate = GPSCoordinate.decimalDegree(49.2402568, 9.1873355),
) {

    val latitudeRange = minCoord.latitudeDegrees..maxCoord.latitudeDegrees
    val longitudeRange = minCoord.longitudeDegrees..maxCoord.longitudeDegrees
    fun generateLocations(amount: Int): List<Location> {
        return (0..<amount).map {
            val latitude = GlobalRandomizer.nextDouble(latitudeRange.start, latitudeRange.endInclusive)
            val longitude = GlobalRandomizer.nextDouble(longitudeRange.start, longitudeRange.endInclusive)
            Location(GPSCoordinate.decimalDegree(latitude, longitude), zone = null, roadAccess = null)
        }
    }
}
