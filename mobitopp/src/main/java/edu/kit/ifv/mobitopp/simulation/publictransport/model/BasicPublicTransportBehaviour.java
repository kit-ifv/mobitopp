package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.FootJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.VehicleBehaviour;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.simulation.publictransport.SimulatedVehicleQueue;
import edu.kit.ifv.mobitopp.simulation.publictransport.VehicleQueue;
import edu.kit.ifv.mobitopp.time.Time;

public abstract class BasicPublicTransportBehaviour implements VehicleBehaviour {

	private static final int oneMinute = 1;
	private final PublicTransportResults results;
	private final WaitingArea waitingArea;
	private final RouteSearch viaRouteSearch;
	private final Vehicles vehicles;
	private final VehicleQueue nextDepartures;
	private final VehiclesAtStops waitingVehicles;
	private final DepartedVehicles departedVehicles;

	public BasicPublicTransportBehaviour(
			RouteSearch routeSearch, PublicTransportResults results, Vehicles vehicles) {
		super();
		viaRouteSearch = routeSearch;
		this.results = results;
		this.vehicles = vehicles;
		nextDepartures = new SimulatedVehicleQueue();
		waitingVehicles = new VehiclesAtStops();
		departedVehicles = new DepartedVehicles();
		waitingArea = new WaitingArea();
	}

	@Override
	public void letVehiclesArriveAt(Time currentDate, EventQueue queue) {
		while (vehicles.hasNextUntil(currentDate)) {
			Vehicle vehicle = vehicles.next();
			arrive(vehicle, currentDate);
			vehicle.notifyPassengers(queue, currentDate);
			waitingArea.notifyWaitingPassengers(queue, vehicle, currentDate);
		}
	}

	private void arrive(Vehicle vehicle, Time currentDate) {
		vehicle.moveToNextStop();
		vehicle.nextDeparture().ifPresent(
				departure -> nextDepartures.add(departure, vehicle));
		waitingVehicles.add(vehicle, vehicle.currentStop());
		results.arrive(currentDate, vehicle);
	}

	@Override
	public void letVehiclesDepartAt(Time currentDate) {
		while (nextDepartures.hasNextUntil(currentDate)) {
			Vehicle vehicle = nextDepartures.next();
			depart(vehicle, currentDate);
		}
		waitingArea.logOn(results, currentDate);
		waitingArea.clearEmptyStops();
	}

	private void depart(Vehicle vehicle, Time currentDate) {
		results.depart(currentDate, vehicle);
		waitingVehicles.remove(vehicle, vehicle.currentStop());
		departedVehicles.add(vehicle);
		addNextArrival(vehicle);
	}

	private void addNextArrival(Vehicle vehicle) {
		Consumer<Time> addEvent = arrival -> vehicles.add(vehicle, arrival);
		vehicle.nextArrival().ifPresent(addEvent);
	}

	@Override
	public boolean hasVehicleDeparted(PublicTransportLeg leg) {
		return departedVehicles.hasDeparted(leg);
	}

	@Override
	public boolean isVehicleAvailable(PublicTransportLeg leg) {
		return isFootJourney(leg) || vehicleIsAvailable(leg);
	}

	private boolean vehicleIsAvailable(PublicTransportLeg leg) {
		return waitingVehicles.contains(vehicleServing(leg), leg.start());
	}

	private Vehicle vehicleServing(PublicTransportLeg leg) {
		return vehicles.vehicleServing(leg.journey());
	}

	private boolean isFootJourney(PublicTransportLeg leg) {
		return FootJourney.footJourney.equals(leg.journey());
	}

	@Override
	public boolean hasPlaceInVehicle(PublicTransportLeg leg) {
		return hasPlaceInVehicle(vehicles, leg);
	}

	protected abstract boolean hasPlaceInVehicle(Vehicles vehicles, PublicTransportLeg leg);

	@Override
	public void board(SimulationPerson person, Time time, PublicTransportLeg leg, TripIfc trip) {
		Vehicle vehicle = vehicleServing(leg);
		logCrowded(vehicle, leg);
		vehicle.board(person, leg.end());
		results.board(person, time, leg, trip);
		leaveWaitingArea(person, leg.start());
	}

	private void logCrowded(Vehicle vehicle, PublicTransportLeg leg) {
		if (vehicle.hasFreePlace()) {
			return;
		}
		results.vehicleCrowded(vehicle, leg);
	}

	@Override
	public void getOff(SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip) {
		vehicleServing(part).getOff(person);
		results.getOff(person, time, part, trip);
		enterWaitingArea(person, part.end());
	}

	@Override
	public TripIfc searchNewTrip(
			SimulationPerson person, Time time, PublicTransportTrip trip) {
		results.vehicleFull(person, time, trip);
		Time inOneMinute = time.plusMinutes(oneMinute);
		return trip.derive(inOneMinute, viaRouteSearch);
	}

	@Override
	public void wait(SimulationPerson person, Time time, PublicTransportLeg part, TripIfc trip) {
		results.wait(person, time, part, trip);
	}

	@Override
	public void enterWaitingArea(SimulationPerson simulationPerson, Stop stop) {
		waitingArea.enterAt(stop, simulationPerson);
	}

	@Override
	public void leaveWaitingArea(SimulationPerson person, Stop stop) {
		waitingArea.leaveFrom(stop, person);
	}

}
