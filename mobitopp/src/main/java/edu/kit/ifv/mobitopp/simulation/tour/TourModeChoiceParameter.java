package edu.kit.ifv.mobitopp.simulation.tour;


import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface TourModeChoiceParameter {

	public Map<Mode,Map<String,Double>> gatherAttributes(
		Set<Mode> modes,
		Tour tour, 
		Person person, 
		Map<Mode, Double> preferences, 
		ImpedanceIfc impedance
	);


	public Map<String,Double> parameterForMode(Mode mode);

}

