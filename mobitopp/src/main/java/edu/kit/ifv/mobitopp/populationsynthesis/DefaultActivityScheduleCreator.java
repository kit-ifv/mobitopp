package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
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
		List<ActivityOfPanelData> activities = parseActivities(personOfPanelData.getActivityPattern());
		return PatternActivityWeek.fromActivityOfPanelData(activities);
	}

	public List<ActivityOfPanelData> parseActivities(String pattern) {
		assert pattern != null;
		StringTokenizer tokenizer = new StringTokenizer(pattern, ";");
		verifyAmountOfTokens(tokenizer);
		List<ActivityOfPanelData> activities = new ArrayList<ActivityOfPanelData>();
		while (tokenizer.hasMoreTokens()) {
			activities
					.add(new ActivityOfPanelData(
							Integer.parseInt(tokenizer.nextToken()),
							ActivityType.getTypeFromInt(Integer.parseInt(tokenizer.nextToken())),
							Integer.parseInt(tokenizer.nextToken()), 
							Integer.parseInt(tokenizer.nextToken())));
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
