package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;

public class DepartedVehicles {

	private final Set<Integer> departed;

	public DepartedVehicles() {
		super();
		departed = new HashSet<>();
	}

	public void add(Vehicle vehicle) {
		Optional<Connection> connection = vehicle.nextConnection();

		connection.map(Connection::id).ifPresent(departed::add);
	}

	public boolean hasDeparted(PublicTransportLeg leg) {
		return departed.contains(leg.firstConnection().id());
	}

}
