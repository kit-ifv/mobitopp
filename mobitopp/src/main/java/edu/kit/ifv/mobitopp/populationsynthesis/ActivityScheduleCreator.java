package edu.kit.ifv.mobitopp.populationsynthesis;

import java.time.Duration;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public interface ActivityScheduleCreator {

	final static int MINUTES_PER_DAY = Math.toIntExact(Duration.ofDays(1).toMinutes());
	final static int MINUTES_PER_WEEK = PatternActivity.maximumDuration;

	public PatternActivityWeek createActivitySchedule(
			PersonOfPanelData personOfPanelData, HouseholdOfPanelData householdOfPanelData,
			Household household);

}
