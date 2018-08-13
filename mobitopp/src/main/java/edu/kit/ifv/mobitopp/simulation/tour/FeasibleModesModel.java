package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface FeasibleModesModel {

	Set<Mode> feasibleModes(ActivityIfc previousActivity, Tour tour, Set<Mode> choiceSet);

	Set<Mode> feasibleModes(ActivityIfc previousActivity, ActivityIfc otherActivity, int additionalActivityTime,
			Set<Mode> choiceSet);

}