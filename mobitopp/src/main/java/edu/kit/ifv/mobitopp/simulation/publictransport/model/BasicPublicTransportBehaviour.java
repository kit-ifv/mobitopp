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
	private final PublicTransportLogger logger;
	private final WaitingArea waitingArea;
	private final RouteSearch viaRouteSearch;
	private final Vehicles vehicles;
	private final VehicleQueue nextDepartures;
	private final WaitingVehicles waitingVehicles;

	public BasicPublicTransportBehaviour(
			RouteSearch routeSearch, PublicTransportLogger logger, Vehicles vehicles) {
		super();
		viaRouteSearch = routeSearch;
		this.logger = logger;
		this.vehicles = vehicles;
		nextDepartures = new SimulatedVehicleQueue();
		waitingVehicles = new WaitingVehicles();
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
		waitingVehicles.wait(vehicle, vehicle.currentStop());
		logger.arrive(currentDate, vehicle);
	}

	@Override
	public void letVehiclesDepartAt(Time currentDate) {
		while (nextDepartures.hasNextUntil(currentDate)) {
			Vehicle vehicle = nextDepartures.next();
			depart(vehicle, currentDate);
		}
		waitingArea.logOn(logger, currentDate);
		waitingArea.clearEmptyStops();
	}

	private void depart(Vehicle vehicle, Time currentDate) {
		logger.depart(currentDate, vehicle);
		waitingVehicles.drive(vehicle, vehicle.currentStop());
		addNextArrival(vehicle);
	}

	private void addNextArrival(Vehicle vehicle) {
		Consumer<Time> addEvent = arrival -> vehicles.add(vehicle, arrival);
		vehicle.nextArrival().ifPresent(addEvent);
	}

	@Override
	public boolean isVehicleAvailable(PublicTransportLeg leg) {
		return isFootJourney(leg) || vehicleIsAvailable(leg);
	}

	private boolean vehicleIsAvailable(PublicTransportLeg leg) {
		return waitingVehicles.isWaiting(vehicles.vehicleServing(leg.journey()), leg.start());
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
	public void board(SimulationPerson person, Time time, PublicTransportLeg part) {
		Vehicle vehicle = vehicles.vehicleServing(part.journey());
		logCrowded(vehicle, part);
		vehicle.board(person, part.end());
		logger.board(person, time, part);
		leaveWaitingArea(person, part.start());
	}

	private void logCrowded(Vehicle vehicle, PublicTransportLeg leg) {
		if (vehicle.hasFreePlace()) {
			return;
		}
		logger.vehicleCrowded(vehicle, leg);
	}

	@Override
	public void getOff(SimulationPerson person, Time time, PublicTransportLeg part) {
		vehicles.vehicleServing(part.journey()).getOff(person);
		logger.getOff(person, time, part);
		enterWaitingArea(person, part.end());
	}

	@Override
	public TripIfc searchNewTrip(
			SimulationPerson person, Time time, PublicTransportTrip trip) {
		logger.vehicleFull(person, time, trip);
		Time inOneMinute = time.plusMinutes(oneMinute);
		return trip.derive(inOneMinute, viaRouteSearch);
	}

	@Override
	public void wait(SimulationPerson person, Time time, PublicTransportLeg part) {
		logger.wait(person, time, part);
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
