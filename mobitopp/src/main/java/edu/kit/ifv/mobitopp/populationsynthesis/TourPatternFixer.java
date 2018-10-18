package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.LinkedList;
import java.util.List;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.Time;

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
		activities.add(newHomeActivityAtStart());
		activities.addAll(patternActivityWeek.getPatternActivities());

		return new PatternActivityWeek(activities);
	}

	private boolean isAtHome(PatternActivity first) {
		return home.equals(first.getActivityType());
	}

	private PatternActivity newHomeActivityAtStart() {
		int tripDuration = 1;
		Time startTime = Time.start;
		int duration = 1;
		return new PatternActivity(home, tripDuration, startTime, duration);
	}

	private PatternActivity newHomeActivityAfter(PatternActivity last) {
		int tripDuration = last.getObservedTripDuration();
		Time startTime = last.startTime().plusMinutes(last.getDuration()).plusMinutes(tripDuration);
		int duration = 1;
		return new PatternActivity(home, tripDuration, startTime, duration);
	}
}
