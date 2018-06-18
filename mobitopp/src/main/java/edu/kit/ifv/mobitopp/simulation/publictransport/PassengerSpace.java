package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Passenger;

public class PassengerSpace {

	private final Map<Stop, Set<Passenger>> passengersPerStop;
	private int passengerCount;

	public PassengerSpace(Map<Stop, Set<Passenger>> passengersPerStop) {
		super();
		this.passengersPerStop = passengersPerStop;
		passengerCount = 0;
	}

	public void board(Passenger person, Stop exitStop) {
		passengersPerStop.get(exitStop).add(person);
		passengerCount++;
	}

	public void getOff(Passenger person, Stop currentStop) {
		passengersPerStop.get(currentStop).remove(person);
		passengerCount--;
	}

	public int count() {
		return passengerCount;
	}

	public void forEachAt(Stop currentStop, Consumer<Passenger> consume) {
		TreeSet<Passenger> treeSet = new TreeSet<>(Comparator.comparing(Passenger::getOid));
		treeSet.addAll(passengersPerStop.get(currentStop));
		treeSet.forEach(consume);
	}

}