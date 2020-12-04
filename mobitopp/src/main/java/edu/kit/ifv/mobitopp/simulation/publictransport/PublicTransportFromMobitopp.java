package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.serializer.Deserializer;
import edu.kit.ifv.mobitopp.publictransport.serializer.NeighbourhoodCoupler;
import edu.kit.ifv.mobitopp.publictransport.serializer.NodeResolver;
import edu.kit.ifv.mobitopp.publictransport.serializer.StationResolver;
import edu.kit.ifv.mobitopp.publictransport.serializer.StopResolver;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.ModifiableJourneys;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFinder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Stations;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StopPoints;
import edu.kit.ifv.mobitopp.time.Time;

public class PublicTransportFromMobitopp extends BasePublicTransportConverter {

	private final SearchFootpath visumConverter;
	private final Deserializer deserializer;

	public PublicTransportFromMobitopp(List<Time> simulationDates, Deserializer deserializer, SearchFootpath visumConverter) {
		super(simulationDates);
		this.deserializer = deserializer;
		this.visumConverter = visumConverter;
	}

	@Override
	protected StopPoints convertStopPoints(Stations stations) {
		StopPoints stopPoints = new StopPoints();
		try {
			convertStopsTo(stopPoints::add, stations);
		} catch (IOException e) {
			e.printStackTrace();
		}
		stopPoints.initialiseNeighbourhood(footpaths(stopPoints));
		return stopPoints;
	}

	NeighbourhoodCoupler footpaths(StopResolver stopResolver) {
		return deserializer().neighbourhoodCoupler(stopResolver);
	}

	private void convertStopsTo(Consumer<Stop> toConsumer, StationResolver stationResolver) throws IOException {
		for (String serializedStop : stopPoints()) {
			convert(serializedStop, toConsumer, stationResolver);
		}
	}
	
	List<String> stopPoints() throws IOException {
		return deserializer().stops();
	}
	
	private void convert(String serialized, Consumer<Stop> consumer, StationResolver stationResolver) throws IOException {
		Stop stop = deserializer().deserializeStop(serialized, stationResolver);
		consumer.accept(stop);
	}
	
	@Override
	protected ModifiableJourneys convertJourneys(StopPoints stopPoints) {
		ModifiableJourneys journeys = new ModifiableJourneys();
		try {
			convertJourneysTo(journeys::add);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return journeys;
	}

	private void convertJourneysTo(Consumer<ModifiableJourney> consumer) throws IOException {
		for (String serialized : journeys()) {
			ModifiableJourney journey = deserializer().deserializeJourney(serialized);
			consumer.accept(journey);
		}
	}

	List<String> journeys() {
		return deserializer().journeys();
	}

	@Override
	protected Connections convertConnections(StopPoints stopPoints, ModifiableJourneys journeys) {
		Connections connections = new Connections();
		try {
			convertConnectionsTo(connections::add, stopPoints, journeys);
		} catch (IOException e) {
			throw new RuntimeException("Could not deserialize connections.");
		}
		return connections;
	}
	
	private void convertConnectionsTo(
			Consumer<Connection> consumer, StopPoints stopPoints, ModifiableJourneys journeys) throws IOException {
		for (String serialized : connections()) {
			Connection connection = deserializer().deserializeConnection(serialized, stopPoints, journeys);
			consumer.accept(connection);
		}
	}

	List<String> connections() {
		return deserializer().connections();
	}

	@Override
	protected Stations convertStations() {
		List<Station> stations = new ArrayList<>();
		for (String serialized : deserializer().stations()) {
			stations.add(deserializer().deserializeStation(serialized, nodeResolver()));
		}
		return Stations.from(stations);
	}

	private NodeResolver nodeResolver() {
		return visumConverter.nodeResolver();
	}
	
	@Override
	protected StationFinder createStationFinder(Stations stations) {
		return visumConverter.createStationFinder(stations);
	}

	@Override
	protected void createFactory(Stations stations) {
	}
	
	private Deserializer deserializer() {
		return deserializer;
	}

}
