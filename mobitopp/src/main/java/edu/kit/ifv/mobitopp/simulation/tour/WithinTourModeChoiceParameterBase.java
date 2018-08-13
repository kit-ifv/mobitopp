package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class WithinTourModeChoiceParameterBase extends WithinTourModeChoiceAttributes 
	implements WithinTourModeChoiceParameter {

	protected final Map<String,Double> parameterGeneric = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterWalk = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterBike = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterCar = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterPassenger = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterPt = new LinkedHashMap<String,Double>();

	public WithinTourModeChoiceParameterBase() {
		super();
	}

}