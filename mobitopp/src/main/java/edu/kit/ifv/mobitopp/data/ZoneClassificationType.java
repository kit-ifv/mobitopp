package edu.kit.ifv.mobitopp.data;

public enum ZoneClassificationType {

	studyArea("STUDYAREA"), extendedStudyArea("EXTENDEDSTUDYAREA"), outlyingArea("OUTLYINGAREA");

	private final String name;

	private ZoneClassificationType(String name) {
		this.name = name;
	}

	public static ZoneClassificationType getTypeFromString(String typeAsString) {
		for (ZoneClassificationType type : values()) {
			if (type.name.equals(typeAsString)) {
				return type;
			}
		}
		throw new IllegalArgumentException("No type found for string: " + typeAsString);
	}

}
