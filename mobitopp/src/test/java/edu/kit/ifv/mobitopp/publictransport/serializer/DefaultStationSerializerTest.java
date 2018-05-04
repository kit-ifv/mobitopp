package edu.kit.ifv.mobitopp.publictransport.serializer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Station;

public class DefaultStationSerializerTest {

	private StationFormat stationFormat;
	private Station station;
	private BufferedWriter stationWriter;
	private String serialized;

	@Before
	public void initialise() throws Exception {
		station = mock(Station.class);
		stationFormat = mock(StationFormat.class);
		stationWriter = mock(BufferedWriter.class);
		serialized = "serialized";
		when(stationFormat.serialize(station)).thenReturn(serialized);
	}
	
	@Test
	public void writeStation() throws Exception {
		serializer().serialize(station);
		
		verify(stationWriter).write(serialized);
		verify(stationWriter).newLine();
		verify(stationFormat).serialize(station);
	}
	
	@Test
	public void closesOutput() throws Exception {
		serializer().close();
		
		verify(stationWriter).close();
	}

	private DefaultStationSerializer serializer() {
		return new DefaultStationSerializer(stationWriter) {
			StationFormat stationFormat() {
				return stationFormat;
			}
		};
	}
}
