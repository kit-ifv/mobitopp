import datastructure.Activity
import domain.data.Car
import domain.data.Employment
import domain.data.Person
import domain.data.Sex
import domain.data.Zone
import domain.enums.ActivityType
import domain.enums.Bbsr17
import domain.enums.LegacyActivityType
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import modeling.discreteChoice.GlobalRandomizer
import modeling.discreteChoice.carChoiceModel
import synthesis.ActivityOutput
import synthesis.AssignAroundCentroid
import synthesis.AssignEconomicStatus
import synthesis.AssignHouseholdLocations
import synthesis.CarOutput
import synthesis.FixedDestinationElements
import synthesis.FixedDestinationOutput
import synthesis.HouseholdOutput
import synthesis.HouseholdSynthesis
import synthesis.IPU
import synthesis.OECDAssigner
import synthesis.OpportunitiesOutput
import synthesis.OpportunityOutput
import synthesis.PersonInfo
import synthesis.PersonOutput
import synthesis.Rule
import synthesis.SurveyHousehold
import synthesis.SurveyInfo
import synthesis.SurveyPerson
import synthesis.SynthesisHouseholdBuilder
import synthesis.SynthesisPerson
import synthesis.TrivialActivityScheduleGeneration
import synthesis.ZoneTarget
import synthesis.fixedDestinations.CommuterMatrix
import synthesis.fixedDestinations.LocationFinder
import synthesis.fixedDestinations.UseBandwidthLocation
import synthesis.fixedDestinations.UseClosestLocation
import synthesis.generateSchedules
import synthesis.randomCoordinate
import synthesis.select
import synthesis.toSurveyHouseholds
import synthesis.transitPassDiscreteChoiceModel
import units.Coordinate
import units.CurrencyUnit
import units.GPSCoordinate
import units.toCurrency
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
import usecases.steps.legacyData.defaultZoneCsvParser
import usecases.steps.toCSV
import utils.csv.DefaultCsvParser
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

fun String.toBooleanNumeric(): Boolean = when (this) {
    "1" -> true
    "0" -> false
    else -> throw IllegalArgumentException("Invalid binary string for Boolean conversion: $this")
}

