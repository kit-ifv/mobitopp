package edu.kit.ifv.mobitopp.data;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		throw warn(new IllegalArgumentException("No type found for string: " + typeAsString), log);
	}

}
