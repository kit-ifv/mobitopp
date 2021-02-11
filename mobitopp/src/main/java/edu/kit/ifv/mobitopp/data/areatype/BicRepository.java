package edu.kit.ifv.mobitopp.data.areatype;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BicRepository implements AreaTypeRepository {

	@Override
	public AreaType getTypeForCode(int code) {
		for (ZoneAreaType type : ZoneAreaType.values()) {
			if (type.getTypeAsInt() == code) {
				return type;
			}
		}

		throw warn(new IllegalArgumentException("No type found for int: " + code), log);
	}

	@Override
	public AreaType getTypeForName(String name) {
		for (ZoneAreaType type : ZoneAreaType.values()) {
			if (type.getTypeAsString().equals(name)) {
				return type;
			}
		}

		throw warn(new IllegalArgumentException("No type found for String: " + name), log);
	}

	@Override
	public AreaType getDefault() {
		return ZoneAreaType.DEFAULT;
	}

}
