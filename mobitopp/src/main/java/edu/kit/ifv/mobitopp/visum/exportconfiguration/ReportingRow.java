package edu.kit.ifv.mobitopp.visum.exportconfiguration;

import static java.util.stream.Collectors.joining;

import java.util.LinkedHashSet;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class ReportingRow extends Row {

	private final LinkedHashSet<String> requestedKeys;
	private final int value;

	public ReportingRow(Map<String, String> defaultValues, int offset) {
		super(defaultValues);
		this.value = offset;
		requestedKeys = new LinkedHashSet<>();
	}
	
	private void addRequestedKey(String key) {
		requestedKeys.add(key);
	}

	public boolean containsAttribute(String key) {
		return true;
	}

	public String get(String key) {
		addRequestedKey(key);
		if (super.containsAttribute(key)) {
			return super.get(key);
		}
		return String.valueOf(value);
	}

	public int valueAsInteger(String key) {
		addRequestedKey(key);
		if (super.containsAttribute(key)) {
			return super.valueAsInteger(key);
		}
		return value;
	}

	public float valueAsFloat(String key) {
		addRequestedKey(key);
		if (super.containsAttribute(key)) {
			return super.valueAsFloat(key);
		}
		return value;
	}

	public double valueAsDouble(String key) {
		addRequestedKey(key);
		if (super.containsAttribute(key)) {
			return super.valueAsDouble(key);
		}
		return value;
	}

	public String serialise() {
		return requestedKeys.stream().collect(joining(";"));
	}
	
}
