package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.TreeSet;

import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.Time;

public class SimulatedVehicleQueue implements VehicleQueue {

	private final TreeSet<VehicleEvent> events;

	public SimulatedVehicleQueue() {
		super();
		events = new TreeSet<>();
	}

	@Override
	public boolean hasNextUntil(Time time) {
		return hasEvents() && isFirstEventBeforeOrAt(time);
	}

	private boolean hasEvents() {
		return !events.isEmpty();
	}

	private boolean isFirstEventBeforeOrAt(Time time) {
		return events.first().time().isBeforeOrEqualTo(time);
	}

	@Override
	public Vehicle next() {
		return events.pollFirst().vehicle();
	}

	@Override
	public void add(Time nextProcessingTime, Vehicle vehicle) {
		add(new VehicleEvent(nextProcessingTime, vehicle));
	}

	void add(VehicleEvent event) {
		events.add(event);
	}

}
