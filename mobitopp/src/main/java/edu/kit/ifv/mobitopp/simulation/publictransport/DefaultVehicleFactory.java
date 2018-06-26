package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultVehicleFactory implements VehicleFactory {

	private final VehicleTimesConverter vehicleTimes;

	public DefaultVehicleFactory() {
		this(new TimetableToVehicleConverter());
	}
	
	public DefaultVehicleFactory(VehicleTimesConverter vehicleTimes) {
		super();
		this.vehicleTimes = vehicleTimes;
	}

	@Override
	public Vehicle createFrom(Journey journey) {
		verifyExisting(journey);
		Collection<Connection> connectionsFromDepot = includeDepot(journey);
		Route route = Route.from(connectionsFromDepot);
		VehicleTimes connections = vehicleTimes.convert(connectionsFromDepot);
		VehicleLocation location = new VehicleLocation(route);
		PassengerCompartment passengersPerStop = initialisePassengerSpace(route, journey);
		VehicleRoute vehicleRoute = new VehicleRoute(journey, location, connections);
		return new SimulatedVehicle(vehicleRoute, passengersPerStop);
	}

	private List<Connection> includeDepot(Journey journey) {
		Collection<Connection> withPassengers = journey.connections().asCollection();
		ArrayList<Connection> connections = new ArrayList<>();
		connections.add(depotExit(journey));
		connections.addAll(withPassengers);
		return connections;
	}

	private Connection depotExit(Journey journey) {
		Collection<Connection> connections = journey.connections().asCollection();
		Connection firstConnection = connections.iterator().next();
		Stop start = firstConnection.start();
		Stop depot = depot();
		Time departure = firstConnection.departure();
		return Connection.from(connectionId(depot), depot, start, departure, departure, journey,
				RoutePoints.from(depot, start));
	}

	private ConnectionId connectionId(Stop depot) {
		return ConnectionId.of(depot.id());
	}

	Stop depot() {
		Point2D depotLocation = new Point2D.Double();
		Station depot = new DepotStation();
		return new Stop(depot.id(), "depot", depotLocation, RelativeTime.ZERO, depot, depot.id());
	}

	private PassengerCompartment initialisePassengerSpace(Route route, Journey journey) {
		return PassengerCompartment.forAll(route.stream(), journey.capacity());
	}

	private void verifyExisting(Journey journey) {
		if (journey.connections().asCollection().isEmpty()) {
			throw new IllegalArgumentException("No connections are assigned to the vehicle: " + journey);
		}
	}

}
