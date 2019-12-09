package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class ModeBuilder {

	private Mode mainMode;
	private Mode accessMode;
	private Mode egressMode;

	public ModeBuilder addMainMode(Mode mainMode) {
		this.mainMode = mainMode;
		return this;
	}

	public ModeBuilder addAccessMode(Mode accessMode) {
		this.accessMode = accessMode;
		return this;
	}

	public ModeBuilder addEgressMode(Mode egressMode) {
		this.egressMode = egressMode;
		return this;
	}

	public Mode build() {
		if (null != accessMode && null != egressMode) {
			return new FullIntermodal(mainMode, accessMode, egressMode);
		}
		if (null != accessMode && null == egressMode) {
			return new AccessIntermodal(mainMode, accessMode);
		}
		if (null == accessMode && null != egressMode) {
			return new EgressIntermodal(mainMode, egressMode);
		}
		return mainMode;
	}

}
