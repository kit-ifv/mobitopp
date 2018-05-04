package edu.kit.ifv.mobitopp.data;

public enum ZoneAreaType {

	DEFAULT(0, "DEFAULT"),
	RURAL(1, "RURAL"),
	PROVINCIAL(2, "PROVINCIAL"),
	CITYOUTSKIRT(3, "CITYOUTSKIRT"),
	METROPOLITAN(4, "METROPOLITAN"),
	CONURBATION(5, "CONURBATION");

	private final int typeAsInt;
	private final String name;

	private ZoneAreaType(int typeAsInt, String name) {
		this.typeAsInt = typeAsInt;
		this.name = name;
	}

	public static ZoneAreaType getTypeFromInt(int typeAsInt) {
		for (ZoneAreaType type : values()) {
			if (type.typeAsInt == typeAsInt) {
				return type;
			}
		}

		throw new RuntimeException("No type found for int: " + typeAsInt);
	}

	public static ZoneAreaType getTypeFromString(String typeAsString) {
		for (ZoneAreaType type : values()) {
			if (type.name.equals(typeAsString)) {
				return type;
			}
		}

		throw new RuntimeException("No type found for String: " + typeAsString);
	}

	public int getTypeAsInt() {
		return typeAsInt;
	}

	public String getTypeAsString() {
		return name;
	}

}
