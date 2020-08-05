package edu.kit.ifv.mobitopp.visum;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.ZoneId;

public class MapBasedMapper implements IdToOidMapper {

	private final Map<String, Integer> mapping;

	public MapBasedMapper(Map<String, Integer> mapping) {
		this.mapping = mapping;
	}

	@Override
	public Integer map(String id) {
		if (mapping.containsKey(id)) {
			return mapping.get(id);
		}
		throw new IllegalArgumentException("Id is unknown: " + id);
	}

	@Override
	public ZoneId mapToZoneId(String id) {
		return new ZoneId(id, map(id));
	}

}
