package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;
import lombok.ToString;

@ToString
class LegMode extends ModeDecorator {

	private final Mode leg;

	public LegMode(Mode complete, Mode leg) {
		super(complete);
		this.leg = leg;
	}
	
	@Override
	public Mode legMode() {
		return leg;
	}
	
}