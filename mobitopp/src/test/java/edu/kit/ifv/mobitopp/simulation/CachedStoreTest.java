package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Store;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class CachedStoreTest {

	private Store store;
	private Profile profile;
	private Profile anotherProfile;
	private Stop stop;
	private Stop anotherStop;
	private Time time;
	private Time inSameHour;
	private CachedStore cache;

	@Before
	public void initialise() throws Exception {
		store = mock(Store.class);
		profile = mock(Profile.class);
		anotherProfile = mock(Profile.class);
		stop = someStop();
		anotherStop = anotherStop();
		time = someTime();
		inSameHour = time.plus(RelativeTime.of(1, MINUTES));

		cache = new CachedStore(store);
	}

	@Test
	public void savesUsingGivenStore() throws Exception {
		cache.save(profile);

		verify(store).save(profile);
	}

	@Test
	public void loadsFromGivenStore() throws Exception {
		loadSomeProfile();

		Profile loaded = cache.profileTo(stop, time);

		assertThat(loaded, is(equalTo(profile)));
		verify(store).profileTo(stop, time);
	}

	@Test
	public void cachesSingleProfile() throws Exception {
		loadSomeProfile();
		Profile loaded = cache.profileTo(stop, time);
		Profile fromCache = cache.profileTo(stop, time);

		assertThat(fromCache, is(equalTo(loaded)));

		verify(store).profileTo(stop, time);
	}
	
	@Test
	public void cacheProfilesPerHour() throws Exception {
		Profile loaded = cache.profileTo(stop, time);
		Profile fromCache = cache.profileTo(stop, inSameHour);

		assertThat(fromCache, is(equalTo(loaded)));

		verify(store).profileTo(stop, time);
	}

	@Test
	public void cachesSeveralProfiles() throws Exception {
		loadSomeProfile();
		loadAnotherProfile();
		Profile loadedStop = cache.profileTo(stop, time);
		Profile loadedAnother = cache.profileTo(anotherStop, time);
		Profile stopFromCache = cache.profileTo(stop, time);
		Profile anotherFromCache = cache.profileTo(anotherStop, time);

		assertThat(anotherFromCache, is(equalTo(loadedAnother)));
		assertThat(stopFromCache, is(equalTo(loadedStop)));

		verify(store).profileTo(stop, time);
		verify(store).profileTo(anotherStop, time);
	}
	
	@Test
	public void realodFromGivenStoreAfterCacheHasBeenCleaned() throws Exception {
		loadSomeProfile();
		
		cache.profileTo(stop, time);
		cache.cleanBefore(oneHourLater());
		cache.profileTo(stop, time);
		
		verify(store, times(2)).profileTo(stop, someTime());
	}

	private Time oneHourLater() {
		return someTime().plus(RelativeTime.of(1, HOURS));
	}

	@Test
	public void cleanOnlyOlderEntries() throws Exception {
		loadSomeProfile();
		
		cache.profileTo(stop, time);
		cache.cleanBefore(time);
		cache.profileTo(stop, time);
		
		verify(store).profileTo(stop, time);
	}

	private void loadSomeProfile() {
		when(store.profileTo(stop, time)).thenReturn(profile);
	}

	private void loadAnotherProfile() {
		when(store.profileTo(anotherStop, time)).thenReturn(anotherProfile);
	}
}
