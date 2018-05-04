package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.Timetable;
import edu.kit.ifv.mobitopp.time.Time;

public class StoreInTest {

	private File output;
	private Timetable timetable;
	private Profile someProfile;
	private Profile somePart;
	private ProfileWriter writer;
	private ProfileReader reader;
	private StoreIn store;
	private EntrySplitter splitter;

	@Before
	public void initialise() throws Exception {
		splitter = mock(EntrySplitter.class);
		output = mock(File.class);
		timetable = mock(Timetable.class);
		someProfile = mock(Profile.class);
		somePart = mock(Profile.class);
		writer = mock(ProfileWriter.class);
		reader = mock(ProfileReader.class);

		when(someProfile.split(splitter)).thenReturn(asList(somePart));

		store = newStore();
	}

	@Test
	public void retrievesProfileToTarget() throws Exception {
		Profile readProfile = store.profileTo(someStop(), someTime());

		assertThat(readProfile, is(someProfile));
		verify(someProfile).loadFrom(reader);
	}

	@Test
	public void savesSplittedProfileToSeveralFiles() throws Exception {
		store.save(someProfile);

		verify(someProfile).split(splitter);
		verify(somePart).saveTo(writer);
	}

	private StoreIn newStore() {
		return new StoreIn(output, timetable, splitter) {

			@Override
			ProfileWriter to(Profile profile) {
				return writer;
			}

			@Override
			ProfileReader from(Stop target) {
				return reader;
			}

			@Override
			ProfileReader from(Stop target, Time time) {
				return reader;
			}

			@Override
			Profile newProfile(Stop target, Time time) {
				return someProfile;
			}

		};
	}
}
