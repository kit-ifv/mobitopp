package edu.kit.ifv.mobitopp.simulation;

import java.util.EnumSet;

public enum Employment {

	UNKNOWN(-1),
	FULLTIME(1),
	PARTTIME(2),
	MARGINAL(22),
	UNEMPLOYED(3),
	STUDENT(4),
	STUDENT_PRIMARY(40),
	STUDENT_SECONDARY(41),
	STUDENT_TERTIARY(42),
	EDUCATION(5),
	HOMEKEEPER(6),
	RETIRED(7),
	INFANT(8),
	NONE(9)
	;

	private final int numeric;

	private Employment(int numeric) {
		this.numeric=numeric;	
	}

	public int getTypeAsInt() { return this.numeric; }

	public String getTypeAsString() {
		return toString();
	}

	public static Employment getTypeFromInt(int numeric) {

		for (Employment employment : EnumSet.allOf(Employment.class)) {

			if (employment.getTypeAsInt() ==  numeric) {
				return employment;
			}
		}

		throw new AssertionError("invalid code: " + numeric);
	}

}
