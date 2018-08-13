package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class WithinTourModeChangeBasedOnTourMode implements WithinTourModeChoiceModel {

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
		
		Map<Mode,Double> utilities = calculateUtilities(tourMode, person, source, destination, nextActivity, choiceSet);
		
		return new DiscreteRandomVariable<Mode>(utilities).realization(randomNumber);
	}

	private Map<Mode, Double> calculateUtilities(
		Mode tourMode, 
		Person person, 
		Zone source, 
		Zone destination,
		ActivityIfc nextActivity, 
		Set<Mode> choiceSet
	) {
		// TODO Auto-generated method stub
		return null;
	}

}
