package edu.kit.ifv.mobitopp.simulation.tour;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardChoiceSet;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.util.logit.LinearUtilityFunction;

public class DefaultWithinTourModeChoiceUtilityFunction
	implements WithinTourModeChoiceUtilityFunction
{

	private final WithinTourModeChoiceParameter parameter;

	private final ImpedanceIfc impedance;

	private final Map<Mode,LinearUtilityFunction> modeUtilityFunctions;

	
	protected DefaultWithinTourModeChoiceUtilityFunction(
		WithinTourModeChoiceParameter modeChoiceParameter, 
		ImpedanceIfc impedance,
		Map<Mode, LinearUtilityFunction> modeUtilityFunctions
	) {
		this.parameter = modeChoiceParameter;
		this.impedance = impedance;

		this.modeUtilityFunctions = Collections.unmodifiableMap(modeUtilityFunctions);
		
	}

	public DefaultWithinTourModeChoiceUtilityFunction(
		WithinTourModeChoiceParameter modeChoiceParameter, 
		ImpedanceIfc impedance
	) {
		this(
			modeChoiceParameter,
			impedance,
			makeUtilityFunctions(modeChoiceParameter, StandardChoiceSet.CHOICE_SET_FLEXIBLE)
		);
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



	@Override
	public Map<Mode, Double> calculateUtilities(
		Tour tour, 
		Mode tourMode, 
		Person person, 
		Zone source, 
		Zone destination,
		ActivityIfc prevActivity, 
		ActivityIfc nextActivity, 
		Set<Mode> choiceSet
	) {
	
		Set<Mode> modes = new LinkedHashSet<Mode>(choiceSet);

		Map<Mode,Double> utilities = new LinkedHashMap<Mode, Double>(); 

		Map<Mode,Map<String,Double>> attributes = this.parameter.gatherAttributes(
																								modes, tour, tourMode, person, source, destination,
																								prevActivity, nextActivity, impedance
																		);
		// System.out.println(attributes);

		for (Mode mode : modes) {

			assert modeUtilityFunctions.containsKey(mode);

			LinearUtilityFunction utilityFunction = modeUtilityFunctions.get(mode);

			Double u = utilityFunction.calculateUtility(attributes.get(mode));

			utilities.put(mode, u);
		}
		// System.out.println(utilities);

		return utilities;
	}




}
