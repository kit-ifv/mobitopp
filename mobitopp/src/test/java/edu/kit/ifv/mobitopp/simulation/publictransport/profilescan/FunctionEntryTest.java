package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.fromOtherToAnother;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fromSomeToAnother;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.twoMinutesLater;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class FunctionEntryTest {

	private Connection notNeededConnection;
	private Stop start;

	@Before
	public void initialise() throws Exception {
		notNeededConnection = mock(Connection.class);
		start = mock(Stop.class);
	}

	@Test
	public void returnsANewInstanceAfterClearingChangeTime() throws Exception {
		FunctionEntry entry = entry(someTime(), oneMinuteLater());

		assertThat(entry.clearChangeTimeAt(start), is(not(sameInstance(entry))));
	}

	private FunctionEntry entry(Time departure, Time arrivalAtTarget) {
		return new FunctionEntry(departure, arrivalAtTarget, notNeededConnection);
	}

	@Test
	public void removesChangeTimeFromDeparture() throws Exception {
		when(start.addChangeTimeTo(someTime())).thenReturn(oneMinuteLater());
		FunctionEntry original = entry(someTime(), twoMinutesLater());

		FunctionEntry cleared = original.clearChangeTimeAt(start);

		assertThat(cleared.departure(), is(oneMinuteLater()));
		verify(start).addChangeTimeTo(someTime());
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		Connection someConnection = fromSomeToAnother();
		Connection anotherConnection = fromOtherToAnother();
		EqualsVerifier
				.forClass(FunctionEntry.class)
				.withPrefabValues(Time.class, someTime(), oneMinuteLater())
				.withPrefabValues(Connection.class, someConnection, anotherConnection)
				.withIgnoredFields("connection")
				.usingGetClass()
				.verify();
	}
}
