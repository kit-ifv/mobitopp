package edu.kit.ifv.mobitopp.simulation.tour;


import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.logit.LogitModel;

public class DefaultTourModeChoiceModel
	implements TourOnlyModeChoiceModel
{
	
	private final TourModeChoiceUtilityFunction utilityFunction;
	
	public DefaultTourModeChoiceModel(TourModeChoiceUtilityFunction utilityFunction) {
		this.utilityFunction = utilityFunction;
	}

	@Override
	public Mode selectMode(
		Tour tour, 
		Person person, 
		Map<Mode, Double> preferences, 
		Set<Mode> choiceSet,
		double randomNumber
	) {
		
	
		Map<Mode,Double> utilities 
			= utilityFunction.calculateUtilities(tour,person,preferences,choiceSet);
		
//		System.out.println(choiceSet);
//		System.out.println(utilities);
		
		LogitModel<Mode> logit = new DefaultLogitModel<Mode>();
		
		Mode selectedMode =  logit.select(utilities, randomNumber);
	
		return selectedMode;
	}

}
