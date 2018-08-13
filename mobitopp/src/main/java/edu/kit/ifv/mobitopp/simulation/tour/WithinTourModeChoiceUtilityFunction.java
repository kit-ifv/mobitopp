package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface WithinTourModeChoiceUtilityFunction {
	
	Map<Mode, Double> calculateUtilities(
		Tour tour, 
		Mode tourMode, 
		Person person, 
		Zone source, 
		Zone destination,
		ActivityIfc prevActivity, 
		ActivityIfc nextActivity, Set<Mode> choiceSet
	);

}
