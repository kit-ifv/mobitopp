package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.TransportSystem;
import edu.kit.ifv.mobitopp.time.Time;

public class CsvJourneyFormatTest {

	private static final Time day = Data.someTime();
	private int id;
	private int capacity;
	private String code;
	private TransportSystem system;
	private ModifiableJourney journey;
	private JourneyFactory factory;

	@Before
	public void initialise() throws Exception {
		id = 1;
		capacity = 2;
		code = "coolio";
		system = new TransportSystem(code);
		journey = journey().withId(id).at(day).seatsFor(capacity).belongsTo(system).build();
		factory = mock(JourneyFactory.class);
	}

	@Test
	public void serializesNeededInformation() throws Exception {
		String serialized = serialize(journey);

		assertThat(serialized, is(equalTo(id + ";" + day.toSeconds() + ";" + capacity + ";" + code)));
	}

	@Test
	public void deserializesJourney() throws Exception {
		when(factory.createJourney(id, day, capacity, system)).thenReturn(journey);
		String serialized = serialize(journey);

		ModifiableJourney deserialized = new CsvJourneyFormat(factory).deserialize(serialized);

		assertThat(deserialized, is(equalTo(journey)));
	}

	private String serialize(Journey journey) {
		return new CsvJourneyFormat(factory).serialize(journey);
	}
}
