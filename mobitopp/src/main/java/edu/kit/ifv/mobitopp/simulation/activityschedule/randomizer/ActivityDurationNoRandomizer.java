package edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;

public class ActivityDurationNoRandomizer 
	implements ActivityStartAndDurationRandomizer{

	@Override
	public PatternActivity randomizeStartAndDuration(PatternActivity activity) {
		return activity;
	}

	@Override
	public PatternActivityWeek randomizeStartAndDuration(PatternActivityWeek week) {
		return week;
	}

}
