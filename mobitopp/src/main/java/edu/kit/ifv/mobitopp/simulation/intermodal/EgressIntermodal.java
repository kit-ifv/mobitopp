package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import lombok.ToString;

@ToString
public class EgressIntermodal implements Mode {

	private final StandardMode main;
	private final StandardMode egress;

	public EgressIntermodal(StandardMode mainMode, StandardMode egressMode) {
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
	public StandardMode mainMode() {
		return main;
	}
	
	@Override
	public StandardMode legMode() {
		return egress;
	}

}
