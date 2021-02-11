package edu.kit.ifv.mobitopp.util.parameter;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Coefficient {

	final public String name;
	final public String value;

	public Coefficient(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public int asInt() {
		return new ParameterEvaluator().evaluateAsInt(value);
	}

	public double asDouble() {
		double asDouble = new ParameterEvaluator().evaluateAsDouble(value);
		if (Double.isInfinite(asDouble) || Double.isNaN(asDouble)) {
			throw warn(new IllegalArgumentException("Value is out of limits: " + name), log);
		}
		return asDouble;
	}

	public String toString() {
		return "'" + name + "': " + value;
	}

}