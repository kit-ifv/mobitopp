package edu.kit.ifv.application.scenarios

import edu.kit.ifv.application.steps.HasAttractivenessModel
import edu.kit.ifv.application.steps.HasChoiceModelModes
import edu.kit.ifv.application.steps.HasImpedance
import edu.kit.ifv.application.steps.HasModeChoiceModel
import edu.kit.ifv.application.steps.HasMutableDestinationChoiceModel
import edu.kit.ifv.application.steps.HasMutableModeAvailabilityModel
import edu.kit.ifv.application.steps.HasReplanningStrategy
import edu.kit.ifv.application.steps.HasSpawnDestinationCharacteristics
import edu.kit.ifv.application.steps.HasSpawnModeCharacteristics
import edu.kit.ifv.application.syntheticsim.ControllableImpedance
import edu.kit.ifv.application.syntheticsim.testAttractivenessModel
import edu.kit.ifv.core.modelsteps.ExecutionMode
import edu.kit.ifv.core.modelsteps.initReport
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.behavior.ChoiceModelModes
import edu.kit.ifv.domain.shared.datastructure.schedule.replanning.ReplanningStrategy
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.enums.legacyChoiceModelModes
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.behavior.AvailabilityModelWithSharing
import edu.kit.ifv.domain.simulation.behavior.DestinationChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.events.GenerateDestinationCharacteristics
import edu.kit.ifv.domain.simulation.events.GenerateModeCharacteristics
import edu.kit.ifv.domain.simulation.events.StandardDestinationImplementation
import edu.kit.ifv.domain.simulation.events.StandardModeImplementation
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.utils.report.ReportBuilder

class ScenarioContext(
    override val scenarioName: String,
    override val attractiveness: AttractivenessModel = testAttractivenessModel,
    override var destinationChoiceModel: FixedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
    override val modeChoice: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
    override val choiceModelModes: ChoiceModelModes = legacyChoiceModelModes,
    override var modeAvailability: AvailabilityModelWithSharing,
    override val spawnModeCharacteristics: GenerateModeCharacteristics<ModeChoiceCharacteristics> =
        StandardModeImplementation,
    override val spawnDestinationCharacteristics: GenerateDestinationCharacteristics<DestinationChoiceCharacteristics> =
        StandardDestinationImplementation,
    override val replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT,
) : HasImpedance,
    HasAttractivenessModel,
    HasMutableDestinationChoiceModel,
    HasModeChoiceModel,
    HasChoiceModelModes,
    HasMutableModeAvailabilityModel,
    HasSpawnModeCharacteristics,
    HasSpawnDestinationCharacteristics,
    HasReplanningStrategy {
    override val execMode: ExecutionMode = ExecutionMode()
    override val report: ReportBuilder = initReport()
    override var impedance: Impedance = ControllableImpedance()
    override var currentStep: String = ""
}
