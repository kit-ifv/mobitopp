package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class DefaultActivityScheduleCreator implements ActivityScheduleCreator {

	private static final ActivityType home = ActivityType.HOME;

	private final Map<PersonOfPanelDataId, PatternActivityWeek> patternActivityWeeks = new HashMap<>();

	public DefaultActivityScheduleCreator() {
		super();
	}

	public PatternActivityWeek createActivitySchedule(
			PersonOfPanelData personOfPanelData, HouseholdOfPanelData householdOfPanelData,
			Household household) {
		assert personOfPanelData != null;
		PersonOfPanelDataId id = personOfPanelData.getId();
		if (!this.patternActivityWeeks.containsKey(id)) {
			PatternActivityWeek patternActivityWeek = parsePatternActivityWeek(personOfPanelData);
			this.patternActivityWeeks.put(id, patternActivityWeek);
		}
		return this.patternActivityWeeks.get(id);
	}

	private PatternActivityWeek parsePatternActivityWeek(PersonOfPanelData personOfPanelData) {
		PatternActivityWeek patternActivityWeek = parsePatternOf(personOfPanelData);
		PatternActivityWeek firstIsAtHome = ensureFirstIsAtHome(patternActivityWeek);
		return ensureLastIsAtHome(firstIsAtHome);
	}

	private PatternActivityWeek parsePatternOf(PersonOfPanelData personOfPanelData) {
		return PatternActivityWeek
				.fromActivityOfPanelData(
						ActivityOfPanelData.parseActivities(personOfPanelData.getActivityPattern()));
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
