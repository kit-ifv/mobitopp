package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static edu.kit.ifv.mobitopp.publictransport.matcher.StopMatcher.hasNeighbour;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.simulation.publictransport.BasePublicTransportConverter.walkingCode;
import static edu.kit.ifv.mobitopp.simulation.publictransport.BasePublicTransportConverter.walkingIndividual;
import static edu.kit.ifv.mobitopp.simulation.publictransport.StationBuilder.station;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumStop;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.ModifiableJourneys;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFinder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Stations;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StopPoints;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.TransferWalkTime;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicles;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;
import edu.kit.ifv.mobitopp.visum.StopAreaPair;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNetworkReader;
import edu.kit.ifv.mobitopp.visum.VisumPtStop;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumPtTransferWalkTimes;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class PublicTransportFromVisumTest {

	private static final Time someDate = Data.someTime();
	private static final Time anotherDate = someDate.plusDays(1);
	private static final List<Time> overSeveralDays = asList(someDate, anotherDate);
	private ModifiableJourneys journeys;
	private Connections connections;
	private Station station1;
	private Station station2;
	private Station station3;
	private Stop stop1;
	private Stop stop2;
	private Stop stop3;
	private Stop stop4;
	private StationFinder stationResolver;
	private Vehicles vehicles;

	@Before
	public void initialise() throws Exception {
		journeys = mock(ModifiableJourneys.class);
		ModifiableJourney journey = mock(ModifiableJourney.class);
		vehicles = mock(Vehicles.class);
		connections = mock(Connections.class);
		stationResolver = mock(StationFinder.class);
		station1 = station().with(1).build();
		station2 = station().with(2).build();
		station3 = station().with(3).build();
		stop1 = stop()
				.withId(0)
				.withExternalId(1)
				.withName("Haltepunkt 1")
				.with(coordinate(9.338369E5f, 6.2698235E6f))
				.build();
		station1.add(stop1);
		stop2 = stop()
				.withId(1)
				.withExternalId(2)
				.withName("Haltepunkt 2")
				.with(coordinate(9.3402744E5f, 6.269817E6f))
				.build();
		station2.add(stop2);
		stop3 = stop()
				.withId(2)
				.withExternalId(3)
				.withName("Haltepunkt 3")
				.with(coordinate(9.33917E5f, 6.269835E6f))
				.build();
		station3.add(stop3);
		stop4 = stop()
				.withId(1)
				.withExternalId(4)
				.withName("Haltepunkt 2")
				.with(coordinate(9.3402744E5f, 6.269817E6f))
				.build();
		station2.add(stop4);
		
		when(journeys.connections()).thenReturn(connections);
		when(journeys.stream()).thenReturn(Stream.empty());
		when(journeys.add(any(), any(), any(), any())).thenReturn(journey);
		when(journey.connections()).thenReturn(new Connections());
	}

	@After
	public void clearTransportSystems()
			throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		ReflectionHelper.clearTransportSystemSetCache();
	}

	@Test
	public void convertSimpleNetwork() throws Exception {
		PublicTransportTimetable publicTransport = convert(simpleNetwork());
		PublicTransportTimetable simpleNetwork = simpleNetworkPublicTransport();

		assertThat(publicTransport, is(equalTo(simpleNetwork)));
		verify(journeys, times(2)).add(any(), any(), any(), eq(someDate));
		verify(journeys).connections();
	}

	private PublicTransportTimetable simpleNetworkPublicTransport() {
		Stations stations = stations(stationsOneAndTwo());
		StopPoints stopPoints = stopPoints();
		stopPoints.add(stop1);
		stopPoints.add(stop4);
		return newPublicTransport(stations, stopPoints);
	}

	private HashMap<Integer, Station> stationsOneAndTwo() {
		HashMap<Integer, Station> stationMap = new HashMap<>();
		stationMap.put(1, station1);
		stationMap.put(2, station2);
		return stationMap;
	}

	private static VisumNetwork simpleNetwork() throws URISyntaxException {
		return load("simpleNetwork.net");
	}

	private static VisumNetwork load(String filename) throws URISyntaxException {
		File file = new File(
				PublicTransportFromVisumTest.class.getResource(filename).toURI());
		VisumNetworkReader networkReader = new VisumNetworkReader();
		return networkReader.readNetwork(file);
	}

	@Test
	public void ensuresSimulationStartIsAtBeginningOfDay() throws Exception {
		Time notAtBeginningOfDay = someDate.plusHours(1);
		PublicTransportTimetable publicTransport = convert(simpleNetwork(), notAtBeginningOfDay);
		PublicTransportTimetable simpleNetwork = simpleNetworkPublicTransport();

		assertThat(publicTransport, is(equalTo(simpleNetwork)));
		verify(journeys, times(2)).add(any(), any(), any(), eq(someDate));
		verify(journeys).connections();
	}

	@Test
	public void convertsSimpleNetworkForMultipleDays() throws Exception {
		PublicTransportTimetable publicTransport = convert(simpleNetwork(), overSeveralDays);
		PublicTransportTimetable simpleNetwork = twoDaysTimetable();

		assertThat(publicTransport, is(equalTo(simpleNetwork)));
		verify(journeys, times(4)).add(any(), any(), any(), any());
		verify(journeys).connections();
	}

	private PublicTransportTimetable twoDaysTimetable() {
		Stations stations = stations(stationsOneAndTwo());
		StopPoints stopPoints = stopPoints();
		stopPoints.add(stop1);
		stopPoints.add(stop4);
		return newPublicTransport(stations, stopPoints);
	}

	@Test
	public void convertTwoJourneyNetwork() throws Exception {
		PublicTransportTimetable publicTransport = convert(twoJourneyNetwork());
		PublicTransportTimetable twoJourneyNetwork = publicTransportNetwork();

		assertThat(publicTransport, is(equalTo(twoJourneyNetwork)));
		verify(journeys, times(3)).add(any(), any(), any(), eq(someDate));
		verify(journeys).connections();
	}

	private static VisumNetwork twoJourneyNetwork() throws URISyntaxException {
		return load("twoJourneys.net");
	}

	private PublicTransportTimetable publicTransportNetwork() {
		HashMap<Integer, Station> stationMap = stationsOneAndTwo();
		stationMap.put(3, station3);
		Stations stations = stations(stationMap);
		StopPoints stopPoints = stopPoints();
		stopPoints.add(stop1);
		stopPoints.add(stop2);
		stopPoints.add(stop3);
		return newPublicTransport(stations, stopPoints);
	}

	@Test
	public void convertTransitionWalkTimes() throws Exception {
		PublicTransportTimetable publicTransport = convert(transitionWalkTimesVisum());
		PublicTransportTimetable simpleNetwork = transitionWalkTimesConverted();

		assertThat(publicTransport, is(equalTo(simpleNetwork)));
		Stop stop1 = publicTransport.stopByExternal(1);
		Stop stop3 = publicTransport.stopByExternal(3);
		assertThat(stop1, hasNeighbour(stop3));
		assertThat(stop3, hasNeighbour(stop1));
		RelativeTime walkTime = RelativeTime.of(5, MINUTES);
		assertThat(stop1.arrivalAt(stop3, someTime()), hasValue(someTime().plus(walkTime)));
		assertThat(stop3.arrivalAt(stop1, someTime()), hasValue(someTime().plus(walkTime)));
		verify(journeys, times(2)).add(any(), any(), any(), eq(someDate));
		verify(journeys).connections();
	}

	@Test
	public void createsCollectionOfStationsFromVisumDataContainingTwoStations() throws Exception {
		new VisumTransportSystem(walkingCode, walkingCode, walkingCode);
		new VisumTransportSystem(walkingIndividual, walkingIndividual, walkingIndividual);
		TransferWalkTime walkTime = mock(TransferWalkTime.class);
		Station firstStation = station().with(0).with(walkTime).build();
		Station secondStation = station().with(1).with(walkTime).build();

		Stations stations = stationsFrom(twoStationNetwork());

		assertThat(stations, containsInAnyOrder(firstStation, secondStation));
	}

	private Map<Integer, VisumPtStop> twoStationNetwork() {
		HashMap<Integer, VisumPtStop> stops = new HashMap<>();
		int id = 0;
		VisumPtStop stop0 = visumStop().withId(id).build();
		stops.put(id, stop0);
		id = 1;
		VisumPtStop stop1 = visumStop().withId(id).build();
		stops.put(id, stop1);
		return stops;
	}

	private Stations stationsFrom(Map<Integer, VisumPtStop> stations) {
		VisumNetwork visumNetwork = mock(VisumNetwork.class);
		PublicTransportFromVisum converter = new PublicTransportFromVisum(overSeveralDays,
				visumNetwork) {
			@Override
			Map<Integer, VisumPtStop> visumStations() {
				return stations;
			}
			
			@Override
			Map<StopAreaPair, VisumPtTransferWalkTimes> visumWalkTimes() {
				return emptyMap();
			}
			
			@Override
			Map<Integer, VisumPtStopPoint> visumStopPoints() {
				return emptyMap();
			}
			
			@Override
			Map<Integer, VisumPtStopArea> visumStopAreas() {
				return emptyMap();
			}
		};
		return converter.convertStations();
	}

	private PublicTransportTimetable convert(VisumNetwork visumNetwork) {
		return convert(visumNetwork, someDate);
	}

	private PublicTransportTimetable convert(VisumNetwork visumNetwork, Time simulationStart) {
		VisumTransportSystem publicWalking = visumNetwork.getTransportSystem(walkingCode);
		PublicTransportConverter network = converter(simulationStart, publicWalking, visumNetwork);
		return network.convert();
	}

	private PublicTransportConverter converter(
			Time simulationStart, VisumTransportSystem publicWalking,
			VisumNetwork visumNetwork) {
		return new PublicTransportFromVisum(asList(publicWalking), simulationStart, visumNetwork) {
			
			@Override
			ModifiableJourneys journeys() {
				return journeys;
			}

			@Override
			Connections connections() {
				return connections;
			}
		};
	}

	private PublicTransportTimetable convert(
			VisumNetwork visumNetwork, List<Time> simulationDays) {
		VisumTransportSystem publicWalking = visumNetwork.getTransportSystem(walkingCode);
		PublicTransportConverter network = converter(publicWalking, simulationDays, visumNetwork);
		return network.convert();
	}

	private PublicTransportConverter converter(
			VisumTransportSystem publicWalking, List<Time> simulationDays,
			VisumNetwork visumNetwork) {
		return new PublicTransportFromVisum(asList(publicWalking), simulationDays, visumNetwork) {

			@Override
			ModifiableJourneys journeys() {
				return journeys;
			}
			
			@Override
			Connections connections() {
				return connections;
			}
		};
	}

	private PublicTransportTimetable transitionWalkTimesConverted() {
		Stations stations = stations(stationsOneAndTwo());
		StopPoints stopPoints = stopPoints();
		stopPoints.add(stop1);
		stopPoints.add(stop2);
		stopPoints.add(stop3);
		return newPublicTransport(stations, stopPoints);
	}

	private PublicTransportTimetable newPublicTransport(Stations stations, StopPoints stopPoints) {
		return new PublicTransportTimetable(connections, stopPoints, journeys, stationResolver, stations, vehicles);
	}

	private StopPoints stopPoints() {
		return new StopPoints();
	}

	private Stations stations(HashMap<Integer, Station> stationMap) {
		return new Stations(stationMap);
	}

	private static VisumNetwork transitionWalkTimesVisum() throws URISyntaxException {
		return load("transitionWalkTimes.net");
	}

}
