package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;

public interface VehicleFactory {

	Vehicle createFrom(Journey journey);

}