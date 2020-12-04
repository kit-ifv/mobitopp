package edu.kit.ifv.mobitopp.simulation.modeChoice;

import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface ModeChoiceParameter {

	public Map<Mode,Map<String,Double>> gatherAttributes(
		Person person,
		Set<Mode> modes,
		Zone source,
		Zone destination,
		ActivityIfc prevActivity,
		ActivityIfc nextActivity,
		ImpedanceIfc impedance
	);


	public Map<String,Double> parameterForMode(Mode mode);

}

