package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

class StopNotReachable extends Exception {

	private static final long serialVersionUID = 6172489978150335031L;

	public StopNotReachable(Stop stop) {
		super("Stop is missing: " + stop.toString());
	}

}
