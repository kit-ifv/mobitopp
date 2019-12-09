package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class EgressIntermodal implements Mode {

	private final Mode mainMode;
	private final Mode egressMode;

	public EgressIntermodal(Mode mainMode, Mode egressMode) {
		this.mainMode = mainMode;
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
		return mainMode.forLogging() + "," + egressMode.forLogging();
	}

	@Override
	public Mode mainMode() {
		return mainMode;
	}

}
