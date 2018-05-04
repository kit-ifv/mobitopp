package edu.kit.ifv.mobitopp.data;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import nl.jqno.equalsverifier.EqualsVerifier;

public class PatternActivityWeekTest {

	private static final PatternActivity mondayActivity = mondayActivity();
	private static final PatternActivity firstTuesdayActivity = tuesdayActivity(1);
	private static final PatternActivity secondTuesdayActivity = tuesdayActivity(2);

	@Test
	public void activityPatternsPerWeekday() {
		PatternActivityWeek activityWeek = buildUpPatternActivityWeek();

		assertPatterns(activityWeek);
	}

	private static PatternActivityWeek buildUpPatternActivityWeek() {
		PatternActivityWeek activityWeek = new PatternActivityWeek();
		activityWeek.addPatternActivity(mondayActivity);
		activityWeek.addPatternActivity(firstTuesdayActivity);
		activityWeek.addPatternActivity(secondTuesdayActivity);
		return activityWeek;
	}

	private void assertPatterns(PatternActivityWeek patterns) {
		List<PatternActivity> mondayActivities = patterns.getPatternActivities(DayOfWeek.MONDAY);
		List<PatternActivity> tuesdayActivities = patterns.getPatternActivities(DayOfWeek.TUESDAY);

		assertThat(mondayActivities, contains(mondayActivity()));
		assertThat(tuesdayActivities, contains(firstTuesdayActivity, secondTuesdayActivity));
	}

	@Test
	public void activityPatternsFromCompleteList() {
		List<PatternActivity> activities = asList(mondayActivity, firstTuesdayActivity,
				secondTuesdayActivity);
		PatternActivityWeek activityWeek = new PatternActivityWeek(activities);

		assertPatterns(activityWeek);
	}

	@Test
	public void existsPatternActivity() {
		PatternActivityWeek activityWeek = buildUpPatternActivityWeek();
		
		assertTrue(activityWeek.existsPatternActivity(ActivityType.HOME));
		assertFalse(activityWeek.existsPatternActivity(ActivityType.WORK));
	}
	
	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(PatternActivityWeek.class).usingGetClass().verify();
	}

	private static PatternActivity mondayActivity() {
		int starttime = 3;
		return newPatternActivity(DayOfWeek.MONDAY, starttime);
	}

	private static PatternActivity tuesdayActivity(int starttime) {
		return newPatternActivity(DayOfWeek.TUESDAY, starttime);
	}

	private static PatternActivity newPatternActivity(DayOfWeek dayOfWeek, int starttime) {
		return new PatternActivity(ActivityType.HOME, dayOfWeek, 2, starttime, 4);
	}
}
