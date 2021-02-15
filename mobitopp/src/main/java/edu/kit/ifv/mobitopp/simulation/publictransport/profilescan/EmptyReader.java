package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		throw warn(new RuntimeException("Should not be called!"), log);
	}

	@Override
	public ArrivalTimeFunction readFunction() {
		throw warn(new RuntimeException("Should not be called!"), log);
	}

	@Override
	public void close() {
	}
}