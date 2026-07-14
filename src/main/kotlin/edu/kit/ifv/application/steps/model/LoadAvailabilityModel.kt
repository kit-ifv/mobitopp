package edu.kit.ifv.application.steps.model

import edu.kit.ifv.application.steps.BaseModesConfig
import edu.kit.ifv.application.steps.DrtModesConfig
import edu.kit.ifv.application.steps.HasImpedance
import edu.kit.ifv.application.steps.HasMutableModeAvailabilityModel
import edu.kit.ifv.application.steps.SharingModesConfig
import edu.kit.ifv.core.modelsteps.steps.modelStep
import edu.kit.ifv.domain.simulation.behavior.availability.defaultAvailabilityModel
import edu.kit.ifv.domain.simulation.behavior.availability.rules.builder.AvailabilityByRuleBuilder

context(config: CFG)
fun <C, CFG> C.loadAvailabilityModel()
where C : HasMutableModeAvailabilityModel,
      C: HasImpedance,
      CFG: BaseModesConfig,
      CFG: SharingModesConfig,
      CFG: DrtModesConfig =
    modelStep("load availability model") {

        modeAvailability = defaultAvailabilityModel(
            pedestrian = config.pedestrianMode,
            bike = config.bikeMode,
            car = config.carMode,
            passenger = config.passengerMode,
            publicTransport = config.publicTransportMode,
            carSharingStation = config.carSharingStationMode,
            carSharingFree = config.carSharingFloatingMode,
            bikeSharingOneWay = config.bikeSharingMode,
            ridePooling = config.ridePoolingMode,
            impedance = impedance
        )
    }

fun <C> C.loadCustomAvailabilityModel(block: AvailabilityByRuleBuilder.() -> Unit)
where C : HasMutableModeAvailabilityModel = modelStep("load custom availability model") {
    modeAvailability = AvailabilityByRuleBuilder().apply { block() }.build()
}
