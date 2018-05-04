package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.Map;

public class DynamicParameters {

	private final Map<String, String> parameters;

	public DynamicParameters(Map<String, String> parameters) {
		super();
		this.parameters = parameters;
	}

	public int valueAsInteger(String parameter) {
		return Integer.parseInt(value(parameter));
	}

	public String value(String parameter) {
		verifyExisting(parameter);
		return parameters.get(parameter).toString();
	}

	private void verifyExisting(String parameter) {
		if (!parameters.containsKey(parameter)) {
			throw new IllegalArgumentException("Experimental parameter missing: " + parameter);
		}
	}

	public double valueAsDouble(String parameter) {
		return Double.parseDouble(value(parameter));
	}

	public boolean valueAsBoolean(String parameter) {
		return Boolean.parseBoolean(value(parameter));
	}

	@Override
	public String toString() {
		return getClass().getName() + " [parameters=" + parameters + "]";
	}

}
