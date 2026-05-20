package application.steps.parser.csv

import application.steps.ActivityTypesConfig
import application.steps.AttractivenessFileConfig
import application.steps.HasMutableAttractivenessModel
import application.steps.PurposesConfig
import core.modelsteps.Check
import core.modelsteps.steps.modelStep
import core.modelsteps.validation.validateCondition
import core.modelsteps.validation.validateFileReadAccess
import domain.shared.behavior.Attractiveness
import domain.shared.behavior.AttractivenessFromCsv
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.capitalizeWithUnderscores
import domain.shared.enums.ActivityType
import domain.shared.location.ZoneId
import utils.csv.CsvReader
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

/**
 * Loads an attractiveness model from a CSV file.
 *
 * This step reads attractiveness data for zones from a CSV file and initializes an
 * [AttractivenessModel], which is then assigned to the context.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasMutableAttractivenessModel].
 * @param CFG The configuration type. Must implement [ActivityTypesConfig],
 *            [AttractivenessFileConfig], and [PurposesConfig].
 * @param config The configuration. Provided via context.
 * @param path The path to the attractiveness CSV file. Defaults to [config.attractivenessFile].
 * @param work The [ActivityType] representing work. Defaults to [config.work].
 * @param privateVisit The [ActivityType] representing private visits. Defaults to [config.privateVisit].
 * @param activityTypes The set of all activity types to load attractivities for.
 *                      Defaults to [config.activityTypes.values()].
 */
context(config: CFG)
fun <C, CFG> C.loadAttractivenessModelFromCsv(
    path: Path = config.attractivenessFile,
    work: ActivityType = config.work,
    privateVisit: ActivityType = config.privateVisit,
    activityTypes: Set<ActivityType> = config.activityTypes.values(),
) where C: HasMutableAttractivenessModel, CFG: ActivityTypesConfig, CFG: AttractivenessFileConfig, CFG: PurposesConfig =
    modelStep(
        "Load Attractiveness Csv",
        validation = listOf(validateAttractivenessColumns(path, activityTypes, work, privateVisit)),
    ) {
        attractiveness = AttractivenessFromCsv(
            path = path,
            work = work,
            privateVisit = privateVisit,
            activityTypes = activityTypes
        )
    }

private fun <C> validateAttractivenessColumns(
    path: Path,
    activityTypes: Set<ActivityType>,
    work: ActivityType,
    privateVisit : ActivityType,
): Check<C> where C: HasMutableAttractivenessModel = {
    validateFileReadAccess(path, fileDescription = "Csv containing attractiveness data by activity type for zones")

    val columns = CsvReader.of(path).columns

    activityTypes.forEach { activityType ->
        val expectedColumn = "Attractivity:${activityType.description.capitalizeWithUnderscores()}"

        validateCondition(
            message = {
                "Expected attractivities file (${path.name}) to contains column '$expectedColumn'! " +
                        "Found columns: $columns"
            },
            isError = false,
        ) {
            expectedColumn in columns
        }
    }

    // Mock attractiveness model for following validation steps -> the can check attractiveness is initialized
    attractiveness = object : AttractivenessModel {
        override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness = Attractiveness.DEFAULT
        override val work: ActivityType = work
        override val privateVisit: ActivityType = privateVisit
    }


    true
}



//
//
//
//fun LoadAttractivenessDataContext.loadAttractivities(
//    path: Path,
//    purposes: ChoiceModelPurposes,
//) = runStep {
//    LoadAttractivenessStep(this, path, purposes)
//}
//
//interface LoadAttractivenessDataContext : DemandSimContext {
//    val attractivenessModel: LateInit<AttractivenessModel>
//}
//
//class LoadAttractivenessStep(
//    private val context: LoadAttractivenessDataContext,
//    private val path: Path,
//    private val purposes: ChoiceModelPurposes,
//) : ModelStep {
//
//    override val name: String = "Load Attractiveness Csv"
//
//    override fun execute() {
//        context.attractivenessModel.value = AttractivenessFromCsv(
//            path = path,
//            purposes = purposes
//        )
//    }
//
//    override fun verifyInput(): Warning? = validateScope("Validate attractiveness input data: ${path.name}") {
//        validateFileReadAccess(path, fileDescription = "Csv containing attractiveness data by activity type for zones")
//
//        val columns = CsvReader.of(path).columns
//
//        purposes.typesWithAttractivity.forEach { activityType ->
//            val expectedColumn = "Attractivity:${activityType.description.capitalizeWithUnderscores()}"
//
//            validateCondition(
//                message = "Expected attractivities file (${path.name}) to contains column '$expectedColumn'!\n" +
//                    "Found columns: $columns\n" +
//                    "File path: ${path.pathString}",
//                isError = true,
//            ) {
//                expectedColumn in columns
//            }
//        }
//    }
//
//    override fun mockBehavior(): Warning? = validateScope("Mock attractiveness data") {
//        context.attractivenessModel.value = object : AttractivenessModel {
//
//            override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness =
//                Attractiveness.DEFAULT
//            override val purposes: ChoiceModelPurposes = this@LoadAttractivenessStep.purposes
//        }
//    }
//}
