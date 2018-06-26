package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.result.ResultWriter;

public interface PublicTransportData {

	PublicTransportData noAssignement = new NoVehicleAssignment();

	VehicleBehaviour vehicleBehaviour(ResultWriter results);

	ImpedanceIfc impedance(ImpedanceIfc impedance, ZoneRepository zoneRepository);

}
