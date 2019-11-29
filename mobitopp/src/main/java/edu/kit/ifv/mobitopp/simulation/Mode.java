package edu.kit.ifv.mobitopp.simulation;

public interface Mode {

	public int getTypeAsInt();

	public boolean isFlexible();

	public boolean isDefined();

	public boolean usesCarAsDriver();

}
