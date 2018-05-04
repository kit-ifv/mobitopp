package edu.kit.ifv.mobitopp.simulation.external;

import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class HourlyTimeProfile {

	private final List<Float> values;
	private final DiscreteRandomVariable<Integer> randomVariable;

	final static public HourlyTimeProfile WIDE = new HourlyTimeProfile(
		Arrays.asList(
			 							0.0f, 0.0f, 0.0f, 0.0f, 0.05f, 0.05f,
									 	0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f,
									 	0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f,
									 	0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f
		)
	);

	final static public HourlyTimeProfile DEFAULT = new HourlyTimeProfile(
		Arrays.asList(
			 							0.0f,  0.0f,  0.0f,  0.0f,  0.0f,  0.0f,
									 	0.05f, 0.05f, 0.1f,  0.1f,  0.1f,  0.1f,
									 	0.1f,  0.1f,  0.1f,  0.1f,  0.05f, 0.05f,
									 	0.0f,  0.0f,  0.0f,  0.0f,  0.0f,  0.0f
		)
	);



	public HourlyTimeProfile(List<Float> values) {
		verifyHours(values);
		this.values = new ArrayList<>(values);
		this.randomVariable = makeRandomVariable(values);
	}

	private void verifyHours(List<Float> values) {
		if (24 != values.size()) {
			throw new IllegalArgumentException(
					"Incorrect number of elements in profile. Must be 24 elements, but was " + values.size());
		}
	}

	private static DiscreteRandomVariable<Integer> makeRandomVariable(List<Float> values) {
		Map<Integer,Float> map = new TreeMap<Integer,Float>();

		for (int i=0; i<values.size(); i++) {
			map.put(i, values.get(i));
		}

		return new DiscreteRandomVariable<Integer>(map);
	}


	public float valueForHour(int hour) {
		verifyHour(hour);
		return this.values.get(hour);
	}

	private void verifyHour(int hour) {
		if (0 > hour || 24 <= hour) {
			throw new IllegalArgumentException("Hour must be between 0 and 23, but was " + hour);
		}
	}

	public int randomHour(Float random) {
		return randomVariable.realization(random);
	}

	public String toString() {
		return values.toString();
	}

}
