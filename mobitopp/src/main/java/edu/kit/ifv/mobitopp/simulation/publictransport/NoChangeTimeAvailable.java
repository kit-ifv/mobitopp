package edu.kit.ifv.mobitopp.simulation.publictransport;

class NoChangeTimeAvailable extends RuntimeException {

	private static final long serialVersionUID = 7188219767054567088L;

	NoChangeTimeAvailable(int stopId) {
		super(String.valueOf(stopId));
	}

}
