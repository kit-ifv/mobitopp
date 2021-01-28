package edu.kit.ifv.mobitopp.simulation;

public interface Mode {

	public boolean isFlexible();

	public boolean isDefined();

	public boolean usesCarAsDriver();

	public String forLogging();

	public StandardMode mainMode();

	public StandardMode legMode();

}
