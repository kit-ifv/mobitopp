package edu.kit.ifv.mobitopp.simulation.modeChoice;

import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.logit.LogitModel;

public class ModeChoiceSimple
	implements ModeChoiceModel 
{

	private final ModeChoiceSimpleUtilityFunction utilityFunction;

	private final LogitModel<Mode> logitModel = new DefaultLogitModel<Mode>();


	public ModeChoiceSimple(ImpedanceIfc impedance) {

		this.utilityFunction = new ModeChoiceSimpleUtilityFunction(impedance);
	}

	

	public Mode selectMode(
		Person person,
		Zone source,
		Zone destination,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Set<Mode> choiceSet,
		double randomNumber
	) {

		Map<Mode,Double> utilities = this.utilityFunction.calculateUtilities(
																														person, source, destination,
																														previousActivity, nextActivity
																														,choiceSet
																													);

		return logitModel.select(utilities, choiceSet, randomNumber);
	}

	
}
