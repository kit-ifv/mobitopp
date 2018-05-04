package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

final class EmptyReader implements ProfileReader {

	private static final ProfileReader instance = new EmptyReader();

	private EmptyReader() {
		super();
	}

	static ProfileReader instance() {
		return instance;
	}

	@Override
	public boolean next() {
		return false;
	}

	@Override
	public Stop readStop() {
		throw new RuntimeException("Should not be called!");
	}

	@Override
	public ArrivalTimeFunction readFunction() {
		throw new RuntimeException("Should not be called!");
	}

	@Override
	public void close() {
	}
}