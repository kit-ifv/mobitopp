package edu.kit.ifv.mobitopp.data.local.serialiser;

import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;

public interface ChargingDataResolver {

	ChargingDataForZone chargingDataFor(int oid);
}
