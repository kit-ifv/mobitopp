package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Passenger;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class SimulatedVehicle implements Vehicle {

	private final Journey journey;
	private final VehicleLocation location;
	private final VehicleConnections connections;
	private final Map<Stop, Set<Passenger>> passengersPerStop;
	private int passengerCount;

	private SimulatedVehicle(
			Journey journey, VehicleLocation location, VehicleConnections connections,
			Map<Stop, Set<Passenger>> passengersPerStop) {
		super();
		verifyExisting(journey);
		this.journey = journey;
		this.location = location;
		this.connections = connections;
		this.passengersPerStop = passengersPerStop;
		passengerCount = 0;
	}

	public static Vehicle from(Journey journey) {
		verifyExisting(journey);
		Collection<Connection> connectionsFromDepot = includeDepot(journey);
		Route route = Route.from(connectionsFromDepot);
		VehicleConnections connections = new VehicleConnections(connectionsFromDepot);
		VehicleLocation location = new VehicleLocation(route);
		Map<Stop, Set<Passenger>> passengersPerStop = initialisePassengerSpace(route);
		return new SimulatedVehicle(journey, location, connections, passengersPerStop);
	}

	private static List<Connection> includeDepot(Journey journey) {
		Collection<Connection> withPassengers = journey.connections().asCollection();
		ArrayList<Connection> connections = new ArrayList<>();
		connections.add(depotExit(journey));
		connections.addAll(withPassengers);
		return connections;
	}

	private static Connection depotExit(Journey journey) {
		Collection<Connection> connections = journey.connections().asCollection();
		Connection firstConnection = connections.iterator().next();
		Stop start = firstConnection.start();
		Stop depot = depot();
		Time departure = firstConnection.departure();
		return Connection.from(depot.id(), depot, start, departure, departure, journey,
				RoutePoints.from(depot, start));
	}

	private static Stop depot() {
		Point2D depotLocation = new Point2D.Double();
		Station depot = new DepotStation();
		return new Stop(depot.id(), "depot", depotLocation, RelativeTime.ZERO, depot, depot.id());
	}

	private static Map<Stop, Set<Passenger>> initialisePassengerSpace(Route route) {
		HashMap<Stop, Set<Passenger>> space = new HashMap<>();
		route.forEach(stop -> space.put(stop, new TreeSet<>(Comparator.comparing(Passenger::getOid))));
		return Collections.unmodifiableMap(space);
	}

	private static void verifyExisting(Journey journey) {
		if (journey.connections().asCollection().isEmpty()) {
			throw new IllegalStateException("No connections are assigned to the vehicle: " + journey);
		}
	}

	@Override
	public int journeyId() {
		return journey.id();
	}

	@Override
	public void board(Passenger person, Stop exitStop) {
		passengersPerStop.get(exitStop).add(person);
		passengerCount++;
	}

	@Override
	public void getOff(Passenger person) {
		passengersPerStop.get(currentStop()).remove(person);
		passengerCount--;
	}

	@Override
	public int passengerCount() {
		return passengerCount;
	}

	@Override
	public boolean hasFreePlace() {
		return journey.capacity() > passengerCount();
	}

	@Override
	public void moveToNextStop() {
		location.move();
		connections.move();
	}

	@Override
	public Stop currentStop() {
		return location.current();
	}

	@Override
	public Optional<Connection> nextConnection() {
		return connections.nextConnection();
	}
	
	@Override
	public Optional<Time> nextDeparture() {
		return connections.nextDeparture();
	}

	@Override
	public Optional<Time> nextArrival() {
		return connections.nextArrival();
	}

	@Override
	public Time firstDeparture() {
		return connections.firstDeparture();
	}

	@Override
	public void notifyPassengers(EventQueue queue, Time currentDate) {
		TreeSet<Passenger> treeSet = new TreeSet<>(Comparator.comparing(Passenger::getOid));
		treeSet.addAll(passengersPerStop.get(currentStop()));
		for (Passenger passenger : treeSet) {
			passenger.arriveAtStop(queue, currentDate);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((journey == null) ? 0 : journey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimulatedVehicle other = (SimulatedVehicle) obj;
		if (journey == null) {
			if (other.journey != null)
				return false;
		} else if (!journey.equals(other.journey))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimulatedVehicle [journey=" + journey + "]";
	}

}
