package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.List;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ExamplePattern {

	private static final int tripDuration = 30;
	private final Time monday;
	private final Time tuesday;
	private final Time wednesday;
	private final Time thursday;
	private final Time friday;
	private final Time saturday;
	private final Time sunday;

	public ExamplePattern() {
		monday = Time.start;
		tuesday = monday.nextDay();
		wednesday = tuesday.nextDay();
		thursday = wednesday.nextDay();
		friday = thursday.nextDay();
		saturday = friday.nextDay();
		sunday = saturday.nextDay();
	}

	public PatternActivityWeek startingAtHome() {
		PatternActivityWeek pattern = new PatternActivityWeek();
		pattern.addPatternActivity(makeActivity(ActivityType.HOME, monday, 0, 6));
		completeProgramOf(pattern);
		return pattern;
	}

	public PatternActivityWeek startingAtWork() {
		PatternActivityWeek pattern = new PatternActivityWeek();
		completeProgramOf(pattern);
		return pattern;
	}

	private void completeProgramOf(PatternActivityWeek pattern) {
		pattern.addPatternActivity(makeActivity(ActivityType.WORK, monday, 7, 8));
		pattern.addPatternActivity(makeActivity(ActivityType.HOME, monday, 15, 15));
		pattern.addPatternActivity(makeActivity(ActivityType.WORK, tuesday, 7, 8));
		pattern.addPatternActivity(makeActivity(ActivityType.HOME, tuesday, 15, 15));
		pattern.addPatternActivity(makeActivity(ActivityType.WORK, wednesday, 7, 8));
		pattern.addPatternActivity(makeActivity(ActivityType.HOME, wednesday, 15, 15));
		pattern.addPatternActivity(makeActivity(ActivityType.WORK, thursday, 7, 8));
		pattern.addPatternActivity(makeActivity(ActivityType.HOME, thursday, 15, 15));
		pattern.addPatternActivity(makeActivity(ActivityType.WORK, friday, 7, 8));
		pattern.addPatternActivity(makeActivity(ActivityType.HOME, friday, 15, 15));
		pattern.addPatternActivity(makeActivity(ActivityType.SHOPPING, saturday, 10, 3));
		pattern.addPatternActivity(makeActivity(ActivityType.HOME, saturday, 13, 15));
		pattern.addPatternActivity(makeActivity(ActivityType.LEISURE, sunday, 14, 3));
		pattern.addPatternActivity(makeActivity(ActivityType.HOME, sunday, 17, 12));
	}

	private PatternActivity makeActivity(
			ActivityType actType, Time day, int hourOfDay, int durationInHours) {
		return new PatternActivity(actType, tripDuration, day.plusHours(hourOfDay), durationInHours * 60);
	}

	public List<Time> dates() {
		return SimpleTime.oneWeek();
	}

}
