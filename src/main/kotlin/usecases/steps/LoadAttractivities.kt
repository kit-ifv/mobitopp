package usecases.steps

import domain.enums.ActivityType
import modeling.steps.LateInit
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import modeling.validation.Warning
import modeling.validation.validateFileReadAccess
import modeling.validation.validateScope
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
import utils.CodePlan
import java.io.File

fun <S, C> S.loadAttractivities(
    file: File,
) where S : ModelExecution<C>, C : LoadAttractivenessDataContext {
    addStep(
        LoadAttractivenessStep(context, file)
    )
}

interface LoadAttractivenessDataContext {
    val activityTypeCodes: CodePlan<ActivityType>
    val attractivenessModel: LateInit<AttractivenessModel>
}

private class LoadAttractivenessStep(
    private val context: LoadAttractivenessDataContext,
    private val file: File,
) : ModelStep {

    override val name: String = "Load Attractiveness Csv"

    override fun execute() {
        context.attractivenessModel.value = AttractivenessFromCsv(
            file = file,
            activityTypes = context.activityTypeCodes.values()
        )
    }

    override fun verifyInput(): Warning? = validateScope("Validate attractiveness input data: ${file.name}") {
        validateFileReadAccess(file, fileDescription = "Csv containing attractiveness data by activity type for zones")
    }

    override fun mockBehavior(): Warning? = validateScope("Mock attractiveness data") {
        context.attractivenessModel.value = AttractivenessModel { _, _ -> 1.0 }
    }
}
