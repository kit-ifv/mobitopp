package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public class FeasibleModesNoRestrictions implements FeasibleModesModel {
	
	public FeasibleModesNoRestrictions() { 
	}

	@Override
	public Set<Mode> feasibleModes(
		ActivityIfc previousActivity, 
		Tour tour, 
		Set<Mode> choiceSet
	) {
		assert !choiceSet.isEmpty();
	
		return choiceSet;
	}

	@Override
	public Set<Mode> feasibleModes(
		ActivityIfc previousActivity, 
		ActivityIfc otherActivity, 
		int additionalActivityTime, 
		Set<Mode> choiceSet
	) {
		assert !choiceSet.isEmpty();
		
		return choiceSet;
	}
}