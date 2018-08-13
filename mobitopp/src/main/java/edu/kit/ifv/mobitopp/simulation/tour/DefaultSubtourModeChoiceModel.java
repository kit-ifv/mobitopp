package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.logit.LogitModel;

public class DefaultSubtourModeChoiceModel 
	implements SubtourModeChoiceModel {
	
	private final SubtourModeChoiceUtilityFunction utilityFunction;
	

	public DefaultSubtourModeChoiceModel(SubtourModeChoiceUtilityFunction utilityFunction) {
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
		
	
		Map<Mode,Double> utilities 
			= utilityFunction.calculateUtilities(tour,tourMode,person,source,destination,previousActivity,nextActivity, choiceSet);
		
		LogitModel<Mode> logit = new DefaultLogitModel<Mode>();
		
		return logit.select(utilities, randomNumber);
	}
}
