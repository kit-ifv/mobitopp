package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;

public class PreferenceOnlyUtilityFunction 
	implements TourModeChoiceUtilityFunction
{

	@Override
	public Map<Mode, Double> calculateUtilities(
		Tour tour, 
		Person person, 
		Map<Mode, Double> preferences,
		Set<Mode> choiceSet
	) {
		
		LinkedHashMap<Mode,Double> utilities = new LinkedHashMap<Mode,Double>();
		
		choiceSet.forEach((mode) -> { 
			utilities.put(mode, 0.0);
		});
		
		utilities.putAll(preferences);
		
		return utilities;
	}

}
