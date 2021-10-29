package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import java.util.Collection;
import java.util.Iterator;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class OnTimeTravelTimeCalculator implements TravelTimeCalculator {

	private final Iterator<Connection> connections;

	public OnTimeTravelTimeCalculator(Iterator<Connection> connections) {
		this.connections = connections;
	}

	public static OnTimeTravelTimeCalculator of(Collection<Connection> connections) {
		return new OnTimeTravelTimeCalculator(connections.iterator());
	}

	@Override
	public TravelTime next(Time currentTime) {
		Connection next = connections.next();
		RelativeTime duration = next.duration();
		return new TravelTime(next.id(), duration);
	}

	@Override
	public boolean hasNext() {
		return connections.hasNext();
	}

}
