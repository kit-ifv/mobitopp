package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.result.ResultWriter;

public class NoVehicleAssignment implements PublicTransportData {

	@Override
	public ImpedanceIfc impedance(ImpedanceIfc impedance, ZoneRepository zoneRepository) {
		return impedance;
	}

	@Override
	public VehicleBehaviour vehicleBehaviour(ResultWriter results) {
		return VehicleBehaviour.noBehaviour;
	}
}