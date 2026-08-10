package edu.kit.ifv.application.steps.parser

import edu.kit.ifv.application.steps.HasAttractivenessModel
import edu.kit.ifv.application.steps.HasParkingPressureModel
import edu.kit.ifv.application.steps.HasZoneRepo
import edu.kit.ifv.application.steps.PurposesConfig
import edu.kit.ifv.core.modelsteps.steps.modelStep
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.behavior.ParkingPressureByAttractiveness
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.shared.location.zone.attributes.HasNumberParkingPlaces
import edu.kit.ifv.domain.shared.location.zone.attributes.HasZoneId

/**
 * Loads and initializes the parking pressure model with an attractiveness model.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasZoneRepo] for [Zone]
 *   - [HasParkingPressureModel]
 *   - [HasAttractivenessModel]
*/
@Suppress("LongParameterList")
context(config: CFG)
fun <C, CFG, Z> C.loadParkingPressureModel(
    work: ActivityType = config.work,
    privateVisit: ActivityType = config.privateVisit,
    zoneProvider: (ZoneId) -> Zone<HasNumberParkingPlaces> = ::getZone,
    attractivenessModel: AttractivenessModel = attractiveness,
)
        where C : HasZoneRepo<*, Z>,
              C : HasAttractivenessModel,
              C : HasParkingPressureModel,
              Z : HasZoneId,
              Z : Zone<HasNumberParkingPlaces>,
              CFG : PurposesConfig =

    modelStep("load parking pressure model") {
        val model = ParkingPressureByAttractiveness(
            work = work,
            privateVisit = privateVisit,
            zoneProvider = zoneProvider,
            attractivenessModel = attractivenessModel,
        )

        this.parkingPressure = model
    }
