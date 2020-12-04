package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class TimetableSerializerTest {

	private DefaultStationSerializer stationSerializer;
	private DefaultStopSerializer stopSerializer;
	private DefaultConnectionSerializer connectionSerializer;
	private DefaultJourneySerializer journeySerializer;

	@Before
	public void initialiseMocks() throws Exception {
		stationSerializer = mock(DefaultStationSerializer.class);
		stopSerializer = mock(DefaultStopSerializer.class);
		connectionSerializer = mock(DefaultConnectionSerializer.class);
		journeySerializer = mock(DefaultJourneySerializer.class);
	}

	@Test
	public void serializesStation() throws Exception {
		Station station = mock(Station.class);
		
		newSerializer().serialize(station);
		
		verify(stationSerializer).serialize(station);
	}

	@Test
	public void serializesNeededInformationOfStops() throws Exception {
		Stop stop = mock(Stop.class);
		Serializer serializer = newSerializer();

		serializer.serialize(stop);

		verify(stopSerializer).serialize(stop);
	}

	@Test
	public void closesOutput() throws Exception {
		newSerializer().close();

		verify(stationSerializer).close();
		verify(stopSerializer).close();
		verify(connectionSerializer).close();
		verify(journeySerializer).close();
	}

	@Test
	public void writesHeaders() throws Exception {
		newSerializer().writeHeaders();

		verify(stationSerializer).writeHeader();
		verify(stopSerializer).writeHeader();
		verify(connectionSerializer).writeHeader();
		verify(journeySerializer).writeHeader();
	}

	@Test
	public void serializesNeededInformationOfConnections() throws Exception {
		int id = 1;
		Connection connection = connection().withId(id).build();

		newSerializer().accept(connection);

		verify(connectionSerializer).accept(connection);
	}

	@Test
	public void serializesNeededInformationOfJourneys() throws Exception {
		Journey journey = mock(Journey.class);

		newSerializer().serialize(journey);

		verify(journeySerializer).serialize(journey);
	}

	private TimetableSerializer newSerializer() {
		return new TimetableSerializer(stationSerializer, stopSerializer, connectionSerializer,
				journeySerializer);
	}
}
