package usecases.steps

import domain.data.ZoneId
import domain.enums.ActivityType
import modeling.steps.Context
import modeling.steps.LateInit
import modeling.steps.ModelStep
import modeling.validation.Warning
import modeling.validation.validateCondition
import modeling.validation.validateFileReadAccess
import modeling.validation.validateScope
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
import usecases.capitalizeWithUnderscores
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import utils.CodePlan
import utils.csv.CsvReader
import java.io.File

fun LoadAttractivenessDataContext.loadAttractivities(
    file: File,
    purposes: ChoiceModelPurposes,
) = runStep {
    LoadAttractivenessStep(this, file, purposes)
}

interface LoadAttractivenessDataContext : Context {
    val activityTypeCodes: CodePlan<ActivityType>
    val attractivenessModel: LateInit<AttractivenessModel>
}

class LoadAttractivenessStep(
    private val context: LoadAttractivenessDataContext,
    private val file: File,
    private val purposes: ChoiceModelPurposes,
) : ModelStep {

    override val name: String = "Load Attractiveness Csv"

    override fun execute() {
        context.attractivenessModel.value = AttractivenessFromCsv(
            file = file,
            purposes = purposes
        )
    }

    override fun verifyInput(): Warning? = validateScope("Validate attractiveness input data: ${file.name}") {
        validateFileReadAccess(file, fileDescription = "Csv containing attractiveness data by activity type for zones")

        val columns = CsvReader.of(file).columns

        purposes.typesWithAttractivity.forEach { activityType ->
            val expectedColumn = "Attractivity:${activityType.description.capitalizeWithUnderscores()}"

            validateCondition(
                message = "Expected attractivities file (${file.name}) to contains column '$expectedColumn'!\n" +
                    "Found columns: $columns\n" +
                    "File path: ${file.path}",
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
