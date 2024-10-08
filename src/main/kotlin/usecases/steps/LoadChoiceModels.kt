package usecases.steps

import domain.enums.ZoneClassification
import domain.events.PersonBehavior
import domain.location.ZoneLocation
import modeling.models.RandomChoiceModel
import modeling.steps.Context
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import modeling.steps.RepositoryState
import modeling.steps.SimulationContext
import modeling.steps.subValidateState
import modeling.validation.subWarning
import modeling.validation.validateScope
import usecases.choicemodels.LegacyDestinationChoice
import usecases.choicemodels.LegacyModeChoiceModel
import usecases.models.VehicleTakeAlongModeChoice

fun <S, C> S.loadChoiceModels()
    where S : ModelExecution<C>, C : Context, C : SimulationContext, C : LegacyZonesContext {
    addStep(
        LoadChoiceModelsStep(context)
    )
}

private class LoadChoiceModelsStep<C>(
    private val context: C,
) : ModelStep where C : SimulationContext, C : LegacyZonesContext {

    override val name: String = "Load transmove legacy mode and destination choice!"

    override fun execute() {
        val impedance = context.impedance.value

        val behavior = PersonBehavior(
            destinationChoice = LegacyDestinationChoice(
                impedance,
                context.attractivenessModel.value,
                umlands = { loc -> (loc as ZoneLocation).zone.classification == ZoneClassification.OUTLYING_AREA },
                context.zoneRepository.elements.toSet(),
                modes = context.modes,
            ),

            modeChoice = VehicleTakeAlongModeChoice(
                LegacyModeChoiceModel(
                    attractivenessModel = context.attractivenessModel.value,
                    modes = context.modes,
                    impedance = context.impedance.value,
                )
            ),

            context.impedance.value
        )

        context.behavior.value = behavior
    }

    override fun validate() = validateScope(
        "Validate $name produced warnings:"
    ) {
        subValidateState(context.zoneRepository, RepositoryState.FINISHED, this@LoadChoiceModelsStep)

        subWarning {
            require(context.impedance.isSet) {
                "Cannot access impedance, as it has not been loaded yet!"
            }
        }

        val impedance = if (context.impedance.isSet) { context.impedance.value } else { dummyImpedance }

        context.behavior.value = PersonBehavior(
            destinationChoice = RandomChoiceModel("Dummy destination choice for validation", setOf()),
            modeChoice = RandomChoiceModel("Dummy mode choice for validation", context.modes.values()),
            impedance = impedance,
        )
    }
}
