package edu.kit.ifv.mobitopp.simulation.publictransport;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static edu.kit.ifv.mobitopp.simulation.publictransport.StationBuilder.station;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.serializer.Deserializer;
import edu.kit.ifv.mobitopp.publictransport.serializer.JourneyProvider;
import edu.kit.ifv.mobitopp.publictransport.serializer.NeighbourhoodCoupler;
import edu.kit.ifv.mobitopp.publictransport.serializer.NodeResolver;
import edu.kit.ifv.mobitopp.publictransport.serializer.StationResolver;
import edu.kit.ifv.mobitopp.publictransport.serializer.StopResolver;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Stations;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StopPoints;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.ModifiableJourneys;

public class PublicTransportFromMobitoppTest {

	private Deserializer deserializer;
	private NeighbourhoodCoupler transfers;
	private PublicTransportFromVisum visumConverter;
	private NodeResolver nodeResolver;

	@Before
	public void initialise() throws Exception {
		deserializer = mock(Deserializer.class);
		transfers = mock(NeighbourhoodCoupler.class);
		visumConverter = mock(PublicTransportFromVisum.class);
		nodeResolver = mock(NodeResolver.class);
		when(visumConverter.nodeResolver()).thenReturn(nodeResolver);
	}

	@Test
	public void deserializesStations() throws Exception {
		Station station1 = station().with(1).build();
		Station station2 = station().with(2).build();
		String serializedStation1 = String.valueOf(station1.id());
		String serializedStation2 = String.valueOf(station2.id());
		when(deserializer.deserializeStation(serializedStation1, nodeResolver)).thenReturn(station1);
		when(deserializer.deserializeStation(serializedStation2, nodeResolver)).thenReturn(station2);
		when(deserializer.stations()).thenReturn(asList(serializedStation1, serializedStation2));

		Stations stations = converter().convertStations();

		Stations expectedStations = Stations.from(asList(station1, station2));
		assertThat(stations, is(equalTo(expectedStations)));

		verify(deserializer).deserializeStation(serializedStation1, nodeResolver);
		verify(deserializer).deserializeStation(serializedStation2, nodeResolver);
	}

	@Test
	public void deserializesSeveralStopPoints() throws Exception {
		Stations stations = mock(Stations.class);
		when(deserializer.stops()).thenReturn(asList(serializedStop1(), serializedStop2()));
		when(deserializer.neighbourhoodCoupler(any())).thenReturn(transfers);
		deserialize(stop1(), stations);
		deserialize(stop2(), stations);
		PublicTransportFromMobitopp converter = converter();

		StopPoints stopPoints = converter.convertStopPoints(stations);

		assertThat(stopPoints, is(severalStopPoints()));

		verify(deserializer).stops();
		verify(deserializer).deserializeStop(serializedStop1(), stations);
		verify(deserializer).deserializeStop(serializedStop2(), stations);
		verify(deserializer).neighbourhoodCoupler(any());
		verify(transfers).addNeighboursshipBetween(stop1(), stop2());
		verify(transfers).addNeighboursshipBetween(stop2(), stop1());
	}

	private void deserialize(Stop stop, StationResolver stationResolver) {
		when(deserializer.deserializeStop(stop.name(), stationResolver)).thenReturn(stop);
	}

	@Test
	public void deserializesSeveralJourneys() throws Exception {
		when(deserializer.journeys()).thenReturn(asList(serializedJourney1(), serializedJourney2()));
		deserialize(journey1());
		deserialize(journey2());
		StopPoints stopPoints = mock(StopPoints.class);
		ModifiableJourneys journeys = new ModifiableJourneys();
		journeys.add(journey1());
		journeys.add(journey2());

		ModifiableJourneys deserialized = converter().convertJourneys(stopPoints);

		assertThat(deserialized, is(equalTo(journeys)));
		verify(deserializer).journeys();
		verify(deserializer).deserializeJourney(serializedJourney1());
		verify(deserializer).deserializeJourney(serializedJourney2());
	}

	private void deserialize(ModifiableJourney journey) {
		when(deserializer.deserializeJourney(serialize(journey))).thenReturn(journey);
	}

	@Test
	public void deserializesSeveralConnections() throws Exception {
		when(deserializer.connections())
				.thenReturn(asList(serializedConnection1(), serializedConnection2()));
		StopPoints stopPoints = mock(StopPoints.class);
		ModifiableJourneys journeys = mock(ModifiableJourneys.class);
		deserialize(connection1(), stopPoints, journeys);
		deserialize(connection2(), stopPoints, journeys);
		Connections connections = new Connections();
		connections.add(connection1());
		connections.add(connection2());

		Connections deserialized = converter().convertConnections(stopPoints, journeys);

		assertThat(deserialized, is(equalTo(connections)));
		verify(deserializer).connections();
		verify(deserializer).deserializeConnection(serializedConnection1(), stopPoints, journeys);
		verify(deserializer).deserializeConnection(serializedConnection2(), stopPoints, journeys);
	}

	@Test
	public void createsStationFinderViaVisumConverter() throws Exception {
		Stations stations = mock(Stations.class);

		converter().createStationFinder(stations);

		verify(visumConverter).createStationFinder(stations);
	}

	private String serializedConnection2() {
		return serialize(connection1());
	}

	private String serializedConnection1() {
		return serialize(connection2());
	}

	private Connection connection1() {
		return connection().withId(1).build();
	}

	private Connection connection2() {
		return connection().withId(2).build();
	}

	private void deserialize(
			Connection connection, StopResolver stopResolver, JourneyProvider journeyProvider) {
		when(deserializer.deserializeConnection(serialize(connection), stopResolver, journeyProvider))
				.thenReturn(connection);
	}

	private String serialize(Connection connection) {
		return String.valueOf(connection.id());
	}

	private String serializedJourney2() {
		return serialize(journey2());
	}

	private String serializedJourney1() {
		return serialize(journey1());
	}

	private String serialize(Journey journey) {
		return String.valueOf(journey.id());
	}

	private ModifiableJourney journey1() {
		return journey().withId(1).build();
	}

	private ModifiableJourney journey2() {
		return journey().withId(2).build();
	}

	private PublicTransportFromMobitopp converter() {
		return new PublicTransportFromMobitopp(days(), deserializer, visumConverter);
	}

	private static List<Time> days() {
		return emptyList();
	}

	private static String serializedStop2() {
		return stop2().name();
	}

	private static String serializedStop1() {
		return stop1().name();
	}

	private static Stop stop2() {
		return anotherStop();
	}

	private static Stop stop1() {
		return someStop();
	}

	private static StopPoints severalStopPoints() {
		StopPoints stopPoints = new StopPoints();
		stopPoints.add(stop1());
		stopPoints.add(stop2());
		return stopPoints;
	}
}
