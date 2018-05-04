package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;


public interface ActivityScheduleCreator {

	final static int MINUTES_PER_DAY = 1440;
	final static int MINUTES_PER_WEEK = 10080;


	public PatternActivityWeek createActivitySchedule(
			PersonOfPanelData personOfPanelData,
			HouseholdOfPanelData householdOfPanelData,
			Household household
	);

}
