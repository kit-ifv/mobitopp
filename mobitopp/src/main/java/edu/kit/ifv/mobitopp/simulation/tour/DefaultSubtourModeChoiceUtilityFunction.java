package edu.kit.ifv.mobitopp.simulation.tour;


import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.util.logit.LinearUtilityFunction;

public class DefaultSubtourModeChoiceUtilityFunction
		extends DefaultWithinTourModeChoiceUtilityFunction
	implements SubtourModeChoiceUtilityFunction
{

	/*
	private final WithinTourModeChoiceParameter parameter;

	private final ImpedanceIfc impedance;

	private final Map<Mode,LinearUtilityFunction> modeUtilityFunctions;
	*/

	
	public DefaultSubtourModeChoiceUtilityFunction(
		WithinTourModeChoiceParameter modeChoiceParameter, 
		ImpedanceIfc impedance
	) {
		super(
			modeChoiceParameter,
			impedance,
			makeUtilityFunctions(modeChoiceParameter, Mode.CHOICE_SET_FULL)
		);
	}

	private static Set<Mode> modeChoiceSetForWithinTourModeChoice() {

		Set<Mode> tmp = new LinkedHashSet<Mode>();
		tmp.add(Mode.PEDESTRIAN);
		tmp.add(Mode.BIKE);
		tmp.add(Mode.CAR);
		tmp.add(Mode.PASSENGER);
		tmp.add(Mode.PUBLICTRANSPORT);

		return Collections.unmodifiableSet(tmp);
	}


	private static Map<Mode,LinearUtilityFunction> makeUtilityFunctions(
		WithinTourModeChoiceParameter parameter,
		Set<Mode> modes
	) {

		Map<Mode,LinearUtilityFunction> utilityFunctions = new LinkedHashMap<Mode,LinearUtilityFunction>();

		for (Mode mode : modes) {

			utilityFunctions.put(mode,	new LinearUtilityFunction(parameter.parameterForMode(mode)));
		}

		return utilityFunctions;
	}







}
