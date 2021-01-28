package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ModeDecorator implements Mode {

	private final Mode other;

	@Override
	public boolean isFlexible() {
		return other.isFlexible();
	}

	@Override
	public boolean isDefined() {
		return other.isDefined();
	}

	@Override
	public boolean usesCarAsDriver() {
		return other.usesCarAsDriver();
	}

	@Override
	public String forLogging() {
		return other.forLogging();
	}

	@Override
	public StandardMode mainMode() {
		return other.mainMode();
	}

	@Override
	public StandardMode legMode() {
		return other.legMode();
	}

}