package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class AccessIntermodal implements Mode {

	private final Mode main;
	private final Mode access;

	public AccessIntermodal(Mode mainMode, Mode accessMode) {
		this.main = mainMode;
		this.access = accessMode;
	}

	@Override
	public boolean isFlexible() {
		return main.isFlexible();
	}

	@Override
	public boolean isDefined() {
		return main.isDefined();
	}

	@Override
	public boolean usesCarAsDriver() {
		return main.usesCarAsDriver();
	}

	@Override
	public String forLogging() {
		return access.forLogging() + "," + main.forLogging();
	}

	@Override
	public Mode mainMode() {
		return main;
	}

	@Override
	public Mode legMode() {
		return access;
	}

}
