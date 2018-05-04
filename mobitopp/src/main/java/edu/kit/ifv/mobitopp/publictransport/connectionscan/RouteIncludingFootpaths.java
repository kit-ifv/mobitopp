package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

class RouteIncludingFootpaths implements PublicTransportRoute {

	private final PublicTransportRoute route;
	private final StopPath start;
	private final StopPath end;

	RouteIncludingFootpaths(PublicTransportRoute route, StopPath start, StopPath end) {
		super();
		this.route = route;
		this.start = start;
		this.end = end;
	}
	
	@Override
	public Stop start() {
		return route.start();
	}
	
	@Override
	public Stop end() {
		return route.end();
	}

	@Override
	public Time arrival() {
		return route.arrival().plus(end.duration());
	}
	
	@Override
	public RelativeTime duration() {
		return start.duration().plus(route.duration()).plus(end.duration());
	}
	
	@Override
	public List<Connection> connections() {
		return route.connections();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((route == null) ? 0 : route.hashCode());
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
		RouteIncludingFootpaths other = (RouteIncludingFootpaths) obj;
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
		if (route == null) {
			if (other.route != null) {
				return false;
			}
		} else if (!route.equals(other.route)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RouteIncludingFootpaths [route=" + route + ", start=" + start + ", end=" + end + "]";
	}

}
