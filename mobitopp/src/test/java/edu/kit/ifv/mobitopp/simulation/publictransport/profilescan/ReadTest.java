package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Read.entrySeparator;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.Timetable;
import edu.kit.ifv.mobitopp.time.SimpleTime;

public class ReadTest {

	private static final int departure = 0;
	private static final int arrival = 60;
	private static final ConnectionId connectionId = ConnectionId.of(1);
	private static final String serializedEntry = departure + entrySeparator + arrival
			+ entrySeparator + connectionId;
	private static final String serializedFunction = "1;" + serializedEntry;

	private BufferedReader fileReader;
	private Timetable timetable;
	private ArrivalTimeFunction someFunction;
	private Read read;

	@Before
	public void initialise() throws Exception {
		fileReader = mock(BufferedReader.class);
		timetable = mock(Timetable.class);
		someFunction = mock(ArrivalTimeFunction.class);
		read = newRead();
	}

	@Test
	public void hasNoNextElementWhenInputIsEmpty() throws Exception {
		assertThat(read.next(), is(false));
	}

	@Test
	public void resolvesIdToStop() throws Exception {
		int id = 1;
		when(fileReader.readLine()).thenReturn(serializedFunction);
		when(timetable.stopFor(id)).thenReturn(someStop());

		read.next();
		Stop stop = read.readStop();

		assertThat(stop, is(equalTo(someStop())));
		verify(timetable).stopFor(id);
		verify(fileReader).readLine();
	}

	@Test
	public void resolvesFunction() throws Exception {
		when(fileReader.readLine()).thenReturn(serializedFunction);

		read.next();
		read.readFunction();

		verify(fileReader).readLine();
		verify(someFunction).addDeserialized(any());
	}

	@Test
	public void parsesEntry() throws Exception {
		Connection connection = connection().withId(connectionId).build();
		when(timetable.connectionFor(connectionId)).thenReturn(connection);

		FunctionEntry entry = read.newEntry(serializedEntry, timetable);

		assertThat(entry.departure(), is(SimpleTime.ofSeconds(departure)));
		assertThat(entry.arrivalAtTarget(), is(SimpleTime.ofSeconds(arrival)));
		assertThat(entry.connection(), is(connection));

		verify(timetable).connectionFor(connectionId);
	}

	@Test
	public void goesToNextEntryWhenCalled() throws Exception {
		read.next();

		verify(fileReader).readLine();
	}

	@Test
	public void closesInputStream() throws Exception {
		read.close();

		verify(fileReader).close();
	}

	@Test
	public void hasNextEntryWhenStreamContainsLines() throws Exception {
		when(fileReader.readLine()).thenReturn(serializedFunction);

		boolean next = read.next();

		assertThat(next, is(true));
		verify(fileReader).readLine();
	}

	private Read newRead() {
		return new Read(fileReader, timetable) {

			@Override
			ArrivalTimeFunction newFunction() {
				return someFunction;
			}
		};
	}
}
