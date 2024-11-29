import domain.data.Employment
import domain.data.Sex
import domain.enums.LegacyActivityType
import modeling.discreteChoice.CarOwnershipParameters
import modeling.discreteChoice.carChoiceModel
import synthesis.AssignAroundCentroid
import synthesis.IPU
import synthesis.OECDAssigner
import synthesis.SurveyInfo
import synthesis.TrivialActivityScheduleGeneration
import synthesis.TrivialLocation
import synthesis.ZoneTarget
import synthesis.generateSchedules
import synthesis.toLocatableZones
import synthesis.toSurveyHouseholds
import units.CurrencyUnit
import units.toCurrency
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
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
            householdIncome = row("hhincome") {it.toDouble().toCurrency(CurrencyUnit.EUROS)},
            hasLicence = row("licence").toBooleanNumeric(),
            employment = row("employmenttype") {Employment.decode(it.toInt())}
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

    val result = parseSurvey(Path("src/test/resources/synthesis/SurveyPopulation.csv").toFile())

    val attractiveness: AttractivenessModel =
        AttractivenessFromCsv(
            file = Path("src/test/resources/synthesis/attractivities.csv").toFile(), activityTypes =
            attractivenessTypes
        )


    val zones = targets.map { it.toSynZone() }
    val rules = targets.associate { it.toSynZone() to it.improvedTargets() }
    val ipu = IPU { vectors, observers ->
        var counter = 0
        while (observers.maxBy { it.difference }.difference >= 0.01 && counter < 100) {
            observers.forEach { it.optimize() }
            counter++
        }
        println("Finished after $counter iterations ${observers.joinToString(", ")}")
        vectors
    }
    val households = result.toSurveyHouseholds()
    val syntheticHouseholds = ipu.synthesize(households.values, zones, rules) // assign location in this step
    val locatedHouseholds = AssignAroundCentroid(100.0).assign(syntheticHouseholds)

    val locatableZones = zones.toLocatableZones()
    val works =
        locatableZones.flatMap { TrivialLocation.generateLocations(it, work, attractiveness) }
    val activityTypeToLoc = attractivenessTypes.associateWith { actType -> locatableZones.flatMap{TrivialLocation.generateLocations(it, actType, attractiveness)} }
    println(locatedHouseholds.sumOf { it.members.size })

    val schedules = locatedHouseholds.generateSchedules(TrivialActivityScheduleGeneration())
    val assigner = OECDAssigner.fromFile()
    val economics = locatedHouseholds.map { assigner.assign(it) }
    locatedHouseholds
    locatedHouseholocatedHouseholds.forEach { carChoiceModel.select(it.toCarOwnershipParameters()) }
}