package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class ProfileMissing extends IllegalArgumentException {

	private static final long serialVersionUID = -8727254918788080510L;

	public ProfileMissing(Stop target) {
		super("Profile for stop missing: " + target);
	}

}
