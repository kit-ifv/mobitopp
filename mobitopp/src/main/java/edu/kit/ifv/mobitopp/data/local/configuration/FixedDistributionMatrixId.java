package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class FixedDistributionMatrixId {

	private final ActivityType type;

	public FixedDistributionMatrixId(ActivityType type) {
		super();
		this.type = type;
	}

	public ActivityType matrixType() {
		return type;
	}

	public Stream<FixedDistributionMatrixId> split() {
		return Stream.of(this);
	}

	@Override
	public String toString() {
		return getClass().getName() + " [type=" + type + "]";
	}

}
