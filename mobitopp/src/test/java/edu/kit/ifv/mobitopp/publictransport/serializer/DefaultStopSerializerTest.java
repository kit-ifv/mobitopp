package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Neighbourhood;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class DefaultStopSerializerTest {

	private BufferedWriter stopWriter;
	private BufferedWriter footpathWriter;
	private StopFormat stopFormat;
	private TransferFormat transferFormat;

	@Before
	public void initialise() throws Exception {
		stopWriter = mock(BufferedWriter.class);
		footpathWriter = mock(BufferedWriter.class);
		stopFormat = mock(StopFormat.class);
		transferFormat = mock(TransferFormat.class);
	}

	@Test
	public void serializesNeededInformationOfStops() throws Exception {
		Stop stop = mock(Stop.class);
		when(stop.neighbours()).thenReturn(new Neighbourhood(stop));
		String serialized = "serialized by format";
		when(stopFormat.serialize(stop)).thenReturn(serialized);

		DefaultStopSerializer serializer = newSerializer();

		serializer.serialize(stop);

		verify(stopFormat).serialize(stop);
		verify(stopWriter).write(serialized);
		verify(stopWriter).newLine();
		verify(stop).neighbours();
	}

	@Test
	public void closesOutput() throws Exception {
		newSerializer().close();

		verify(stopWriter).close();
		verify(footpathWriter).close();
	}

	@Test
	public void serializesNeededInformationOfFootpaths() throws Exception {
		Stop someStop = stop().withId(1).withExternalId(1).build();
		Stop anotherStop = stop().withId(2).withExternalId(2).build();
		RelativeTime walkTime = RelativeTime.of(1, MINUTES);
		someStop.addNeighbour(anotherStop, walkTime);
		String serialized = "serialized by format";
		when(transferFormat.serialize(someStop, anotherStop, walkTime)).thenReturn(serialized);

		newSerializer().serialize(someStop);

		verify(footpathWriter).write(serialized);
		verify(footpathWriter).newLine();
		verify(transferFormat).serialize(someStop, anotherStop, walkTime);
	}

	private DefaultStopSerializer newSerializer() {
		return new DefaultStopSerializer(stopWriter, footpathWriter) {

			StopFormat stopFormat() {
				return stopFormat;
			}

			TransferFormat transferFormat() {
				return transferFormat;
			}

		};
	}
}
