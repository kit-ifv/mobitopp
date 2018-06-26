package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.Time;

public abstract class BaseVehicleConverter implements VehicleTimesConverter {

	public BaseVehicleConverter() {
		super();
	}

	@Override
	public VehicleTimes convert(Collection<Connection> connections) {
		verify(connections);
		Iterator<VehicleWaiting> waiting = waiting(connections);
		Iterator<VehicleDriving> driving = driving(connections);
		Time firstDeparture = connections.iterator().next().departure();
		return new VehicleTimes(firstDeparture, driving, waiting);
	}

	private void verify(Collection<Connection> connections) {
		if (connections.isEmpty()) {
			throw new IllegalArgumentException("Cannot create a route without connections.");
		}
	}

	private Iterator<VehicleWaiting> waiting(Collection<Connection> connections) {
		List<VehicleWaiting> waiting = new ArrayList<>();
		Iterator<Connection> iterator = connections.iterator();
		Connection previous = iterator.next();
		while (iterator.hasNext()) {
			Connection current = iterator.next();
			waiting.add(waitingBetween(previous, current));
			previous = current;
		}
		return waiting.iterator();
	}

	private Iterator<VehicleDriving> driving(Collection<Connection> connections) {
		return connections.stream().map(this::travelTimeOf).iterator();
	}

	protected abstract VehicleDriving travelTimeOf(Connection current);

	protected abstract VehicleWaiting waitingBetween(Connection previous, Connection current);

}