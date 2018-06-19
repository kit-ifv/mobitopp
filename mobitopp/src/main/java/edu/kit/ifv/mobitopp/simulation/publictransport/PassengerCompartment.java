package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Passenger;

public class PassengerCompartment {

	private final Map<Stop, SortedSet<Passenger>> passengersPerStop;
	private int passengerCount;

	private PassengerCompartment(Map<Stop, SortedSet<Passenger>> passengersPerStop) {
		super();
		this.passengersPerStop = passengersPerStop;
		passengerCount = 0;
	}

	public void board(Passenger passenger, Stop exitStop) {
		passengersAt(exitStop).add(passenger);
		passengerCount++;
	}

	public void getOff(Passenger passenger, Stop currentStop) {
		if (passengersAt(currentStop).contains(passenger)) {
			passengersAt(currentStop).remove(passenger);
			passengerCount--;
		}
	}

	private SortedSet<Passenger> passengersAt(Stop currentStop) {
		return passengersPerStop.get(currentStop);
	}

	public int count() {
		return passengerCount;
	}

	public void forEachAt(Stop currentStop, Consumer<Passenger> consume) {
		TreeSet<Passenger> treeSet = new TreeSet<>(passengersAt(currentStop));
		treeSet.forEach(consume);
	}

	public static PassengerCompartment forAll(Stream<Stop> stops) {
		Map<Stop, SortedSet<Passenger>> passengersPerStop = new HashMap<>();
		stops.forEach(stop -> passengersPerStop.put(stop, newTreeSet()));
		return new PassengerCompartment(passengersPerStop);
	}

	private static TreeSet<Passenger> newTreeSet() {
		return new TreeSet<>(Comparator.comparing(Passenger::getOid));
	}
}