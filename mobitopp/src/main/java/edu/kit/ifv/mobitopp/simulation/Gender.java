package edu.kit.ifv.mobitopp.simulation;


public enum Gender
{

	MALE(1),
	FEMALE(2)
	;

	private final int numeric;

	private Gender(int numeric) {
		this.numeric=numeric;	
	}

	public int getTypeAsInt() { return this.numeric; }

	public static Gender getTypeFromInt(int numeric) {

		if (numeric==1) { return MALE; }
		if (numeric==2) { return FEMALE; }

		throw new AssertionError();
	}
}
