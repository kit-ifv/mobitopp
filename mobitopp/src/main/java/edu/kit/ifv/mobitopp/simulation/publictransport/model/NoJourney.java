package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.TransportSystem;
import edu.kit.ifv.mobitopp.time.Time;

class NoJourney implements ModifiableJourney {

	public static final ModifiableJourney noJourney = new NoJourney();
	
	private static final int defaultId = -2;

	NoJourney() {
		super();
	}

	@Override
	public int id() {
		return defaultId;
	}

	@Override
	public Time day() {
		return Time.future;
	}

	@Override
	public Connections connections() {
		return new Connections();
	}

	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public TransportSystem transportSystem() {
		return new TransportSystem("no system");
	}

	@Override
	public void add(Connection connection) {
	}
}