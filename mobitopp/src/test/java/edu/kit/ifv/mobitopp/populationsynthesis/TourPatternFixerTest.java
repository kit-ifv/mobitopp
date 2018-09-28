package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class TourPatternFixerTest {

	private static final int tripDuration = 1;
	private static final int duration = 2;
	private static final int startTime = 3;

	@Test
	public void ensureFirstActivityIsAtHome() {
		PatternActivityWeek pattern = missingHomeAtStart();
		PatternActivityWeek activities = fixActivitiesFrom(pattern);

		assertThat(activities.first().getActivityType(), is(equalTo(ActivityType.HOME)));
	}

	private PatternActivityWeek missingHomeAtStart() {
		List<PatternActivity> activities = new LinkedList<>();
		activities.add(createActivityFor(ActivityType.WORK));
		activities.add(createActivityFor(ActivityType.HOME));
		return new PatternActivityWeek(activities);
	}

	@Test
	public void ensureLastActivityIsAtHome() {
		PatternActivityWeek pattern = missingHomeAtEnd();

		PatternActivityWeek activities = fixActivitiesFrom(pattern);

		assertThat(activities.last().getActivityType(), is(equalTo(ActivityType.HOME)));
	}

	private PatternActivityWeek missingHomeAtEnd() {
		List<PatternActivity> activities = new LinkedList<>();
		activities.add(createActivityFor(ActivityType.HOME));
		activities.add(createActivityFor(ActivityType.WORK));
		return new PatternActivityWeek(activities);
	}

	private PatternActivityWeek fixActivitiesFrom(PatternActivityWeek pattern) {
		PatternFixer fixer = new TourPatternFixer();
		return fixer.ensureIsTour(pattern);
	}

	private PatternActivity createActivityFor(ActivityType type) {
		return new PatternActivity(type, DayOfWeek.MONDAY, tripDuration, startTime, duration);
	}
}
