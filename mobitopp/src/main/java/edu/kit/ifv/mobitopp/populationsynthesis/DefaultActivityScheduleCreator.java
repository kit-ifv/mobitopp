package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.simulation.Household;

public class DefaultActivityScheduleCreator 
	implements ActivityScheduleCreator
{

	private Map<PersonOfPanelDataId,PatternActivityWeek> patternActivityWeeks
					= new HashMap<PersonOfPanelDataId,PatternActivityWeek>();

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

			PatternActivityWeek patternActivityWeek = PatternActivityWeek.fromActivityOfPanelData(
					ActivityOfPanelData.parseActivities(personOfPanelData.getActivityPattern())
			);

			this.patternActivityWeeks.put(id, patternActivityWeek);
		}

		return this.patternActivityWeeks.get(id);
	}
}

