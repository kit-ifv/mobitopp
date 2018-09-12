package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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

	private static final DayOfWeek DEFAULT_START_DAY = DayOfWeek.MONDAY;
	private static final int DEFAULT_START_TIME = 0;
	
	private final Map<PersonOfPanelDataId, PatternActivityWeek> patternActivityWeeks;
	private final PatternFixer fixer;

	public DefaultActivityScheduleCreator(PatternFixer fixer) {
		super();
		this.fixer = fixer;
		patternActivityWeeks = new HashMap<>();
	}

	public DefaultActivityScheduleCreator() {
		this(new TourPatternFixer());
	}


	@Override
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
		return fixer.ensureIsTour(patternActivityWeek);
	}

	private PatternActivityWeek parsePatternOf(PersonOfPanelData personOfPanelData) {
		PatternActivityWeek patternActivityWeek = new PatternActivityWeek();
		
		if (personOfPanelData.getActivityPattern().isEmpty()) {
		
			return PatternActivityWeek.WHOLE_WEEK_AT_HOME;
		}

		List<ActivityOfPanelData> activityOfPanelData = parseActivities(personOfPanelData.getActivityPattern());

		int previousEndTime = 0;

		for (ActivityOfPanelData activity : activityOfPanelData) {

			DayOfWeek weekDayType = null;
			int starttime = -1;
	
			if (activity.getStarttime() != -1) {

				int startTimeOfActivity = activity.getStarttime();
				weekDayType = determineWeekDayFromMinutesSinceSundayMidnight(startTimeOfActivity);
				starttime = startTimeOfActivity;
	
			} else { // starttime = -1
	
				if (patternActivityWeek.getPatternActivities().isEmpty()) {

					weekDayType = DEFAULT_START_DAY;
					starttime = DEFAULT_START_TIME;

				} else {

					int tripDuration = activity.getObservedTripDuration() > -1 ?
															activity.getObservedTripDuration() : 15;

					int starttimeOfActivity = previousEndTime + tripDuration;

					weekDayType = determineWeekDayFromMinutesSinceSundayMidnight(starttimeOfActivity);
					starttime = starttimeOfActivity;
				}
	
			}

			previousEndTime = starttime + activity.getDuration();
			int relativeStartTime = starttime % MINUTES_PER_DAY;
		
			PatternActivity patternActivity 
				= new PatternActivity(
						ActivityType.getTypeFromInt(activity.getActivityTypeAsInt()),
								weekDayType, 
								activity.getObservedTripDuration(),
								relativeStartTime, 
								activity.getDuration()
					);

			patternActivityWeek.addPatternActivity(patternActivity);
		}

		return patternActivityWeek;
	}

	private DayOfWeek determineWeekDayFromMinutesSinceSundayMidnight(int startTimeOfActivity) {
		assert startTimeOfActivity >= 0;
		assert startTimeOfActivity <= 2 * MINUTES_PER_WEEK;

		return DayOfWeek.fromDay((startTimeOfActivity % MINUTES_PER_WEEK) / MINUTES_PER_DAY);
	}

	public List<ActivityOfPanelData> parseActivities(String pattern) {
		assert pattern != null;
		assert !pattern.isEmpty();

		StringTokenizer tokenizer = new StringTokenizer(pattern, ";");

		verifyAmountOfTokens(tokenizer);

		List<ActivityOfPanelData> activities = new ArrayList<ActivityOfPanelData>();

		while (tokenizer.hasMoreTokens()) {
			activities.add(
						new ActivityOfPanelData(
								Integer.parseInt(tokenizer.nextToken()), 
								ActivityType.getTypeFromInt(Integer.parseInt(tokenizer.nextToken())), 
								Integer.parseInt(tokenizer.nextToken()), 
								Integer.parseInt(tokenizer.nextToken())
						)
			);
		}

		return activities;
	}

	private void verifyAmountOfTokens(StringTokenizer tokenizer) {
		if ((tokenizer.countTokens() % 4) != 0) {
			throw new IllegalArgumentException(
					"The amount of elements of the activity pattern must be dividable by 4");
		}
	}

}
