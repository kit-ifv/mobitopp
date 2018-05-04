package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ScannedRoute implements PublicTransportRoute {

	private final Stop start;
	private final Stop end;
	private final Time departure;
	private final Time arrival;
	private final List<Connection> connections;

	public ScannedRoute(
			Stop start, Stop end, Time departure, Time arrival, List<Connection> connections) {
		super();
		this.start = start;
		this.end = end;
		this.departure = departure;
		this.arrival = arrival;
		this.connections = connections;
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
	public Time arrival() {
		return arrival;
	}

	@Override
	public RelativeTime duration() {
		return arrival.differenceTo(departure);
	}

	@Override
	public List<Connection> connections() {
		return Collections.unmodifiableList(connections);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrival == null) ? 0 : arrival.hashCode());
		result = prime * result + ((connections == null) ? 0 : connections.hashCode());
		result = prime * result + ((departure == null) ? 0 : departure.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
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
		ScannedRoute other = (ScannedRoute) obj;
		if (arrival == null) {
			if (other.arrival != null) {
				return false;
			}
		} else if (!arrival.equals(other.arrival)) {
			return false;
		}
		if (connections == null) {
			if (other.connections != null) {
				return false;
			}
		} else if (!connections.equals(other.connections)) {
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
		return "Tour [start=" + start + ", end=" + end + ", departure=" + departure + ", arrival="
				+ arrival + ", connections=" + connections + "]";
	}

}
