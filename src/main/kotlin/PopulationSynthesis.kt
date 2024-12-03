import domain.data.Employment
import domain.data.Sex
import domain.data.Zone
import domain.enums.Bbsr17
import domain.enums.LegacyActivityType
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import modeling.discreteChoice.GlobalRandomizer
import modeling.discreteChoice.carChoiceModel
import synthesis.AssignAroundCentroid
import synthesis.HouseholdOutput
import synthesis.IPU
import synthesis.OECDAssigner
import synthesis.SurveyInfo
import synthesis.SurveyPerson
import synthesis.SynZone
import synthesis.SynthesisHouseholdBuilder
import synthesis.TrivialActivityScheduleGeneration
import synthesis.fixedDestinations.TrivialLocation
import synthesis.ZoneTarget
import synthesis.fixedDestinations.BandwidthParameters
import synthesis.fixedDestinations.CommuterMatrix
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
import kotlin.io.path.Path

fun String.toBooleanNumeric(): Boolean = when (this) {
    "1" -> true
    "0" -> false
    else -> throw IllegalArgumentException("Invalid binary string for Boolean conversion: $this")
}

fun parseSurvey(file: File): Sequence<SurveyInfo> {
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

    return parser.parse(file)
}

fun main() {

    val work = LegacyActivityType.WORK
    val attractivenessTypes = setOf(
        work,
        LegacyActivityType.EDUCATION_PRIMARY,
        LegacyActivityType.EDUCATION_SECONDARY,
        LegacyActivityType.EDUCATION_TERTIARY,
    )
    val targets = ZoneTarget.fromFile(Path("src/test/resources/synthesis/ZoneTargets.csv").toFile()).toList()

    val visumPath = Path("src/test/resources/rastatt.net")
    val elements = parse(visumPath)
    val visumZones = elements.zones.associateBy { it.id }
    val eoe = targets.associateWith {
        val zone = visumZones[domain.ZoneId(it.zoneId.id.toInt())]!!
        SynZone(zone.id.rawValue, zone.coordinate)
    }


    val visumNetwork = parseNetwork(visumPath) {}
    val result = parseSurvey(Path("src/test/resources/synthesis/SurveyPopulation.csv").toFile())

    val attractiveness: AttractivenessModel =
        AttractivenessFromCsv(
            file = Path("src/test/resources/synthesis/attractivities.csv").toFile(), activityTypes =
            attractivenessTypes
        )
    val zones = eoe.values
//    val zones = eoe.entries.map { (k, v) -> SynZone(k.zoneId.id.toInt(), v.coordinate) }
//    val locatableZones = zones.toLocatableZones()
    val zonese = targets.map { it.toSynZone() }
    val rules = targets.associate {
        eoe[it]!! to it.improvedTargets()
    }
    val ipu = IPU { vectors, observers ->
        var counter = 0
        while (observers.maxBy { it.difference }.difference >= 0.01 && counter < 100) {
            observers.forEach { it.optimize() }
            counter++
        }
        println("Finished after $counter iterations ${observers.joinToString(", ")}")
        vectors
    }
    val inputZones = defaultZoneCsvParser(areaTypeCodePlan = Bbsr17).parse("src/test/resources/synthesis/zones.csv").toList().map{it.build()}
    val mapping = zones.associateWith {syn -> inputZones.first {it.id == syn.id} }
    val households = result.toSurveyHouseholds()
    val syntheticHouseholds: Map<Zone, List<SynthesisHouseholdBuilder>> = ipu.synthesize(households.values, zones, rules).map { mapping[it.key]!! to it.value }.toMap()// assign location in this step
    val locatedHouseholds = AssignAroundCentroid(100.0).assign(syntheticHouseholds)

    val schedules = locatedHouseholds.generateSchedules(TrivialActivityScheduleGeneration())
    val economicStatusDesigner = OECDAssigner.fromFile()
    val economics = locatedHouseholds.map { economicStatusDesigner.assign(it) }
    locatedHouseholds.forEach {

        it.amountOfCars = carChoiceModel.select(it.toCarOwnershipParameters())
    }
    locatedHouseholds.forEach {
        it.members.forEach { person ->
            person.hasTransitPass = transitPassDiscreteChoiceModel.select(it, person)
        }
    }
    val people = locatedHouseholds.flatMap {household -> household.members.map{SettledPerson(it, household.location) }}
    val schools: List<Location> = inputZones.filter{attractiveness.attractivenessFor(it.id, LegacyActivityType.EDUCATION_PRIMARY) > 0}.flatMap { it.generateLocations(10) }
    val secondschools: List<Location> = inputZones.filter{attractiveness.attractivenessFor(it.id, LegacyActivityType.EDUCATION_SECONDARY) > 0}.flatMap { it.generateLocations(10) }
    val primarySchoolAssigner = UseClosestLocation(schools)
    val secondarySchoolAssigner = UseBandwidthLocation(secondschools, attractiveness, BandwidthParameters())
    val primaries = people.filter { it.surveyPerson.employment == Employment.STUDENT_PRIMARY }
    val primaryLocations = primaries.map { primarySchoolAssigner.find(it.surveyPerson, it.location, LegacyActivityType.EDUCATION_PRIMARY) }
    val secondaries = people.filter{it.surveyPerson.employment == Employment.STUDENT_SECONDARY}
    val secondaryLocations = secondaries.associateWith{ secondarySchoolAssigner.find(it.surveyPerson, it.location, LegacyActivityType.EDUCATION_SECONDARY)}
    val workAssigner = CommuterMatrix.parse(zoneMapping = inputZones.associateBy { it.id })
    val workers = people.filter{it.surveyPerson.employment == Employment.FULLTIME || it.surveyPerson.employment == Employment.PARTTIME}

    val workLocations = workers.associateWith { workAssigner.find(it.surveyPerson, it.location, LegacyActivityType.WORK) }
    //TODO generate cars based on some form of generation description.
    // TODO assign primary schools by picking the closest one.
    // TODO assign work/education based  on the legacy implementation of mobitopp bands
    locatedHouseholds.forEach { it.amountOfCars }
    people.joinToString{
        toCSV(
            it.surveyPerson.id,
            -1,
            -1,
            "householdyear",
            "householdNumber",
            "activitytype",
        )


    }
    val csvString = HouseholdOutput.toCSV(locatedHouseholds)
    val output = File("src/test/resources/household.csv")
    output.writeText(csvString)
    csvString
    println(csvString)

}

/*
 * Everything below here is development code and should be properly assigned to the corresponding packages.
 */

private data class SettledPerson(
    val surveyPerson: SurveyPerson,
    val location: Location
)
data class School(
    val location: Location,
    val students: MutableList<SurveyPerson> = mutableListOf()
)
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
        return (0..<amount).map{
            val latitude = GlobalRandomizer.nextDouble(latitudeRange.start, latitudeRange.endInclusive)
            val longitude = GlobalRandomizer.nextDouble(longitudeRange.start, longitudeRange.endInclusive)
            Location(GPSCoordinate.decimalDegree(latitude, longitude), zone = null, roadAccess = null)}
    }
}