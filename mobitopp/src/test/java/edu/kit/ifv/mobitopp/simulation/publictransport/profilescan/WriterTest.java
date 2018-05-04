package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Writer.endOfEntry;
import static edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Writer.entrySeparator;
import static edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Writer.intermediate;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.Time;

public class WriterTest {

	private BufferedWriter bufferedWriter;
	private Writer writer;
	private InOrder inOrder;
	private ArrivalTimeFunction function;

	@Before
	public void initialise() throws Exception {
		bufferedWriter = mock(BufferedWriter.class);
		function = mock(ArrivalTimeFunction.class);
		inOrder = inOrder(bufferedWriter, function);
		writer = new Writer(bufferedWriter);
	}

	@Test
	public void writesFunction() throws Exception {
		writer.write(someStop(), function);

		String id = String.valueOf(someStop().id());
		inOrder.verify(bufferedWriter).write(id);
		inOrder.verify(bufferedWriter).write(intermediate);
		inOrder.verify(function).forEach(writer);
		inOrder.verify(bufferedWriter).newLine();
	}

	@Test
	public void writesEntry() throws Exception {
		int id = 1;
		Time departure = someTime();
		Time arrival = oneMinuteLater();
		Connection connection = connection().withId(id).build();
		FunctionEntry entry = mock(FunctionEntry.class);
		when(entry.departure()).thenReturn(departure);
		when(entry.arrivalAtTarget()).thenReturn(arrival);
		when(entry.connection()).thenReturn(connection);

		writer.write(entry);

		inOrder.verify(bufferedWriter).write(String.valueOf(departure.toSeconds()));
		inOrder.verify(bufferedWriter).write(entrySeparator);
		inOrder.verify(bufferedWriter).write(String.valueOf(arrival.toSeconds()));
		inOrder.verify(bufferedWriter).write(entrySeparator);
		inOrder.verify(bufferedWriter).write(String.valueOf(id));
		inOrder.verify(bufferedWriter).write(endOfEntry);
	}

}
