package edu.kit.ifv.mobitopp.simulation.tour;


import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.logit.LogitModel;

public class DefaultWithinTourModeChoiceModel
	implements WithinTourModeChoiceModel
{
	
	private final WithinTourModeChoiceUtilityFunction utilityFunction;
	
	public DefaultWithinTourModeChoiceModel(WithinTourModeChoiceUtilityFunction utilityFunction) {
		this.utilityFunction = utilityFunction;
	}


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
		
		if (choiceSet.size() == 1) {
			return choiceSet.iterator().next();
		}
		
		assert !choiceSet.contains(StandardMode.CAR) : choiceSet;
		assert !choiceSet.contains(StandardMode.BIKE) : choiceSet;
		
	
		Map<Mode,Double> utilities 
			= utilityFunction.calculateUtilities(tour,tourMode,person,source,destination,previousActivity,nextActivity, choiceSet);
		
		LogitModel<Mode> logit = new DefaultLogitModel<Mode>();
		
		return logit.select(utilities, randomNumber);
	}

}
