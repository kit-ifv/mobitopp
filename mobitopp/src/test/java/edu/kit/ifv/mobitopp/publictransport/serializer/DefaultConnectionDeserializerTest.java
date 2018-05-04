package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultConnectionDeserializerTest {

	private static final Time day = Data.someTime();

	private File connectionFile;
	private ConnectionFormat connectionFormat;
	private StopResolver stopResolver;
	private JourneyProvider journeyProvider;

	@Before
	public void initialiseMocks() throws Exception {
		connectionFormat = mock(ConnectionFormat.class);
		stopResolver = mock(StopResolver.class);
		journeyProvider = mock(JourneyProvider.class);
	}

	@Test
	public void deserializeConnections() throws Exception {
		Connection connection = connection().build();
		String serialized = "serialized";
		when(connectionFormat.deserialize(serialized, stopResolver, journeyProvider, day))
				.thenReturn(connection);

		Connection deserialized = newSerializer().deserializeConnection(serialized, stopResolver,
				journeyProvider);

		assertThat(deserialized, is(equalTo(connection)));

		verify(connectionFormat).deserialize(serialized, stopResolver, journeyProvider, day);
	}

	private DefaultConnectionDeserializer newSerializer() {
		return new DefaultConnectionDeserializer(connectionFile, day) {

			@Override
			ConnectionFormat connectionFormat() {
				return connectionFormat;
			}
		};
	}
}
