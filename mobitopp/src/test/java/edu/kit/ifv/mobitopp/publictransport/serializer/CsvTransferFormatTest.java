package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class CsvTransferFormatTest {

	private StopResolver stopResolver;

	@Before
	public void initialise() throws Exception {
		stopResolver = mock(StopResolver.class);
	}
	
	@Test
	public void serializesNeededInformationOfTransfer() throws Exception {
		int stopId = 0;
		int neighbourId = 1;
		int walkTimeInSeconds = 60;
		Stop stop = stop().withId(stopId).build();
		Stop neighbour = stop().withId(neighbourId).build();
		RelativeTime walkTime = RelativeTime.of(walkTimeInSeconds, SECONDS);

		String serialized = serialize(stop, neighbour, walkTime);
		
		assertThat(serialized, is(equalTo(stopId + ";" + neighbourId + ";" + walkTimeInSeconds)));
	}
	
	@Test
	public void deserializesTransfer() throws Exception {
		Stop stop = someStop();
		Stop neighbour = anotherStop();
		RelativeTime walkTime = RelativeTime.of(60, SECONDS);
		String serialized = serialize(stop, neighbour, walkTime);
		resolve(stop);
		resolve(neighbour);
		
		StopTransfer deserialized = deserialize(serialized);
		
		assertThat(deserialized, is(transferBetween(stop, neighbour, walkTime)));
		
		verify(stopResolver).resolve(stop.id());
		verify(stopResolver).resolve(neighbour.id());
	}

	private StopTransfer deserialize(String serialized) {
		return new CsvTransferFormat().deserialize(serialized, stopResolver);
	}

	private void resolve(Stop stop) {
		when(stopResolver.resolve(stop.id())).thenReturn(stop);
	}

	private StopTransfer transferBetween(Stop stop, Stop neighbour, RelativeTime walkTime) {
		return new StopTransfer(stop, neighbour, walkTime);
	}

	private static String serialize(Stop stop, Stop neighbour, RelativeTime walkTime) {
		return new CsvTransferFormat().serialize(stop, neighbour, walkTime);
	}
}
