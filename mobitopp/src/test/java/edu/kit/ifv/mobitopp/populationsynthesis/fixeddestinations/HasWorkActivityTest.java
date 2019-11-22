package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

@ExtendWith(MockitoExtension.class)
public class HasWorkActivityTest {

	@Mock
	private PersonBuilder person;

	@Test
	void containsNoWorkActivityInWeek() throws Exception {
		configureStayAtHome();

		HasWorkActivity predicate = new HasWorkActivity();

		assertFalse(predicate.test(person));
		verify(person).getActivityPattern();
	}

	private void configureStayAtHome() {
		when(person.getActivityPattern()).thenReturn(ExampleSetup.activitySchedule(ActivityType.HOME));
	}

	@Test
	void containsWorkActivityInWeek() throws Exception {
		configureAtWork();

		HasWorkActivity predicate = new HasWorkActivity();

		assertTrue(predicate.test(person));
		verify(person).getActivityPattern();
	}

	private void configureAtWork() {
		when(person.getActivityPattern())
				.thenReturn(
						ExampleSetup.activitySchedule(ActivityType.HOME, ActivityType.WORK, ActivityType.HOME));
	}
}
