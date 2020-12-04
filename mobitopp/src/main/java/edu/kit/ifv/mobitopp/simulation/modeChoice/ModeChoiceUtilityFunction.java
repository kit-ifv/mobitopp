package edu.kit.ifv.mobitopp.simulation.modeChoice;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface ModeChoiceUtilityFunction {


	public Map<Mode,Double> calculateUtilities(
		Person person,
		Zone source, 
		Zone target,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Collection<Mode> choiceSet
	);
	

}

