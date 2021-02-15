package edu.kit.ifv.mobitopp.simulation.person;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VehicleLeg implements PublicTransportLeg {

	private final Stop start;
	private final Stop end;
	private final Journey journey;
	private final Time departure;
	private final Time arrival;
	private final List<Connection> connections;

	public VehicleLeg(Stop start, Stop end, Journey journey, Time departure, Time arrival, List<Connection> connections) {
		super();
		this.start = start;
		this.end = end;
		this.journey = journey;
		this.departure = departure;
		this.arrival = arrival;
		verify(connections);
		this.connections = connections;
	}

	private void verify(List<Connection> connections) {
		if (connections.isEmpty()) {
			throw warn(new IllegalArgumentException("Leg must contain connections, but does not. Leg: " + this), log);
		}
	}

	@Override
	public int journeyId() {
		return journey.id();
	}

	@Override
	public Stop start() {
		return start;
	}

	@Override
	public Stop end() {
		return end;
	}

	@Override
	public Journey journey() {
		return journey;
	}

	@Override
	public Time arrival() {
		return arrival;
	}

	@Override
	public Time departure() {
		return departure;
	}

	@Override
	public List<Connection> connections() {
		return connections;
	}

	@Override
	public ConnectionId firstConnection() {
		return connections.iterator().next().id();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrival == null) ? 0 : arrival.hashCode());
		result = prime * result + ((departure == null) ? 0 : departure.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((journey == null) ? 0 : journey.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VehicleLeg other = (VehicleLeg) obj;
		if (arrival == null) {
			if (other.arrival != null) {
				return false;
			}
		} else if (!arrival.equals(other.arrival)) {
			return false;
		}
		if (departure == null) {
			if (other.departure != null) {
				return false;
			}
		} else if (!departure.equals(other.departure)) {
			return false;
		}
		if (end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!end.equals(other.end)) {
			return false;
		}
		if (journey == null) {
			if (other.journey != null) {
				return false;
			}
		} else if (!journey.equals(other.journey)) {
			return false;
		}
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PublicTransportLeg [start=" + start + ", end=" + end + ", journey=" + journey
				+ ", departure=" + departure + ", arrival=" + arrival + ", connections=" + connections
				+ "]";
	}

}
