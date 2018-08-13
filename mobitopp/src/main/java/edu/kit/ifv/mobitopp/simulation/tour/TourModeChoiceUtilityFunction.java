package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface TourModeChoiceUtilityFunction {
	
	Map<Mode,Double> calculateUtilities(
		Tour tour,
		Person person,
		Map<Mode,Double> preferences,
		Set<Mode> choiceSet
	);

}
