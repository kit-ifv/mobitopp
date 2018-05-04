package edu.kit.ifv.mobitopp.publictransport.model;

import java.awt.geom.Point2D;
import java.util.Optional;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class Stop {

	private final int id;
	private final String name;
	private final Point2D coordinate;
	private final RelativeTime minimumChangeTime;
	private final Neighbourhood neighbours;
	private final Station station;
	private final int externalId;

	public Stop(
			int id, String name, Point2D coordinate, RelativeTime minimumChangeTime, Station station,
			int externalId) {
		super();
		this.id = id;
		this.name = name;
		this.coordinate = coordinate;
		this.minimumChangeTime = minimumChangeTime;
		this.station = station;
		this.externalId = externalId;
		neighbours = new Neighbourhood(this);
	}
	
	public int id() {
		return id;
	}

	public String name() {
		return name;
	}

	public Point2D coordinate() {
		return coordinate;
	}

	public RelativeTime changeTime() {
		return minimumChangeTime;
	}

	public Station station() {
		return station;
	}

	public int externalId() {
		return externalId;
	}
	
	public Time addChangeTimeTo(Time currentArrival) {
		return currentArrival.plus(minimumChangeTime);
	}

	public Time subtractChangeTimeFrom(Time departure) {
		return departure.minus(minimumChangeTime);
	}

	public void addNeighbour(Stop toStop, RelativeTime duration) {
		if (equals(toStop)) {
			return;
		}
		neighbours.add(toStop, duration);
	}

	public Neighbourhood neighbours() {
		return neighbours;
	}

	public Optional<Time> arrivalAt(Stop stop, Time arrival) {
		Optional<RelativeTime> walkTime = neighbours.walkTimeTo(stop);
		return walkTime.map(arrival::plus);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Stop other = (Stop) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Stop [id=" + id + ", name=" + name + ", externalId=" + externalId + "]";
	}

}
