package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

public class FullIntermodal implements Mode {

	private final StandardMode mainMode;
	private final StandardMode accessMode;
	private final StandardMode egressMode;

	public FullIntermodal(StandardMode mainMode, StandardMode accessMode, StandardMode egressMode) {
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
	public StandardMode mainMode() {
		return mainMode;
	}
	
	@Override
	public StandardMode legMode() {
		throw new UnsupportedOperationException(
			String.format("Could not determine a leg mode for an intermodal mode: %s", this));
	}

	public Mode accessMode() {
		return new LegMode(this, accessMode);
	}
	
	public Mode egressMode() {
		return new LegMode(this, egressMode);
	}
	
	@Override
	public String toString() {
		return accessMode + "," + mainMode + "," + egressMode;
	}
	
}
