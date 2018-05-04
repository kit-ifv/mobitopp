package edu.kit.ifv.mobitopp.publictransport.serializer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;

public class DefaultJourneySerializerTest {

	private BufferedWriter journeyWriter;
	private JourneyFormat journeyFormat;

	@Before
	public void initialiseMocks() throws Exception {
		journeyWriter = mock(BufferedWriter.class);
		journeyFormat = mock(JourneyFormat.class);
	}

	@Test
	public void closesOutput() throws Exception {
		newSerializer().close();

		verify(journeyWriter).close();
	}

	@Test
	public void serializesNeededInformationOfJourneys() throws Exception {
		Journey journey = mock(Journey.class);
		String serialized = "serialized by foot";
		when(journeyFormat.serialize(journey)).thenReturn(serialized);

		newSerializer().serialize(journey);

		verify(journeyWriter).write(serialized);
		verify(journeyWriter).newLine();
		verify(journeyFormat).serialize(journey);
	}

	private DefaultJourneySerializer newSerializer() {
		return new DefaultJourneySerializer(journeyWriter, journeyFormat);
	}
}
