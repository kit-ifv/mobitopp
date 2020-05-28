package edu.kit.ifv.mobitopp.dataimport;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.Attractivities;

public class AttractivitiesData {

	private final StructuralData data;

	public AttractivitiesData(StructuralData data) {
		this.data = data;
	}

	public boolean hasValue(String zoneId, String key) {
		return data.hasValue(zoneId, key);
	}

	public int valueOrDefault(String zoneId, String key) {
		return data.valueOrDefault(zoneId, key);
	}

	public Map<Integer, Attractivities> build() {
		Map<Integer, Attractivities> mapping = new LinkedHashMap<>();
		while (hasNext()) {
			int currentZone = currentZone();
			String zoneId = String.valueOf(currentZone);
			Attractivities attractivities = new AttractivitiesBuilder(this).attractivities(zoneId);
			mapping.put(currentZone, attractivities);
			next();
		}
		return mapping;
	}

	private boolean hasNext() {
		return data.hasNext();
	}

	private int currentZone() {
		return data.currentRegion();
	}

	private void next() {
		data.next();
	}

}
