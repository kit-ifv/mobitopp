package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Collection;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;

public interface VehicleTimesConverter {

	VehicleTimes convert(Collection<Connection> connections);

}