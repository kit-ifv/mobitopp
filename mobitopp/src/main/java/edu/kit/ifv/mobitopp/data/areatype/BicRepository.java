package edu.kit.ifv.mobitopp.data.areatype;

public class BicRepository implements AreaTypeRepository {

	@Override
	public AreaType getTypeForCode(int code) {
		for (ZoneAreaType type : ZoneAreaType.values()) {
			if (type.getTypeAsInt() == code) {
				return type;
			}
		}

		throw new IllegalArgumentException("No type found for int: " + code);
	}

	@Override
	public AreaType getTypeForName(String name) {
		for (ZoneAreaType type : ZoneAreaType.values()) {
			if (type.getTypeAsString().equals(name)) {
				return type;
			}
		}

		throw new IllegalArgumentException("No type found for String: " + name);
	}

	@Override
	public AreaType getDefault() {
		return ZoneAreaType.DEFAULT;
	}

}
