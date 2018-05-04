package edu.kit.ifv.mobitopp.publictransport.serializer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Station;

public class DefaultStationDeserializerTest {

	private File stationInput;
	private StationFormat stationFormat;
	private NodeResolver nodeResolver;
	
	@Before
	public void initialise() throws Exception {
		stationFormat = mock(StationFormat.class);
		nodeResolver = mock(NodeResolver.class);
	}

	@Test
	public void deserializesStation() throws Exception {
		Station station = mock(Station.class);
		String serialized = "serialized";
		when(stationFormat.deserialize(serialized, nodeResolver)).thenReturn(station);
		
		Station deserialized = deserializer().deserializeStation(serialized, nodeResolver );
		
		assertThat(deserialized, is(equalTo(station)));
		
		verify(stationFormat).deserialize(serialized, nodeResolver);
	}

	private DefaultStationDeserializer deserializer() {
		return new DefaultStationDeserializer(stationInput) {
			@Override
			StationFormat stationFormat() {
				return stationFormat;
			}
		};
	}
}
