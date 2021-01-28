package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import lombok.ToString;

@ToString
public class AccessIntermodal implements Mode {

	private final StandardMode main;
	private final StandardMode access;

	public AccessIntermodal(StandardMode mainMode, StandardMode accessMode) {
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
	public StandardMode mainMode() {
		return main;
	}

	@Override
	public StandardMode legMode() {
		return access;
	}

}
