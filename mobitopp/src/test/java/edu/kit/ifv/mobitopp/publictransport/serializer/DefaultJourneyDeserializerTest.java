package edu.kit.ifv.mobitopp.publictransport.serializer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;

public class DefaultJourneyDeserializerTest {

	private File journeyFile;
	private JourneyFormat journeyFormat;

	@Before
	public void initialiseMocks() throws Exception {
		journeyFormat = mock(JourneyFormat.class);
	}

	@Test
	public void deserializesJourneys() throws Exception {
		String serialized = "serialized";
		ModifiableJourney journey = mock(ModifiableJourney.class);
		when(journeyFormat.deserialize(serialized)).thenReturn(journey);

		ModifiableJourney deserialized = newSerializer().deserializeJourney(serialized);

		assertThat(deserialized, is(journey));

		verify(journeyFormat).deserialize(serialized);
	}

	private DefaultJourneyDeserializer newSerializer() {
		return new DefaultJourneyDeserializer(journeyFile, journeyFormat);
	}
}
