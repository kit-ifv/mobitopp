package edu.kit.ifv.mobitopp.simulation.events;

import java.util.TreeSet;

import edu.kit.ifv.mobitopp.time.Time;

public class SimpleEventQueue implements EventQueue {

	private final TreeSet<DemandSimulationEventIfc> events;

	public SimpleEventQueue() {
		events = new TreeSet<DemandSimulationEventIfc>();
	}

	@Override
	public void add(DemandSimulationEventIfc event) {
		verifyDoesNotContain(event);
		events.add(event);
	}

	private void verifyDoesNotContain(DemandSimulationEventIfc event) {
		if (events.contains(event)) {
			throw new IllegalArgumentException("Event already in queue: " + event);
		}
	}

	@Override
	public boolean hasEventsUntil(Time date) {
		return eventsAreAvailable() && firstStartsBefore(date);
	}

	private boolean firstStartsBefore(Time date) {
		return events.first().getSimulationDate().isBeforeOrEqualTo(date);
	}

	private boolean eventsAreAvailable() {
		return events.size() > 0;
	}

	@Override
	public DemandSimulationEventIfc nextEvent() {
		return events.pollFirst();
	}

	@Override
	public int size() {
		return events.size();
	}

}
