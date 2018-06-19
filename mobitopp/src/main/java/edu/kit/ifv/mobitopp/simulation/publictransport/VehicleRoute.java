package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class VehicleRoute {

	private final Journey journey;
	private final VehicleLocation location;
	private final VehicleConnections connections;

	public VehicleRoute(Journey journey, VehicleLocation location, VehicleConnections connections) {
		super();
		this.journey = journey;
		this.location = location;
		this.connections = connections;
	}

	public Stop currentStop() {
		return location.current();
	}

	public Optional<ConnectionId> nextConnection() {
		return connections.nextConnection();
	}

	public Optional<Time> nextDeparture() {
		return connections.nextDeparture();
	}

	public Optional<Time> nextArrival() {
		return connections.nextArrival();
	}

	public Time firstDeparture() {
		return connections.firstDeparture();
	}

	public void moveToNextStop() {
		location.move();
		connections.move();
	}

	public int journeyId() {
		return journey.id();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((journey == null) ? 0 : journey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VehicleRoute other = (VehicleRoute) obj;
		if (journey == null) {
			if (other.journey != null)
				return false;
		} else if (!journey.equals(other.journey))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [journey=" + journey + "]";
	}

}
