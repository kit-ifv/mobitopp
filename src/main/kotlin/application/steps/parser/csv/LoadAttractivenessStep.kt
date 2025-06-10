package application.steps.parser.csv

import core.modelsteps.Context
import core.modelsteps.LateInit
import core.modelsteps.ModelStep
import core.modelsteps.Warning
import core.modelsteps.validateCondition
import core.modelsteps.validateFileReadAccess
import core.modelsteps.validateScope
import domain.shared.behavior.AttractivenessFromCsv
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.capitalizeWithUnderscores
import domain.shared.enums.ActivityType
import domain.simulation.behavior.ChoiceModelPurposes
import domain.synthesis.data.ZoneId
import utils.CodePlan
import utils.csv.CsvReader
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

fun LoadAttractivenessDataContext.loadAttractivities(
    path: Path,
    purposes: ChoiceModelPurposes,
) = runStep {
    LoadAttractivenessStep(this, path, purposes)
}

interface LoadAttractivenessDataContext : Context {
    val activityTypeCodes: CodePlan<ActivityType>
    val attractivenessModel: LateInit<AttractivenessModel>
}

class LoadAttractivenessStep(
    private val context: LoadAttractivenessDataContext,
    private val path: Path,
    private val purposes: ChoiceModelPurposes,
) : ModelStep {

    override val name: String = "Load Attractiveness Csv"

    override fun execute() {
        context.attractivenessModel.value = AttractivenessFromCsv(
            path = path,
            purposes = purposes
        )
    }

    override fun verifyInput(): Warning? = validateScope("Validate attractiveness input data: ${path.name}") {
        validateFileReadAccess(path, fileDescription = "Csv containing attractiveness data by activity type for zones")

        val columns = CsvReader.of(path).columns

        purposes.typesWithAttractivity.forEach { activityType ->
            val expectedColumn = "Attractivity:${activityType.description.capitalizeWithUnderscores()}"

            validateCondition(
                message = "Expected attractivities file (${path.name}) to contains column '$expectedColumn'!\n" +
                    "Found columns: $columns\n" +
                    "File path: ${path.pathString}",
                isError = true,
            ) {
                expectedColumn in columns
            }
        }
    }

    override fun mockBehavior(): Warning? = validateScope("Mock attractiveness data") {
        context.attractivenessModel.value = object : AttractivenessModel {

            override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Double = 1.0
            override val purposes: ChoiceModelPurposes = this@LoadAttractivenessStep.purposes
        }
    }
}
