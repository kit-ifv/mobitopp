package edu.kit.ifv.mobitopp.publictransport.model;

import edu.kit.ifv.mobitopp.time.Time;

public class FootJourney implements Journey {

	public static final Journey footJourney = new FootJourney();
	private static final TransportSystem footSystem = new TransportSystem("foot");
	private static final int unlimited = Integer.MAX_VALUE;
	private static int footId = -1;

	private FootJourney() {
		super();
	}

	@Override
	public int id() {
		return footId;
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
		return unlimited;
	}

	@Override
	public TransportSystem transportSystem() {
		return footSystem;
	}

}
