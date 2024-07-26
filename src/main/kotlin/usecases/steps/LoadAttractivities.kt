package usecases.steps

import domain.enums.ActivityType
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
import utils.files.validateFileReadAccess
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

    override fun validate(): Boolean {
        val valid = validateFileReadAccess(file, messagePrefix = this.name)

        context.attractivenessModel.value = AttractivenessModel { _, _ -> 1.0 }

        return valid
    }
}
