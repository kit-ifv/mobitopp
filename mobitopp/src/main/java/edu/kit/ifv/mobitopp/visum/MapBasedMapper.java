package edu.kit.ifv.mobitopp.visum;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.ZoneId;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		throw warn(new IllegalArgumentException("Id is unknown: " + id), log);
	}

	@Override
	public ZoneId mapToZoneId(String id) {
		return new ZoneId(id, map(id));
	}

}
