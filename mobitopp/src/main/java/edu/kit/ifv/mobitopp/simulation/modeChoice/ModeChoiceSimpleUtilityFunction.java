package edu.kit.ifv.mobitopp.simulation.modeChoice;


import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ArrayList;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.util.logit.LinearUtilityFunction;

public class ModeChoiceSimpleUtilityFunction
	implements ModeChoiceUtilityFunction
{

	private final ModeChoiceParameterSimple parameter = new ModeChoiceParameterSimple();

	private final ImpedanceIfc impedance;

	private final Map<Mode,LinearUtilityFunction> modeUtilityFunctions;

	private final Collection<Mode> modes;

	public ModeChoiceSimpleUtilityFunction(ImpedanceIfc impedance) {

		this.impedance = impedance;

		this.modes = Collections.unmodifiableCollection(universalModeSet());

		this.modeUtilityFunctions = Collections.unmodifiableMap(makeUtilityFunctions(this.parameter));
	}

	private static Collection<Mode> universalModeSet() {

		Collection<Mode> tmp = new ArrayList<Mode>();
		tmp.add(StandardMode.PEDESTRIAN);
		tmp.add(StandardMode.BIKE);
		tmp.add(StandardMode.CAR);
		tmp.add(StandardMode.PASSENGER);
		tmp.add(StandardMode.PUBLICTRANSPORT);

		return Collections.unmodifiableCollection(tmp);
	}


	private Map<Mode,LinearUtilityFunction> makeUtilityFunctions(
		ModeChoiceParameterSimple parameter
	) {

		Map<Mode,LinearUtilityFunction> utilityFunctions = new LinkedHashMap<Mode,LinearUtilityFunction>();

		for (Mode mode : modes) {

			utilityFunctions.put(mode,	new LinearUtilityFunction(parameter.parameterForMode(mode)));
		}

		return utilityFunctions;
	}




	
	public Map<Mode,Double> calculateUtilities(
		Person person,
		Zone source,
		Zone dest,
		ActivityIfc prevActivity,
		ActivityIfc nextActivity,
		Collection<Mode> choiceSet
	) {

		Set<Mode> modes = new LinkedHashSet<>(choiceSet);

		Map<Mode,Double> utilities = new LinkedHashMap<Mode, Double>(); 

		Map<Mode,Map<String,Double>> attributes = this.parameter.gatherAttributes(
																						person, modes, source, dest, 
																						prevActivity, nextActivity,
																						impedance
																		);

		for (Mode mode : modes) {

			assert modeUtilityFunctions.containsKey(mode);

			LinearUtilityFunction utilityFunction = modeUtilityFunctions.get(mode);

			Double u = utilityFunction.calculateUtility(attributes.get(mode));

			utilities.put(mode, u);
		}

		return utilities;
	}


}
