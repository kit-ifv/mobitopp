package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class CsvConnectionFormatTest {

	private static final Time day = Data.someTime();
	private static final Time someTime = day.plusMinutes(1);
	private static final Time anotherTime = someTime.plusMinutes(1);
	private Stop start;
	private Stop end;
	private ModifiableJourney journey;
	private int id;
	private Connection connection;
	private StopResolver stopResolver;
	private JourneyProvider journeyProvider;

	@Before
	public void initialise() throws Exception {
		stopResolver = mock(StopResolver.class);
		journeyProvider = mock(JourneyProvider.class);
		start = someStop();
		end = anotherStop();
		journey = journey().withId(1).build();
		id = 1;
		connection = connection()
				.withId(id)
				.startsAt(start)
				.endsAt(end)
				.departsAt(someTime)
				.arrivesAt(anotherTime)
				.partOf(journey)
				.with(asList(new Point2D.Double(3, 3)))
				.build();
		resolve(start);
		resolve(end);
		resolve(journey);
	}

	private void resolve(ModifiableJourney journey) {
		when(journeyProvider.get(journey.id())).thenReturn(journey);
	}

	private void resolve(Stop stop) {
		when(stopResolver.resolve(stop.id())).thenReturn(stop);
	}

	@Test
	public void serializesAllInformation() throws Exception {
		String serialized = serialize(connection);

		assertThat(serialized, is(equalTo(id + ";0;1;60;120;1;0.0,0.0#3.0,3.0#1.0,2.0")));
	}

	@Test
	public void deserializeConnection() throws Exception {
		String serialized = serialize(connection);
		
		Connection deserialized = new CsvConnectionFormat().deserialize(serialized, stopResolver,
				journeyProvider, day);

		assertThat(deserialized, is(equalTo(connection)));
		assertThat(deserialized.id(), is(equalTo(connection.id())));
		assertThat(deserialized.points(), is(equalTo(connection.points())));
		assertThat(journey.connections().asCollection(), contains(connection));
		
		verify(stopResolver).resolve(start.id());
		verify(stopResolver).resolve(end.id());
		verify(journeyProvider).get(journey.id());
	}

	private static String serialize(Connection connection) {
		return new CsvConnectionFormat().serialize(connection, day);
	}

}
