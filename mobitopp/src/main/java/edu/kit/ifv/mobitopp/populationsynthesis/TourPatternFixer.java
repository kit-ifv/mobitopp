package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.LinkedList;
import java.util.List;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class TourPatternFixer implements PatternFixer {

	private static final ActivityType home = ActivityType.HOME;

	@Override
	public PatternActivityWeek ensureIsTour(PatternActivityWeek patternActivityWeek) {
		PatternActivityWeek firstIsAtHome = ensureFirstIsAtHome(patternActivityWeek);
		return ensureLastIsAtHome(firstIsAtHome);
	}

	private PatternActivityWeek ensureLastIsAtHome(PatternActivityWeek patternActivityWeek) {
		PatternActivity last = patternActivityWeek.last();
		if (isAtHome(last)) {
			return patternActivityWeek;
		}
		List<PatternActivity> activities = new LinkedList<>();
		activities.addAll(patternActivityWeek.getPatternActivities());
		activities.add(newHomeActivityAfter(last));
		return new PatternActivityWeek(activities);
	}

	private PatternActivityWeek ensureFirstIsAtHome(PatternActivityWeek patternActivityWeek) {
		PatternActivity first = patternActivityWeek.first();
		if (isAtHome(first)) {
			return patternActivityWeek;
		}
		List<PatternActivity> activities = new LinkedList<>();
		activities.add(newHomeActivityBefore(first));
		activities.addAll(patternActivityWeek.getPatternActivities());

		return new PatternActivityWeek(activities);
	}

	private boolean isAtHome(PatternActivity first) {
		return home.equals(first.getActivityType());
	}

	private PatternActivity newHomeActivityBefore(PatternActivity first) {
		DayOfWeek weekDayType = first.getWeekDayType();
		int tripDuration = 1;
		int startTime = 0;
		int duration = 1;
		return new PatternActivity(home, weekDayType, tripDuration, startTime, duration);
	}

	private PatternActivity newHomeActivityAfter(PatternActivity last) {
		DayOfWeek weekDayType = last.getWeekDayType();
		int tripDuration = last.getObservedTripDuration();
		int startTime = last.getStarttime() + last.getDuration() + tripDuration;
		int duration = 1;
		return new PatternActivity(home, weekDayType, tripDuration, startTime, duration);
	}
}
