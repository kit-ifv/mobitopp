package edu.kit.ifv.mobitopp.data;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class PatternActivityWeekTest {

	private static final int oneWeek = 7;
	private static final PatternActivity mondayActivity = mondayActivity();
	private static final PatternActivity mondayActivityNextWeek = mondayActivityNextWeek();
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
		activityWeek.addPatternActivity(mondayActivityNextWeek);
		activityWeek.addPatternActivity(firstTuesdayActivity);
		activityWeek.addPatternActivity(secondTuesdayActivity);
		return activityWeek;
	}

	private void assertPatterns(PatternActivityWeek patterns) {
		Time monday = Time.start;
		Time tuesday = monday.nextDay();
		List<PatternActivity> mondayActivities = patterns.getPatternActivities(monday);
		List<PatternActivity> tuesdayActivities = patterns.getPatternActivities(tuesday);

		assertThat(mondayActivities, contains(mondayActivity()));
		assertThat(tuesdayActivities, contains(firstTuesdayActivity, secondTuesdayActivity));
	}
	
	@Test
	public void activityPatternsPerDay() {
		PatternActivityWeek activityWeek = buildUpPatternActivityWeek();
		
		assertPatternsNextWeek(activityWeek);
	}
	
	private void assertPatternsNextWeek(PatternActivityWeek patterns) {
		Time monday = Time.start;
		Time mondayNextWeek = monday.plusDays(oneWeek);
		List<PatternActivity> mondayActivities = patterns.getPatternActivities(mondayNextWeek);
		
		assertThat(mondayActivities, contains(mondayActivityNextWeek()));
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
		Time startTime = Time.start.plusMinutes(3);
		return new PatternActivity(ActivityType.HOME, 2, startTime, 4);
	}
	
	private static PatternActivity mondayActivityNextWeek() {
		Time startTime = Time.start.plusDays(oneWeek).plusMinutes(3);
		return new PatternActivity(ActivityType.HOME, 2, startTime, 4);
	}

	private static PatternActivity tuesdayActivity(int startMinute) {
		Time startTime = Time.start.nextDay().plusMinutes(startMinute);
		return new PatternActivity(ActivityType.HOME, 2, startTime, 4);
	}
}
