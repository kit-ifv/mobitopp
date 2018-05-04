package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class ProfilesTest {

	private ProfileBuilder builder;
	private Profile createdByBuilder;
	private Store store;

	@Before
	public void initialise() throws Exception {
		builder = mock(ProfileBuilder.class);
		createdByBuilder = mock(Profile.class);
		store = mock(Store.class);
	}

	@Test
	public void buildsUpProfileToStop() throws Exception {
		returnCreatedProfile();

		Profiles profiles = newProfiles();
		profiles.buildTo(someStop());

		verify(store).save(createdByBuilder);
		verifyNoMoreInteractions(store);
	}

	@Test
	public void serializesProfile() throws Exception {
		returnCreatedProfile();
		Profiles profiles = newProfiles();

		profiles.buildTo(someStop());

		verify(store).save(createdByBuilder);
	}

	private Profiles newProfiles() {
		return new Profiles(builder, store);
	}

	private void returnCreatedProfile() {
		when(builder.buildUpTo(someStop())).thenReturn(createdByBuilder);
	}

}
