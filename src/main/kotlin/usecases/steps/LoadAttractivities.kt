package usecases.steps

import domain.enums.ActivityType
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import modeling.validation.subValidateFileReadAccess
import modeling.validation.validateScope
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
import java.io.File

fun <S> S.loadAttractivities(
    file: File,
    activityTypes: Set<ActivityType>,
) where S : ModelExecution<LegacyContext> {
    addStep(
        LoadAttractivenessStep(context, file, activityTypes)
    )
}

private class LoadAttractivenessStep(
    private val context: LegacyZonesContext,
    private val file: File,
    private val activityTypes: Set<ActivityType>,
) : ModelStep {

    override val name: String = "Load Attractiveness Csv"

    override fun execute() {
        context.attractivenessModel.value = AttractivenessFromCsv(
            file = file,
            activityTypes = activityTypes
        )
    }

    // TODO split into validate and repair function?
    override fun validate() = validateScope(
        "Validate $name produced warnings:"
    ) {
        subValidateFileReadAccess(file)
        context.attractivenessModel.value = AttractivenessModel { _, _ -> 1.0 }
    }
}
