package usecases.steps

import domain.data.LegacyZone
import domain.data.SharingStation
import domain.data.SharingStationId
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ZoneClassification
import domain.events.CarSelector
import domain.events.ModeScopeDispatcher
import domain.events.PersonBehavior
import domain.events.SharingVehicleSelector
import modeling.models.RandomChoiceModel
import modeling.steps.Context
import modeling.steps.LateInit
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.RepositoryDependentStep
import modeling.steps.SimulationContext
import modeling.validation.Warning
import modeling.validation.validateCondition
import modeling.validation.validateScope
import usecases.AttractivenessModel
import usecases.choicemodels.ChoiceModelModes
import usecases.choicemodels.LegacyModeChoiceModel
import usecases.choicemodels.ModeAvailabilityFilter
import usecases.choicemodels.destinationchoice.ModernizedDestinationChoice
import usecases.choicemodels.destinationchoice.ParameterObject
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import usecases.choicemodels.modechoice.ModeParameters
import usecases.choicemodels.modechoice.ModernizedModeUtility
import usecases.models.VehicleTakeAlongModeChoice

fun LoadChoiceModelsContext.loadChoiceModels(
    modes: ChoiceModelModes,
    purposes: ChoiceModelPurposes,
    destinationParameters: ParameterObject = ParameterObject(purposes),
    modeParameters: (ChoiceModelModes) -> ModeParameters = { ModeParameters(it, purposes) }
) = runStep {
    LoadChoiceModelsStep(this, modes, purposes, destinationParameters, modeParameters(modes))
}

interface LoadChoiceModelsContext : Context, SimulationContext {
    val sharingStationsRepository: Repository<SharingStation, SharingStationId>
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone>
    val attractivenessModel: LateInit<AttractivenessModel>
}

class LoadChoiceModelsStep(
    private val context: LoadChoiceModelsContext,
    private val modes: ChoiceModelModes,
    private val purposes: ChoiceModelPurposes,
    private val parameters: ParameterObject,
    private val modeParameters: ModeParameters,
) : RepositoryDependentStep {

    override val name: String = "Load transmove legacy mode and destination choice!"
    override val repository: MutableRepository<*, *>? = null
    override val dependentRepositories: Set<Repository<*, *>> = setOf(
        context.zoneRepository,
        context.sharingStationsRepository
    )

    override fun execute() {
        val impedance = context.impedance.value

        val availability = ModeAvailabilityFilter(
            modes,
            context.sharingStationsRepository.elements.toSet(),
            context.sharingStationsRepository.elements.groupBy {
                it.owner.mode
            }.mapValues {
                it.value.map { s -> s.owner }.toSet()
            },
            impedance
        )

        val modeChoice = VehicleTakeAlongModeChoice(

            LegacyModeChoiceModel(
                attractivenessModel = context.attractivenessModel.value,
                modes = modes,
                impedance = context.impedance.value,
                choiceFilter = availability,
                utilitiesGenerator = { a, _, m, _, p -> ModernizedModeUtility(m, a, p) },
                betterParameters = modeParameters,
                purposes = purposes
            )
        )

        val behavior = PersonBehavior(
            destinationChoice = ModernizedDestinationChoice(
                impedance,
                context.attractivenessModel.value,
                umlands = { loc -> loc.requireZone().classification == ZoneClassification.OUTLYING_AREA },
                context.zoneRepository.elements.toSet(),
                modes = modes,
                parameterObject = parameters,
                purposes = purposes
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

    override fun verifyInput(): Warning? = validateScope("Validate impedance is initialized:") {
        validateCondition("Cannot access impedance, as it has not been loaded yet!") {
            context.impedance.isSet
        }
    }

    override fun mockBehavior(): Warning? = validateScope("Mock choice models for validation:") {
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
