package edu.kit.ifv.mobitopp.util.dataimport;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;

class ZoneValue {

	private final int zoneId;
	private final String areaType;
	private final Map<String, String> mapping;

	public ZoneValue(int zoneId, String areaType, Map<String, String> mapping) {
		super();
		this.zoneId = zoneId;
		this.areaType = areaType;
		this.mapping = mapping;
	}
	
	int zoneId() {
		return zoneId;
	}

	String[] toLine() {
		ArrayList<String> elements = new ArrayList<>();
		elements.add(String.valueOf(zoneId));
		elements.add(areaType);
		for (String constraint : constraints()) {
			elements.add(mapping.getOrDefault(constraint, "0"));
		}
		return elements.toArray(new String[elements.size()]);
	}

	static ZoneValue from(List<Value> values) {
		int zoneId = values
				.stream()
				.map(Value::zoneId)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No values parsed."));
		String areaType = values
				.stream()
				.map(Value::areaType)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No values parsed."));
		Map<String, String> mapping = values.stream().collect(toMap(Value::constraint, Value::amount));
		return new ZoneValue(zoneId, areaType, mapping);
	}

	static void writeHeaderTo(CSVWriter writer) {
		ArrayList<String> elements = new ArrayList<>();
		elements.add("ID");
		elements.add("areaType");
		elements.addAll(constraints());
		String[] header = elements.toArray(new String[elements.size()]);
		writer.writeNext(header);
	}

	private static ArrayList<String> constraints() {
		ArrayList<String> elements = new ArrayList<>();
		for (int size = 1; size <= 5; size++) {
			elements.add(String.format("HHTyp:%s", size));
		}
		elements.add("Age:M:0-5");
		elements.add("Age:M:6-9");
		elements.add("Age:M:10-14");
		elements.add("Age:M:15-17");
		elements.add("Age:M:18-24");
		elements.add("Age:M:25-29");
		elements.add("Age:M:30-44");
		elements.add("Age:M:45-59");
		elements.add("Age:M:60-64");
		elements.add("Age:M:65-74");
		elements.add("Age:M:75-");
		elements.add("Age:F:0-5");
		elements.add("Age:F:6-9");
		elements.add("Age:F:10-14");
		elements.add("Age:F:15-17");
		elements.add("Age:F:18-24");
		elements.add("Age:F:25-29");
		elements.add("Age:F:30-44");
		elements.add("Age:F:45-59");
		elements.add("Age:F:60-64");
		elements.add("Age:F:65-74");
		elements.add("Age:F:75-");
		return elements;
	}
}