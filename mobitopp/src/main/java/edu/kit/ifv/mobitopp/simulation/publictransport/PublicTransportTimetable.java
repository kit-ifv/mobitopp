package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.Collection;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.ConnectionScan;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.TransitNetwork;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.serializer.JourneyProvider;
import edu.kit.ifv.mobitopp.publictransport.serializer.Serializer;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.ModifiableJourneys;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFinder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPaths;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Stations;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StopPoints;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicles;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumStopResolver;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.ProfileConnectionScan;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Store;

public class PublicTransportTimetable implements StationFinder, Timetable {

	private final Connections connections;
	private final StopPoints stopPoints;
	private final StationFinder stationFinder;
	private final Stations stations;
	private final ModifiableJourneys journeys;
	private final Vehicles vehicles;

	public PublicTransportTimetable(
			Connections connections, StopPoints stopPoints, ModifiableJourneys journeys, StationFinder stationFinder,
			Stations stations, Vehicles vehicles) {
		super();
		this.connections = connections;
		this.stopPoints = stopPoints;
		this.stationFinder = stationFinder;
		this.stations = stations;
		this.journeys = journeys;
		this.vehicles = vehicles;
	}

	@Override
	public Connection connectionFor(ConnectionId id) {
		return connections.get(id);
	}
	
	@Override
	public Stop stopFor(int id) {
		return stopPoints.resolve(id);
	}
	
	@Override
	public Stop stopByExternal(int id) {
		return stopPoints.get(id);
	}

	public RouteSearch createRouteScan() {
		Collection<Stop> stops = stopPoints.stops();
		TransitNetwork transitNetwork = TransitNetwork.createOf(stops, connections);
		return new ConnectionScan(transitNetwork);
	}

	public RouteSearch createProfileScan(Store store) {
		Collection<Stop> stops = stopPoints.stops();
		Collection<Connection> prepared = connections.asCollection();
		return ProfileConnectionScan.from(prepared, stops, store);
	}

	public RouteSearch loadProfileScan(Store store) {
		return new ProfileConnectionScan(store);
	}
	
	public Collection<Stop> stops() {
		return stopPoints.stops();
	}

	public Vehicles vehicles() {
		return vehicles;
	}
	
	public VisumStopResolver stopProvider() {
		return stopPoints;
	}

	public JourneyProvider createJourneyProvider() {
		return journeys;
	}

	public Function<Integer, Float> vehicleScaler() {
		return journeys.vehicleScaler();
	}

	public Station stationFor(int id) {
		return stations.getStation(id);
	}

	@Override
	public StationPaths findReachableStations(Location location) {
		return stationFinder.findReachableStations(location);
	}

	public Collection<Connection> createConnections() {
		return connections.asCollection();
	}

	public void serializeTo(Serializer serializer) {
		stopPoints.serializeTo(serializer);
		connections.apply(serializer);
		journeys.serializeJourneysTo(serializer);
		stations.serializeStationsTo(serializer);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connections == null) ? 0 : connections.hashCode());
		result = prime * result + ((journeys == null) ? 0 : journeys.hashCode());
		result = prime * result + ((stopPoints == null) ? 0 : stopPoints.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PublicTransportTimetable other = (PublicTransportTimetable) obj;
		if (connections == null) {
			if (other.connections != null) {
				return false;
			}
		} else if (!connections.equals(other.connections)) {
			return false;
		}
		if (journeys == null) {
			if (other.journeys != null) {
				return false;
			}
		} else if (!journeys.equals(other.journeys)) {
			return false;
		}
		if (stopPoints == null) {
			if (other.stopPoints != null) {
				return false;
			}
		} else if (!stopPoints.equals(other.stopPoints)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PublicTransportTimetable [connections=" + connections + ", stopPoints=" + stopPoints
				+ ", journeys=" + journeys + "]";
	}

}
