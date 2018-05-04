package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.serializer.ConnectionDeserializer;
import edu.kit.ifv.mobitopp.publictransport.serializer.Deserializer;
import edu.kit.ifv.mobitopp.publictransport.serializer.JourneyDeserializer;
import edu.kit.ifv.mobitopp.publictransport.serializer.NodeResolver;
import edu.kit.ifv.mobitopp.publictransport.serializer.StationDeserializer;
import edu.kit.ifv.mobitopp.publictransport.serializer.StopDeserializer;
import edu.kit.ifv.mobitopp.publictransport.serializer.StopResolver;
import edu.kit.ifv.mobitopp.publictransport.serializer.TimetableDeserializer;

public class TimetableDeserializerTest {

	private StopResolver stopResolver;
	private JourneyProvider journeyProvider;
	private StationResolver stationResolver;
	private StationDeserializer stationDeserializer;
	private StopDeserializer stopDeserializer;
	private ConnectionDeserializer connectionDeserializer;
	private JourneyDeserializer journeyDeserializer;
	private NodeResolver nodeResolver;

	@Before
	public void initialiseMocks() throws Exception {
		stationDeserializer = mock(StationDeserializer.class);
		stopDeserializer = mock(StopDeserializer.class);
		connectionDeserializer = mock(ConnectionDeserializer.class);
		journeyDeserializer = mock(JourneyDeserializer.class);
		stopResolver = mock(StopResolver.class);
		journeyProvider = mock(JourneyProvider.class);
		stationResolver = mock(StationResolver.class);
		nodeResolver = mock(NodeResolver.class);
	}
	
	@Test
	public void providesListOfSerializedStations() throws Exception {
		@SuppressWarnings("unchecked")
		List<String> serializedStations = mock(List.class);
		when(stationDeserializer.stations()).thenReturn(serializedStations );
		
		List<String> stations = newDeserializer().stations();
		
		assertThat(stations , is(equalTo(serializedStations)));
	}

	@Test
	public void deserializesStation() throws Exception {
		Station station = mock(Station.class);
		String serialized = "serialized";
		when(stationDeserializer.deserializeStation(serialized, nodeResolver)).thenReturn(station);
		
		Station deserialized = newDeserializer().deserializeStation(serialized, nodeResolver);
		
		assertThat(deserialized, is(equalTo(station)));
		
		verify(stationDeserializer).deserializeStation(serialized, nodeResolver);
	}

	@Test
	public void deserializesStop() throws Exception {
		Stop stop = someStop();
		String serialized = stop.name();
		when(stopDeserializer.deserializeStop(serialized, stationResolver)).thenReturn(stop);

		Stop deserialized = newDeserializer().deserializeStop(serialized, stationResolver);

		assertThat(deserialized, is(equalTo(stop)));

		verify(stopDeserializer).deserializeStop(serialized, stationResolver);
	}

	@Test
	public void deserializeConnections() throws Exception {
		Connection connection = connection().build();
		String serialized = "serialized";
		when(connectionDeserializer.deserializeConnection(serialized, stopResolver, journeyProvider))
				.thenReturn(connection);

		Connection deserialized = newDeserializer().deserializeConnection(serialized, stopResolver,
				journeyProvider);

		assertThat(deserialized, is(equalTo(connection)));

		verify(connectionDeserializer).deserializeConnection(serialized, stopResolver, journeyProvider);
	}

	@Test
	public void deserializesJourneys() throws Exception {
		String serialized = "serialized";
		ModifiableJourney journey = mock(ModifiableJourney.class);
		when(journeyDeserializer.deserializeJourney(serialized)).thenReturn(journey);

		ModifiableJourney deserialized = newDeserializer().deserializeJourney(serialized);

		assertThat(deserialized, is(journey));

		verify(journeyDeserializer).deserializeJourney(serialized);
	}

	private Deserializer newDeserializer() {
		return new TimetableDeserializer(stopDeserializer, connectionDeserializer, journeyDeserializer,
				stationDeserializer);
	}
}
