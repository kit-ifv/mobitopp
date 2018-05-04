package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Store;
import edu.kit.ifv.mobitopp.time.Time;

public class InMemoryTest {

	private Time time;
	private Store memory;
	private Profile toStop1;
	private Profile toStop2;
	private Stop stop1;
	private Stop stop2;
	
	@Before
	public void initialise() throws Exception {
		time = someTime();
		stop1 = someStop();
		stop2 = anotherStop();
		toStop1 = mock(Profile.class);
		toStop2 = mock(Profile.class);
		memory = new InMemory();
		when(toStop1.target()).thenReturn(stop1);
		when(toStop2.target()).thenReturn(stop2);
	}

	@Test
	public void storeAndRetrieveProfiles() {
		memory.save(toStop1);
		memory.save(toStop2);
		
		verify(stop1, toStop1);
		verify(stop2, toStop2);
	}

	private void verify(Stop stop, Profile profile) {
		Profile stop1FromStore = memory.profileTo(stop, time);
		assertThat(stop1FromStore, is(equalTo(profile)));
	}

	@Test(expected = ProfileMissing.class)
	public void retrievMissingProfile() throws Exception {
		Store memory = new InMemory();

		memory.profileTo(someStop(), someTime());
	}

}
