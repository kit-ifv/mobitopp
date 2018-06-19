package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;

public class DepartedVehicles {

	private final Set<ConnectionId> departed;

	public DepartedVehicles() {
		super();
		departed = new HashSet<>();
	}

	public void add(Vehicle vehicle) {
		Optional<ConnectionId> connection = vehicle.nextConnection();

		connection.ifPresent(departed::add);
	}

	public boolean hasDeparted(PublicTransportLeg leg) {
		return departed.contains(leg.firstConnection());
	}

}
