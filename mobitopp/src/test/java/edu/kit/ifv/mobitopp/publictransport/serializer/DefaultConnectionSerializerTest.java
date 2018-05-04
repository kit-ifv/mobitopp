package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultConnectionSerializerTest {

	private static final Time day = Data.someTime();
	private static final Time someTime = day.plus(RelativeTime.of(1, MINUTES));
	private static final Time anotherTime = someTime.plus(RelativeTime.of(1, MINUTES));

	private BufferedWriter connectionWriter;
	private ConnectionFormat connectionFormat;

	@Before
	public void initialiseMocks() throws Exception {
		connectionWriter = mock(BufferedWriter.class);
		connectionFormat = mock(ConnectionFormat.class);
	}

	@Test
	public void serializesNeededInformationOfConnections() throws Exception {
		Stop someStop = someStop();
		Stop anotherStop = anotherStop();
		Journey someJourney = journey().withId(1).build();
		int id = 1;
		Connection connection = connection()
				.withId(id)
				.startsAt(someStop)
				.endsAt(anotherStop)
				.departsAt(someTime)
				.arrivesAt(anotherTime)
				.partOf(someJourney)
				.build();
		String serialized = "serialized by format";
		when(connectionFormat.serialize(connection, day)).thenReturn(serialized);

		newSerializer().accept(connection);

		verify(connectionWriter).write(serialized);
		verify(connectionWriter).newLine();
		verify(connectionFormat).serialize(connection, day);
	}

	private DefaultConnectionSerializer newSerializer() {
		return new DefaultConnectionSerializer(connectionWriter, day) {

			@Override
			ConnectionFormat connectionFormat() {
				return connectionFormat;
			}
		};
	}
}
