package usecases.steps

import domain.data.LegacyZone
import domain.data.SharingStation
import domain.data.SharingStationId
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.Mode
import domain.events.CarSelector
import domain.events.ModeScopeDispatcher
import domain.events.PersonBehavior
import domain.events.SharingVehicleSelector
import domain.location.Location
import modeling.models.ChoiceModel
import modeling.models.FixedChoicesModel
import modeling.models.RandomChoiceModel
import modeling.models.addFilter
import modeling.models.fixed
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
import usecases.models.ChoiceModelModes
import usecases.models.DestinationAlternative
import usecases.models.FixedModesFilter
import usecases.models.ModeAvailabilityFilter
import usecases.models.ModeChoiceAlternative
import usecases.models.SharingAvailabilityFilter

fun LoadChoiceModelsContext.loadChoiceModels(
    destinationChoiceModel: ChoiceModel<DestinationAlternative, Location>,
    modeChoiceModel: FixedChoicesModel<ModeChoiceAlternative, Mode>,
    modes: ChoiceModelModes,
) = runStep {
    LoadChoiceModelsStep(this, destinationChoiceModel, modeChoiceModel, modes)
}

interface LoadChoiceModelsContext : Context, SimulationContext {
    val sharingStationsRepository: Repository<SharingStation, SharingStationId>
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone>
    val attractivenessModel: LateInit<AttractivenessModel>
}

class LoadChoiceModelsStep(
    private val context: LoadChoiceModelsContext,
    private val destinationChoiceModel: ChoiceModel<DestinationAlternative, Location>,
    private val modeChoiceModel: FixedChoicesModel<ModeChoiceAlternative, Mode>,
    private val modes: ChoiceModelModes,
) : RepositoryDependentStep {

    override val name: String = "Load transmove legacy mode and destination choice!"
    override val repository: MutableRepository<*, *>? = null
    override val dependentRepositories: Set<Repository<*, *>> = setOf(
        context.zoneRepository,
        context.sharingStationsRepository
    )

    override fun execute() {
        val impedance = context.impedance.value

        val availability =
            SharingAvailabilityFilter( // TODO refactor availability model, as composite of availability rules
                modes,
                context.sharingStationsRepository.elements.toSet(),
                context.sharingStationsRepository.elements.groupBy {
                    it.owner.mode
                }.mapValues {
                    it.value.map { s -> s.owner }.toSet()
                },
                impedance
            )

        val modeChoice = modeChoiceModel.addFilter(
            availability
        ).addFilter(FixedModesFilter)

        val destinationChoice = destinationChoiceModel.fixed(
            context.zoneRepository.elements.map { it.centroid }.toSet()
        )

        val behavior = PersonBehavior(
            destinationChoice,
            modeChoice,
            impedance,
            ModeScopeDispatcher(
                modes.car to CarSelector(modes.car),
                modes.let {
                    it.bikeSharing to SharingVehicleSelector(it.bikeSharing, availability, impedance, it.pedestrian)
                }
            ),
            context.attractivenessModel.value,
            availability,
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
            scopeDispatcher = ModeScopeDispatcher(mapOf()),
            context.attractivenessModel.value,
            DummyAvailability,
        )
    }
}

object DummyAvailability : ModeAvailabilityFilter {
    override fun filter(choices: Set<ModeChoiceAlternative>) = choices
}
