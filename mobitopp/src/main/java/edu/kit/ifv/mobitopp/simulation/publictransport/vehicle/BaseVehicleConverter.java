package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseVehicleConverter implements VehicleTimesConverter {

	public BaseVehicleConverter() {
		super();
	}

	@Override
	public VehicleTimes convert(Collection<Connection> connections) {
		verify(connections);
		Iterator<WaitTime> waiting = waiting(connections);
		Iterator<TravelTime> driving = driving(connections);
		Time firstDeparture = connections.iterator().next().departure();
		return new VehicleTimes(firstDeparture, driving, waiting);
	}

	private void verify(Collection<Connection> connections) {
		if (connections.isEmpty()) {
			throw warn(new IllegalArgumentException("Cannot create a route without connections."), log);
		}
	}

	private Iterator<WaitTime> waiting(Collection<Connection> connections) {
		List<WaitTime> waiting = new ArrayList<>();
		Iterator<Connection> iterator = connections.iterator();
		Connection previous = iterator.next();
		while (iterator.hasNext()) {
			Connection current = iterator.next();
			waiting.add(waitingBetween(previous, current));
			previous = current;
		}
		return waiting.iterator();
	}

	private Iterator<TravelTime> driving(Collection<Connection> connections) {
		return connections.stream().map(this::travelTimeOf).iterator();
	}

	protected abstract TravelTime travelTimeOf(Connection current);

	protected abstract WaitTime waitingBetween(Connection previous, Connection current);

}