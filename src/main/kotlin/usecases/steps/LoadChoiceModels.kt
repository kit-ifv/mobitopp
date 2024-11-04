package usecases.steps

import domain.enums.ZoneClassification
import domain.events.CarSelector
import domain.events.ModeScopeDispatcher
import domain.events.PersonBehavior
import domain.events.SharingVehicleSelector
import modeling.models.RandomChoiceModel
import modeling.steps.Context
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import modeling.steps.RepositoryState
import modeling.steps.SimulationContext
import modeling.steps.subValidateState
import modeling.validation.subWarning
import modeling.validation.validateScope
import usecases.choicemodels.ChoiceModelModes
import usecases.choicemodels.LegacyModeChoiceModel
import usecases.choicemodels.ModeAvailabilityFilter
import usecases.choicemodels.destinationchoice.ModernizedDestinationChoice
import usecases.choicemodels.destinationchoice.ParameterObject
import usecases.choicemodels.modechoice.ModeParameters
import usecases.choicemodels.modechoice.ModernizedModeUtility
import usecases.models.VehicleTakeAlongModeChoice

fun <S, C> S.loadChoiceModels(
    modes: ChoiceModelModes,
    destinationParameters: ParameterObject = ParameterObject(),
    modeParameters: (ChoiceModelModes) -> ModeParameters = { ModeParameters(it) }
) where
      S : ModelExecution<C>,
      C : Context,
      C : SimulationContext,
      C : LegacyZonesContext,
      C : SharingStationsContext {
    addStep(
        LoadChoiceModelsStep(context, modes, destinationParameters, modeParameters(modes))
    )
}

private class LoadChoiceModelsStep<C>(
    private val context: C,
    private val modes: ChoiceModelModes,
    private val parameters: ParameterObject,
    private val modeParameters: ModeParameters
) : ModelStep where C : SimulationContext, C : LegacyZonesContext, C : SharingStationsContext {

    override val name: String = "Load transmove legacy mode and destination choice!"

    override fun execute() {
        val impedance = context.impedance.value

        val availability = ModeAvailabilityFilter(
            modes,
            context.sharingStationsRepository.elements.toSet(),
            context.sharingStationsRepository.elements.groupBy {
                it.owner.mode
            }.mapValues {
                it.value.map { it.owner }.toSet()
            },
            impedance
        )

        val modeChoice = VehicleTakeAlongModeChoice(

            LegacyModeChoiceModel(
                attractivenessModel = context.attractivenessModel.value,
                modes = modes,
                impedance = context.impedance.value,
                choiceFilter = availability,
                utilitiesGenerator = { a, l, m, h, p -> ModernizedModeUtility(m, a, p) },
                betterParameters = modeParameters
            )
        )

        val behavior = PersonBehavior(
//            destinationChoice = LegacyDestinationChoice(
            destinationChoice = ModernizedDestinationChoice(
                impedance,
                context.attractivenessModel.value,
                umlands = { loc -> loc.requireZone().classification == ZoneClassification.OUTLYING_AREA },
                context.zoneRepository.elements.toSet(),
                modes = modes,
                parameterObject = parameters
            ),

            modeChoice = modeChoice,

            impedance,
            ModeScopeDispatcher(
                modes.car to CarSelector(modes.car),
                modes.let {
                    it.bikeSharing to SharingVehicleSelector(it.bikeSharing, availability, impedance, it.pedestrian)
                }
            )
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

        val impedance = if (context.impedance.isSet) {
            context.impedance.value
        } else {
            dummyImpedance
        }

        context.behavior.value = PersonBehavior(
            destinationChoice = RandomChoiceModel("Dummy destination choice for validation", setOf()),
            modeChoice = RandomChoiceModel("Dummy mode choice for validation", context.modes.values()),
            impedance = impedance,
            scopeDispatcher = ModeScopeDispatcher(mapOf())
        )
    }
}
