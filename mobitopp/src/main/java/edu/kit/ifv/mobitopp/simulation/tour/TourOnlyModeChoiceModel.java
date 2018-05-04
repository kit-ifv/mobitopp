package edu.kit.ifv.mobitopp.simulation.tour;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface TourOnlyModeChoiceModel {

	public Mode selectMode(
		Tour tour,
		Person person,
		Map<Mode,Double> preferences,
		Set<Mode> choiceSet,
		double randomNumber
	);
	
	default Mode selectMode(
			Tour tour,
			Person person,
			Set<Mode> choiceSet,
			double randomNumber
	) {
		Map<Mode,Double> preferences = new LinkedHashMap<Mode,Double>();
		
		choiceSet.forEach((mode) -> {
				preferences.put(mode, 0.0);
		});
			
		return selectMode(tour, person, preferences, choiceSet, randomNumber);	
	}
}
