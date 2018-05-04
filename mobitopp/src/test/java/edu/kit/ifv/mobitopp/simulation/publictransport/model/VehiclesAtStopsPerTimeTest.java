package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.time.Time;

public class VehiclesAtStopsPerTimeTest {

	private BiConsumer<Time, List<StopJourney>> logger;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() throws Exception {
		logger = mock(BiConsumer.class);
	}

	@Test
	public void logsSingleTimeAtStopWhenCreatedForDepartures() throws Exception {
		VehiclesAtStopsPerTime timeAtStop = VehiclesAtStopsPerTime.departuresOf(departingConnection());

		timeAtStop.log(someDate(), logger);

		StopJourney stopJourney = new StopJourney(someStop(), someJourney());
		List<StopJourney> singleJourney = asList(stopJourney);
		verify(logger).accept(someDate(), singleJourney);
	}

	@Test
	public void logsSingleTimeAtStopWhenCreatedForArrivals() throws Exception {
		VehiclesAtStopsPerTime timeAtStop = VehiclesAtStopsPerTime.arrivalsOf(arrivingConnection());

		timeAtStop.log(someDate(), logger);

		StopJourney stopJourney = new StopJourney(someStop(), someJourney());
		List<StopJourney> singleJourney = asList(stopJourney);
		verify(logger).accept(someDate(), singleJourney);
	}

	@Test
	public void logsSeveralElementsWhenCreatedFromSeveralConnections() throws Exception {
		VehiclesAtStopsPerTime timeAtStop = VehiclesAtStopsPerTime.arrivalsOf(severalConnections());

		timeAtStop.log(someDate(), logger);

		StopJourney someJourney = new StopJourney(someStop(), someJourney());
		StopJourney anotherJourney = new StopJourney(anotherStop(), anotherJourney());
		List<StopJourney> severalJourneys = asList(someJourney, anotherJourney);
		verify(logger).accept(someDate(), severalJourneys);
	}

	@Test
	public void logsAnEmptyListWhenCreatedFromNoConnections() throws Exception {
		VehiclesAtStopsPerTime timeAtStop = VehiclesAtStopsPerTime.arrivalsOf(Collections.emptyList());

		timeAtStop.log(someDate(), logger);

		verify(logger).accept(someDate(), Collections.emptyList());
	}

	private List<Connection> severalConnections() {
		return asList(someArrivingConnection(), anotherArrivingConnection());
	}

	private static List<Connection> departingConnection() {
		Connection connection = connection()
				.startsAt(someStop())
				.departsAt(someTime())
				.partOf(someJourney())
				.build();
		return asList(connection);
	}

	private static List<Connection> arrivingConnection() {
		return asList(someArrivingConnection());
	}

	private static Connection someArrivingConnection() {
		return connection().endsAt(someStop()).arrivesAt(someTime()).partOf(someJourney()).build();
	}

	private static Connection anotherArrivingConnection() {
		return connection()
				.endsAt(anotherStop())
				.arrivesAt(someTime())
				.partOf(anotherJourney())
				.build();
	}

	private static Time someTime() {
		return someDate();
	}

	private static Journey someJourney() {
		return journey().withId(1).build();
	}

	private static Journey anotherJourney() {
		return journey().withId(2).build();
	}

	private static Time someDate() {
		return tooEarly().plusMinutes(1);
	}

	private static Time tooEarly() {
		return Data.someTime();
	}

}
