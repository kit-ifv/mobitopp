package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.publictransport.model.BaseStation;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class StationFromVisum extends BaseStation implements Station {

	private final TransferWalkTime walkTime;

	public StationFromVisum(int id, TransferWalkTime walkTime, List<Node> nodes) {
		super(id, nodes);
		this.walkTime = walkTime;
	}

	@Override
	public void add(Stop newStop) {
		Consumer<Stop> newToExisting = stop -> neighbourShipBetween(newStop, stop);
		Consumer<Stop> existingToNew = stop -> neighbourShipBetween(stop, newStop);
		forEach(newToExisting);
		forEach(existingToNew);
		super.add(newStop);
	}
	
	private void neighbourShipBetween(Stop stop, Stop neighbour) {
		Consumer<RelativeTime> createNeighbourShip = time -> stop.addNeighbour(neighbour, time);
		walkTime.walkTime(stop, neighbour).ifPresent(createNeighbourShip);
	}

	public RelativeTime minimumChangeTime(int stopId) {
		return walkTime.minimumChangeTime(stopId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id();
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
		StationFromVisum other = (StationFromVisum) obj;
		if (id() != other.id()) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "StationFromVisum [id()=" + id() + "]";
	}

}
