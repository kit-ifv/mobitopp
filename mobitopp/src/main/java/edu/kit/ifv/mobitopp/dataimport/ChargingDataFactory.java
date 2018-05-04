package edu.kit.ifv.mobitopp.dataimport;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;

public interface ChargingDataFactory {

	ChargingDataForZone create(List<ChargingFacility> any);

}
