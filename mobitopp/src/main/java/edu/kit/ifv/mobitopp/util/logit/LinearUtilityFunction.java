package edu.kit.ifv.mobitopp.util.logit;

import java.util.Collections;
import java.util.Map;


public class LinearUtilityFunction
{

	private final Map<String,Double> parameters;


	public LinearUtilityFunction(Map<String,Double> parameters) {

		assert parameters != null;
		assert !parameters.isEmpty() : parameters;

		this.parameters = Collections.unmodifiableMap(parameters);
	}


	public Double calculateUtility(Double scaling, Map<String,Double> attributes) {
		
		assert attributes != null;

		Double result = 0.0;

		for (String attribute: this.parameters.keySet()) {
			assert attributes.get(attribute) != null : ("attribute " + attribute + " not found\n" + attributes + "\n" + this.parameters) ;
			assert this.parameters.get(attribute) != null : ("parameter " + attribute + " not found \n" + this.parameters);

			assert attributes.containsKey(attribute) : (attribute + "\n" + attributes);

			result += this.parameters.get(attribute) * attributes.get(attribute);
		}

		return scaling*result;
	}

	public Double calculateUtility(Map<String,Double> attributes) {
		return calculateUtility(1.0, attributes);
	}

}
