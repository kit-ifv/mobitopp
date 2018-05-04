package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

class WaitingArea {

	private final Map<Stop, Set<SimulationPerson>> waiting;

	public WaitingArea() {
		super();
		waiting = new HashMap<>();
	}

	void enterAt(Stop stop, SimulationPerson person) {
		Set<SimulationPerson> persons = waiting.getOrDefault(stop, new LinkedHashSet<>());
		persons.add(person);
		waiting.put(stop, persons);
	}

	void leaveFrom(Stop stop, SimulationPerson person) {
		Set<SimulationPerson> persons = waiting.getOrDefault(stop, new LinkedHashSet<>());
		persons.remove(person);
		waiting.put(stop, persons);
	}

	void logOn(PublicTransportLogger logger, Time time) {
		for (Entry<Stop, Set<SimulationPerson>> entry : waiting.entrySet()) {
			logger.waitingAt(entry.getKey(), time, entry.getValue().size());
		}
	}

	void clearEmptyStops() {
		Set<Stop> emptyStops = waiting
				.entrySet()
				.stream()
				.filter(empty())
				.map(Entry::getKey)
				.collect(toSet());
		for (Stop stop : emptyStops) {
			waiting.remove(stop);
		}
	}

	private Predicate<Entry<Stop, Set<SimulationPerson>>> empty() {
		return entry -> entry.getValue().isEmpty();
	}

	public void notifyWaitingPassengers(
			EventQueue queue, Vehicle vehicle, Time currentDate) {
		Stop currentStop = vehicle.currentStop();
		if (waiting.containsKey(currentStop)) {
			notifiy(queue, vehicle, currentDate, currentStop);
		}
	}

	private void notifiy(
			EventQueue queue, Vehicle vehicle, Time currentDate, Stop currentStop) {
		Set<SimulationPerson> set = waiting.get(currentStop);

		for (SimulationPerson passenger : new LinkedHashSet<>(set)) {
			passenger.vehicleArriving(queue, vehicle, currentDate);
		}
	}

}
