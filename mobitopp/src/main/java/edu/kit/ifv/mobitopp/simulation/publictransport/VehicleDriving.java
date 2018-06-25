package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

class VehicleDriving {

	private final RelativeTime travelTime;
	private final ConnectionId id;

	public VehicleDriving(ConnectionId id, RelativeTime travelTime) {
		super();
		this.travelTime = travelTime;
		this.id = id;
	}

	public Optional<Time> nextArrival(Optional<Time> nextDeparture) {
		return nextDeparture.map(time -> time.plus(travelTime));
	}

	public ConnectionId nextConnection() {
		return id;
	}

}