fun parseSurvey(file: Path): Sequence<SurveyInfo> {
    val parser = DefaultCsvParser { row ->
        SurveyInfo(
            householdId = row("ID").toInt(),
            householdSize = row("size").toInt(),
            sex = row("sex") { Sex.decode(it.toInt()) },
            age = row("year").toInt() - row("birthyear").toInt(),
            householdIncome = row("hhincome") { it.toDouble().toCurrency(CurrencyUnit.EUROS) },
            hasLicence = row("licence").toBooleanNumeric(),
            employment = row("employmenttype") { Employment.decode(it.toInt()) }
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
        steps.add(AssignStep(element.activityType, SynthesisPerson::isPrimaryStudent, element.assignmentStrategy))
    }

    fun secondarySchool(lambda: FixedIn.() -> Unit) {
        val element = FixedIn()
        element.lambda()
        steps.add(AssignStep(element.activityType, SynthesisPerson::isPrimaryStudent, element.assignmentStrategy))
    }

    fun work(lambda: FixedIn.() -> Unit) {
        val element = FixedIn()
        element.lambda()
        steps.add(AssignStep(element.activityType, SynthesisPerson::isPrimaryStudent, element.assignmentStrategy))
    }
}


class SynthesisSteps(
    val zones: List<Zone>,
    val surveyHouseholds: Collection<SurveyHousehold>,
    val attractivenessModel: AttractivenessModel
) {

    lateinit var householdsByZone: Map<Zone, List<SynthesisHouseholdBuilder>>
    val households get() = householdsByZone.flatMap { it.value }
    val people get() = households.flatMap { it.members }
    val activities = listOf<Activity>() // TODO currently there is no generation of activities.
    val cars = listOf<Car>() // TODO cuirrently there is no generation of cars
    var fixedDestinations: List<FixedDestinationElements> = emptyList()
    fun input(lambda: () -> Unit) {
        //TODO attractiveness Model, Zones, etc.
    }

    fun fixedDestinations(lambda: AssignStepBuilder.() -> Unit) {
        val stepBuilder = AssignStepBuilder(zones)
        stepBuilder.apply(lambda)
        fixedDestinations = stepBuilder.steps.flatMap { it.runOther(people) }
    }

    fun synthesis(randsums: Map<Zone, List<Rule>>, lambda: () -> HouseholdSynthesis) {
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

    var assignHouseholdLocationStrategy: AssignHouseholdLocations = AssignAroundCentroid(100.0)
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
    }

    fun assignAmountOfCarsUsingChoiceModel(lambda: ChoiceModelData.() -> Unit) {
        val model = ChoiceModelData().apply(lambda).choiceModel

        households.forEach { it.amountOfCars = model.select(it.toCarOwnershipParameters()) }
    }

    class TransitCardChoiceModelData {
        var choiceModel = transitPassDiscreteChoiceModel
    }

    fun assignTransitCardOwnership(lambda: TransitCardChoiceModelData.() -> Unit) {
        val model = TransitCardChoiceModelData().apply(lambda).choiceModel

        households.forEach {
            it.members.forEach { person ->
                person.hasTransitPass = model.select(it, person)
            }
        }
    }

    fun generateCars(lambda: () -> Unit) {

    }
}

class OtherHolder(
    private val outputDirectory: Path,
    val zones: List<Zone>,
    val surveyHouseholds: Collection<SurveyHousehold>,
    val randsums: Map<Zone, List<Rule>>,
    val attractivenessModel: AttractivenessModel
) {
    val opportunities : MutableList<OpportunityOutput> = mutableListOf()
    fun build(lambda: SynthesisSteps.() -> Unit) {
        val s = SynthesisSteps(zones, surveyHouseholds, attractivenessModel).apply(lambda)
        HouseholdOutput.writeCSVToFile(outputDirectory.resolve("household.csv"), s.households)
        PersonOutput.writeCSVToFile(outputDirectory.resolve("person.csv"), s.people)
        FixedDestinationOutput.writeCSVToFile(outputDirectory.resolve("fixeddestination.csv"), s.fixedDestinations)
        ActivityOutput.writeCSVToFile(outputDirectory.resolve("activity.csv"), s.activities)
        CarOutput.writeCSVToFile(outputDirectory.resolve("car.csv"), s.cars)
        OpportunitiesOutput.writeCSVToFile(outputDirectory.resolve("opportunities.csv"), opportunities)
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
        class SynthesisConfiguration() {
            lateinit var outputDirectory: Path
            lateinit var zones: List<Zone>
            lateinit var surveyHouseholds: Collection<SurveyHousehold>
            lateinit var randsums: Map<Zone, List<Rule>>
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

        fun configure(lambda: SynthesisConfiguration.() -> Unit): OtherHolder {
            val config = SynthesisConfiguration().apply(lambda)
            val targets = ZoneTarget.fromFile(Path("src/test/resources/synthesis/ZoneTargets.csv")).toList()
            val rules: Map<Zone, List<Rule>> = targets.associate {
                config.zones.first { i -> i.id == it.zoneId } to it.improvedTargets()
            }

            return OtherHolder(
                config.outputDirectory,
                config.zones,
                config.surveyHouseholds,
                rules,
                config.attractivenessModel
            )
        }
    }
}

fun tryout() {

    val o = OtherHolder.configure {
        outputDirectory = Path("src/test/resources/tempOutput")
        zones = defaultZoneCsvParser(areaTypeCodePlan = Bbsr17).parse("src/test/resources/synthesis/zones.csv").toList()
            .map { it.build() }
        surveyHouseholds =
            parseSurvey(Path("src/test/resources/synthesis/SurveyPopulation.csv")).toSurveyHouseholds().values
        attractivenessModel = attractivenessFromFile {
            file = Path("src/test/resources/synthesis/attractivities.csv")
            activityTypes = setOf(LegacyActivityType.EDUCATION_PRIMARY)
        }

    }

    val primarySchools: List<Location> = o.generateLocations(LegacyActivityType.EDUCATION_PRIMARY, amount = 1)
    require(primarySchools.isNotEmpty()) {
        "Somehow no primary schools are generated"
    }
    o.build {
        synthesis(o.randsums) {
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

        assignAmountOfCarsUsingChoiceModel {
            choiceModel = carChoiceModel
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
        generateCars {

        }

    }

}

fun main() {

    tryout()

}

/*
 * Everything below here is development code and should be properly assigned to the corresponding packages.
 */

class AssignStep(
    private val activityType: ActivityType,
    private val filter: (SynthesisPerson) -> Boolean,
    private val assignFunction: LocationFinder
) {
    fun run(target: Collection<SynthesisPerson>): Map<SynthesisPerson, Pair<ActivityType, Location>> {
        return target.filter(filter).associateWith { activityType to assignFunction.find(it, activityType) }
    }

    fun runOther(target: Collection<SynthesisPerson>): List<FixedDestinationElements> {
        return target.filter(filter)
            .map { FixedDestinationElements(it, activityType, assignFunction.find(it, activityType)) }
    }

}

class AllAssignments {
    val steps: MutableList<AssignStep> = mutableListOf()


    fun run(people: Collection<SynthesisPerson>): String {
        return FixedDestinationOutput.generateCSVString(steps.flatMap { it.runOther(people) })
    }

    fun write(
        people: Collection<SynthesisPerson>,
        file: File = File("src/test/resources/tempOutput/fixedDestination.csv")
    ) {
        file.writeText(run(people))
    }

}

private data class SettledPerson(
    val surveyPerson: PersonInfo,
    val location: Location
) : PersonInfo by surveyPerson

data class School(
    val location: Location,
    val students: MutableList<SurveyPerson> = mutableListOf()
)

fun SynthesisPerson.isPrimaryStudent(): Boolean = employment == Employment.STUDENT_PRIMARY
fun SynthesisPerson.isHigherStudent(): Boolean =
    employment == Employment.STUDENT_SECONDARY || employment == Employment.STUDENT_TERTIARY

fun SynthesisPerson.isWorker() = employment == Employment.FULLTIME || employment == Employment.PARTTIME
fun SynthesisPerson.hasEducationActivity() = false // TODO needs schedule information
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

fun extem() {
    val work = LegacyActivityType.WORK
    val attractivenessTypes = setOf(
        work,
        LegacyActivityType.EDUCATION_PRIMARY,
        LegacyActivityType.EDUCATION_SECONDARY,
        LegacyActivityType.EDUCATION_TERTIARY,
    )
    val targets = ZoneTarget.fromFile(Path("src/test/resources/synthesis/ZoneTargets.csv")).toList()

//
//    val visumPath = Path("src/test/resources/rastatt.net")
//    val elements = parse(visumPath)
//    val visumZones = elements.zones.associateBy { it.id }
//
//
//    val visumNetwork = parseNetwork(visumPath) {}
    val result = parseSurvey(Path("src/test/resources/synthesis/SurveyPopulation.csv"))
    val inputZones =
        defaultZoneCsvParser(areaTypeCodePlan = Bbsr17).parse("src/test/resources/synthesis/zones.csv").toList()
            .map { it.build() }

    val attractiveness: AttractivenessModel =
        AttractivenessFromCsv(
            file = Path("src/test/resources/synthesis/attractivities.csv").toFile(), activityTypes =
            attractivenessTypes
        )

    val zones = targets.map { target -> inputZones.first { it.id == target.zoneId } }
    val rules: Map<Zone, List<Rule>> = targets.associate {
        inputZones.first { i -> i.id == it.zoneId } to it.improvedTargets()
    }
    val ipu = IPU { vectors, observers ->
        var counter = 0
        while (observers.maxBy { it.difference }.difference >= 0.01 && counter < 100) {
            observers.forEach { it.optimize() }
            counter++
        }
//        println("Finished after $counter iterations ${observers.joinToString(", ")}")
        vectors
    }
    val households = result.toSurveyHouseholds()
    val syntheticHouseholds: Map<Zone, List<SynthesisHouseholdBuilder>> =
        ipu.synthesize(households.values, zones, rules)// assign location in this step
    AssignAroundCentroid(100.0).assign(syntheticHouseholds)

    val households2 = syntheticHouseholds.flatMap { it.value }
    //TODO Schedule generation
    val schedules = households2.generateSchedules(TrivialActivityScheduleGeneration())
    val economicStatusDesigner = OECDAssigner.fromPath()
    val economics = households2.map { economicStatusDesigner.assign(it) }
    households2.forEach {

        it.amountOfCars = carChoiceModel.select(it.toCarOwnershipParameters())
    }
    households2.forEach {
        it.members.forEach { person ->
            person.hasTransitPass = transitPassDiscreteChoiceModel.select(it, person)
        }
    }
    val people = households2.flatMap { household -> household.members }
    // Generate potential targets for determining fixed destinations, should be exposed so that external code can create the locations
    val primarySchools: List<Location> =
        inputZones.generateLocations(attractiveness, LegacyActivityType.EDUCATION_PRIMARY)
    val higherSchools: List<Location> = inputZones.generateLocations(
        attractiveness,
        listOf(LegacyActivityType.EDUCATION_SECONDARY, LegacyActivityType.EDUCATION_TERTIARY)
    )
    val assignStep = AssignStep(
        LegacyActivityType.EDUCATION_PRIMARY,
        SynthesisPerson::isPrimaryStudent,
        UseClosestLocation(primarySchools)
    )

    val assignStep2 = AssignStep(
        LegacyActivityType.EDUCATION_SECONDARY,
        SynthesisPerson::isHigherStudent,
        UseBandwidthLocation(higherSchools, attractiveness)
    )
    val assignStep3 = AssignStep(
        LegacyActivityType.EDUCATION_TERTIARY,
        SynthesisPerson::hasEducationActivity,
        UseClosestLocation(primarySchools)
    )

    val workAssigner = CommuterMatrix.parse(zoneMapping = inputZones.associateBy { it.id })
    val assignStep4 = AssignStep(
        LegacyActivityType.WORK,
        SynthesisPerson::isWorker,
        workAssigner
    )

    val fixedDestinations = AllAssignments().apply {
        steps += listOf(assignStep, assignStep2, assignStep4)
    }

    fixedDestinations.write(people)
    val primarySchoolAssigner = UseClosestLocation(primarySchools)
    val secondarySchoolAssigner = UseBandwidthLocation(higherSchools, attractiveness)
    val primaries = people.filter { it.employment == Employment.STUDENT_PRIMARY }
    val primaryLocations =
        primaries.associateWith { primarySchoolAssigner.find(it, LegacyActivityType.EDUCATION_PRIMARY) }
    val secondaries = people.filter { it.employment == Employment.STUDENT_SECONDARY }
    val secondaryLocations =
        secondaries.associateWith { secondarySchoolAssigner.find(it, LegacyActivityType.EDUCATION_SECONDARY) }
    val workers = people.filter { it.employment == Employment.FULLTIME || it.employment == Employment.PARTTIME }

    val workLocations = workers.associateWith { workAssigner.find(it, LegacyActivityType.WORK) }
    //TODO generate cars based on some form of generation description.
    households2.forEach { it.amountOfCars }
    people.joinToString {
        toCSV(
            it.id,
            -1,
            -1,
            "householdyear",
            "householdNumber",
            "activitytype",
        )


    }

    val csvString = HouseholdOutput.generateCSVString(households2)
    val output = File("src/test/resources/household.csv")
    output.writeText(csvString)
    val personCSV = PersonOutput.generateCSVString(people)
    val output2 = File("src/test/resources/person.csv")
    output2.writeText(personCSV)
    val destinations = listOf(primaryLocations, secondaryLocations, workLocations)
    val output3 = File("src/test/resources/person.csv")
    output3.writeText(personCSV)
}
/*
 *
 */