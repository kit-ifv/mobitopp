package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class FullIntermodal implements Mode {

	private final Mode mainMode;
	private final Mode accessMode;
	private final Mode egressMode;

	public FullIntermodal(Mode mainMode, Mode accessMode, Mode egressMode) {
		this.mainMode = mainMode;
		this.accessMode = accessMode;
		this.egressMode = egressMode;
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
		return accessMode.forLogging() + "," + mainMode.forLogging() + "," + egressMode.forLogging();
	}
	
	@Override
	public Mode mainMode() {
		return mainMode;
	}
	
}
