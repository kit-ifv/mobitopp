package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class RandomModeChangeWithinTour implements WithinTourModeChoiceModel {

	@Override
	public Mode selectMode(
		Tour tour, 
		Mode tourMode, 
		Person person, 
		Zone source, 
		Zone destination,
		ActivityIfc previousActivity, 
		ActivityIfc nextActivity, 
		Set<Mode> choiceSet, 
		double randomNumber
	) {
		
		assert choiceSet.size() > 0;
		
		Map<Mode,Float> distribution = new LinkedHashMap<Mode,Float>();
		
		float probability = 1.0f / choiceSet.size();
		
		for (Mode mode : choiceSet) {
			distribution.put(mode, probability);
		}
		
		return new DiscreteRandomVariable<Mode>(distribution).realization(randomNumber);
	}

}
