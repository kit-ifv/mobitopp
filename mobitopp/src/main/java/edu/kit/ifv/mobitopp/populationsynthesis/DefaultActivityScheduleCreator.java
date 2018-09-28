package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class DefaultActivityScheduleCreator implements ActivityScheduleCreator {

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
		return PatternActivityWeek
				.fromActivityOfPanelData(
						ActivityOfPanelData.parseActivities(personOfPanelData.getActivityPattern()));
	}

}
