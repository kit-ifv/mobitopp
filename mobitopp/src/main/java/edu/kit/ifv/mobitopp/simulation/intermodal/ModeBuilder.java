package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

public class ModeBuilder {

	private StandardMode mainMode;
	private StandardMode accessMode;
	private StandardMode egressMode;

	public ModeBuilder addMainMode(StandardMode mainMode) {
		this.mainMode = mainMode;
		return this;
	}

	public ModeBuilder addAccessMode(StandardMode accessMode) {
		this.accessMode = accessMode;
		return this;
	}

	public ModeBuilder addEgressMode(StandardMode egressMode) {
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
