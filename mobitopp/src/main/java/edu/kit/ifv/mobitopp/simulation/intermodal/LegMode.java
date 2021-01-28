package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import lombok.ToString;

@ToString
class LegMode extends ModeDecorator {

	private final StandardMode leg;

	public LegMode(Mode complete, StandardMode leg) {
		super(complete);
		this.leg = leg;
	}
	
	@Override
	public StandardMode legMode() {
		return leg;
	}
	
}