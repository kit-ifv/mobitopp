package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class EgressIntermodal implements Mode {

	private final Mode main;
	private final Mode egress;

	public EgressIntermodal(Mode mainMode, Mode egressMode) {
		this.main = mainMode;
		this.egress = egressMode;
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
		return main.forLogging() + "," + egress.forLogging();
	}

	@Override
	public Mode mainMode() {
		return main;
	}
	
	@Override
	public Mode legMode() {
		return egress;
	}

}
