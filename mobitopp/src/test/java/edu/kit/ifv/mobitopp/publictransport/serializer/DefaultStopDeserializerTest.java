package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class DefaultStopDeserializerTest {

	private File stopInput;
	private File footpathInput;
	private StopFormat stopFormat;
	private TransferFormat transferFormat;
	private StopResolver stopResolver;
	private StationResolver stationResolver;

	@Before
	public void initialise() throws Exception {
		stopFormat = mock(StopFormat.class);
		transferFormat = mock(TransferFormat.class);
		stopResolver = mock(StopResolver.class);
		stationResolver = mock(StationResolver.class);
	}

	@Test
	public void deserializesStop() throws Exception {
		Stop stop = someStop();
		String serialized = stop.name();
		when(stopFormat.deserialize(serialized, stationResolver)).thenReturn(stop);

		Stop deserialized = newSerializer().deserializeStop(serialized, stationResolver);

		assertThat(deserialized, is(equalTo(stop)));

		verify(stopFormat).deserialize(serialized, stationResolver);
	}

	@Test
	public void deserializesTransfers() throws Exception {
		Stop stop = someStop();
		Stop neighbour = anotherStop();
		RelativeTime walkTime = RelativeTime.of(1, MINUTES);
		String serialized = "serialized";
		StopTransfer transferBetweenStops = transferBetween(stop, neighbour, walkTime);
		when(transferFormat.deserialize(serialized, stopResolver)).thenReturn(transferBetweenStops);

		StopTransfer deserialized = newSerializer().deserializeTransfer(serialized, stopResolver);

		assertThat(deserialized, is(equalTo(transferBetweenStops)));

		verify(transferFormat).deserialize(serialized, stopResolver);
	}

	private StopTransfer transferBetween(Stop stop, Stop neighbour, RelativeTime walkTime) {
		return new StopTransfer(stop, neighbour, walkTime);
	}

	private DefaultStopDeserializer newSerializer() {
		return new DefaultStopDeserializer(stopInput, footpathInput) {

			StopFormat stopFormat() {
				return stopFormat;
			}

			TransferFormat transferFormat() {
				return transferFormat;
			}

		};
	}
}
