package application.steps.parser

import application.steps.HasAttractivenessModel
import application.steps.HasParkingPressureModel
import application.steps.HasZoneRepo
import application.steps.PurposesConfig
import core.modelsteps.steps.modelStep
import domain.shared.behavior.ParkingPressureByAttractiveness
import domain.shared.location.MutableZone
import domain.shared.location.Zone

/**
 * Loads and initializes the parking pressure model with an attractiveness model.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasZoneRepo] for [Zone]
 *   - [HasParkingPressureModel]
 *   - [HasAttractivenessModel]
*/
context(config: CFG)
@Suppress("LongParameterList")
fun <C, CFG> C.loadParkingPressureModel()
        where C : HasZoneRepo<MutableZone, Zone>, C : HasAttractivenessModel, C : HasParkingPressureModel, CFG : PurposesConfig
        = modelStep("load parking pressure model") {
    val model = ParkingPressureByAttractiveness(
        work = config.work,
        privateVisit = config.privateVisit,
        zoneProvider = ::getZone,
        attractivenessModel = attractiveness
    )

    this.parkingPressure = model
}