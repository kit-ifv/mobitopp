package edu.kit.ifv.mobitopp.data;

import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.VehicleBehaviour;

public interface DataRepositoryForSimulation {

	Map<ActivityType, FixedDistributionMatrix> fixedDistributionMatrices();

	PersonLoader personLoader();

	ZoneRepository zoneRepository();

	ImpedanceIfc impedance();

	VehicleBehaviour vehicleBehaviour();

}
