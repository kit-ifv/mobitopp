package edu.kit.ifv.mobitopp.data;

/** 
 * DON'T CHANGE the int constants
 * 	To improve the debugging they have the same values as in the panel data
 */
public enum ProjectAreaType {
	DEFAULT(0, "DEFAULT"),
	RURAL(1, "RURAL"),
	PROVINCIAL(2, "PROVINCIAL"),
	CITYOUTSKIRT(3, "CITYOUTSKIRT"),
	METROPOLITAN(4, "METROPOLITAN"),
	CONURBATION(5, "CONURBATION");

	private final int type;
	private String name;

	private ProjectAreaType(int type, String name) {
		this.type = type;
		this.name = name;
	}

	public static ProjectAreaType getTypeFromInt(int typeAsInt) {
		for (ProjectAreaType type : values()) {
			if (type.type == typeAsInt) {
				return type;
			}
		}

		throw new RuntimeException("No type found for int: " + typeAsInt);
	}

	public static ProjectAreaType getTypeFromString(String typeAsString) {
		for (ProjectAreaType type : values()) {
			if (type.name == typeAsString) {
				return type;
			}
		}

		throw new RuntimeException("No type found for string: " + typeAsString);
	}

}
