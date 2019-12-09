package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class AccessIntermodal implements Mode {

	private final Mode mainMode;
	private final Mode accessMode;

	public AccessIntermodal(Mode mainMode, Mode accessMode) {
		this.mainMode = mainMode;
		this.accessMode = accessMode;
	}

	@Override
	public boolean isFlexible() {
		return mainMode.isFlexible();
	}

	@Override
	public boolean isDefined() {
		return mainMode.isDefined();
	}

	@Override
	public boolean usesCarAsDriver() {
		return mainMode.usesCarAsDriver();
	}

	@Override
	public String forLogging() {
		return accessMode.forLogging() + "," + mainMode.forLogging();
	}

	@Override
	public Mode mainMode() {
		return mainMode;
	}

}
