package edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;

public interface ActivityStartAndDurationRandomizer {
	
	PatternActivity randomizeStartAndDuration(PatternActivity activity);
	
	default PatternActivityWeek randomizeStartAndDuration(PatternActivityWeek week) {
		
		List<PatternActivity> randomized = new ArrayList<PatternActivity>();
		
		List<PatternActivity> activities = week.getPatternActivities();
		
		if (!activities.isEmpty()) {
			randomized.add(activities.get(0));
		}
		
		for (int i=1; i<activities.size()-1; i++) {
			PatternActivity act = activities.get(i);
			randomized.add(randomizeStartAndDuration(act));
		}
		
		if (activities.size() >= 2) {
			randomized.add(activities.get(activities.size()-1));
		}
	
		return new PatternActivityWeek(randomized);
	}

}