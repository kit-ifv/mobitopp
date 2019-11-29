package edu.kit.ifv.mobitopp.simulation.tour;


import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardChoiceSet;
import edu.kit.ifv.mobitopp.util.logit.LinearUtilityFunction;

public class DefaultTourModeChoiceUtilityFunction
	implements TourModeChoiceUtilityFunction
{

	private final TourModeChoiceParameter parameter;

	private final ImpedanceIfc impedance;

	private final Map<Mode,LinearUtilityFunction> modeUtilityFunctions;

	private final Collection<Mode> modes;

	public DefaultTourModeChoiceUtilityFunction(
		TourModeChoiceParameter modeChoiceParameter, 
		ImpedanceIfc impedance
	) {

		this.parameter = modeChoiceParameter;
		this.impedance = impedance;

		this.modes = Collections.unmodifiableCollection(StandardChoiceSet.CHOICE_SET_FULL);

		this.modeUtilityFunctions = Collections.unmodifiableMap(makeUtilityFunctions(this.parameter));
	}


	
	private Map<Mode,LinearUtilityFunction> makeUtilityFunctions(
		TourModeChoiceParameter parameter
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
		Person person, 
		Map<Mode, Double> preferences,
		Set<Mode> choiceSet
	) {

		Set<Mode> modes = new LinkedHashSet<Mode>(choiceSet);

		Map<Mode,Double> utilities = new LinkedHashMap<Mode, Double>(); 

		Map<Mode,Map<String,Double>> attributes = this.parameter.gatherAttributes(
																						modes, tour, person, preferences, impedance
																		);
		
		assert attributes != null : modes;

		for (Mode mode : modes) {

			assert modeUtilityFunctions.containsKey(mode);
			assert attributes.containsKey(mode);

			LinearUtilityFunction utilityFunction = modeUtilityFunctions.get(mode);

			Double u = utilityFunction.calculateUtility(attributes.get(mode));

			utilities.put(mode, u);
		}
		// System.out.println(utilities);

		return utilities;
	}




}
