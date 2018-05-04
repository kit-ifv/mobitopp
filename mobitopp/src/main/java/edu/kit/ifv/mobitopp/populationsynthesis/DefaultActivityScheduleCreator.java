package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.PatternActivity;

import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class DefaultActivityScheduleCreator 
	implements ActivityScheduleCreator
{

	private static final int DEFAULT_START_TIME = 0;

	private static final DayOfWeek DEFAULT_START_DAY = DayOfWeek.MONDAY;

	private Map<PersonOfPanelDataId,PatternActivityWeek> patternActivityWeeks
					= new HashMap<PersonOfPanelDataId,PatternActivityWeek>();

	private final static int MINUTES_PER_DAY = 1440;
	private final static int MINUTES_PER_WEEK = 10080;

	public DefaultActivityScheduleCreator() {
	}


	public PatternActivityWeek createActivitySchedule(
			PersonOfPanelData personOfPanelData,
			HouseholdOfPanelData householdOfPanelData,
			Household household
	) {
		assert personOfPanelData != null;

		PersonOfPanelDataId id = personOfPanelData.getId();

		if (!this.patternActivityWeeks.containsKey(id)) {

			PatternActivityWeek patternActivityWeek = instantiatePatternActivityWeek(personOfPanelData);

			this.patternActivityWeeks.put(id, patternActivityWeek);
		}

		return this.patternActivityWeeks.get(id);
	}

	private PatternActivityWeek instantiatePatternActivityWeek(
		PersonOfPanelData personOfPanelData
	) {

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
				starttime = startTimeOfActivity % MINUTES_PER_DAY;
	
			} else { // starttime = -1
	
				if (patternActivityWeek.getPatternActivities().isEmpty()) {

					weekDayType = DEFAULT_START_DAY;
					starttime = DEFAULT_START_TIME;

				} else {

					int tripDuration = activity.getObservedTripDuration() > -1 ?
															activity.getObservedTripDuration() : 15;

					int starttimeOfActivity = previousEndTime + tripDuration;

					weekDayType = determineWeekDayFromMinutesSinceSundayMidnight(starttimeOfActivity);
					starttime = starttimeOfActivity % MINUTES_PER_DAY;
				}
	
			}

			previousEndTime = starttime + activity.getDuration();
		
			PatternActivity patternActivity 
				= new PatternActivity(
						ActivityType.getTypeFromInt(activity.getActivityTypeAsInt()),
								weekDayType, 
								activity.getObservedTripDuration(),
								starttime, 
								activity.getDuration()
					);

			patternActivityWeek.addPatternActivity(patternActivity);
		}

		return patternActivityWeek;
	}

	private DayOfWeek determineWeekDayFromMinutesSinceSundayMidnight(int startTimeOfActivity_) {
		assert startTimeOfActivity_ >= 0;
		assert startTimeOfActivity_ <=  2*MINUTES_PER_WEEK;
		
		return DayOfWeek.fromDay((startTimeOfActivity_ % MINUTES_PER_WEEK) / MINUTES_PER_DAY);
	}


	// copied from PanelDataCsvReader
	public List<ActivityOfPanelData> parseActivities(String pattern_) {
		assert pattern_ != null;
		assert !pattern_.isEmpty();

		StringTokenizer tokenizer = new StringTokenizer(pattern_, ";");

		if ((tokenizer.countTokens() % 4) != 0) {
			throw new AssertionError("the amount elements of the activity pattern must dividable by 4");
		}

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
}

