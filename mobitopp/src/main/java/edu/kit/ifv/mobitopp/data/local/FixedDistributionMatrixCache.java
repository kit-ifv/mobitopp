package edu.kit.ifv.mobitopp.data.local;

import java.io.IOException;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.FixedDistributionMatrixId;
import edu.kit.ifv.mobitopp.data.local.configuration.MatrixConfiguration;
import edu.kit.ifv.mobitopp.data.local.configuration.TaggedFixedDistributionMatrix;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class FixedDistributionMatrixCache
		extends MatrixCache<FixedDistributionMatrixId, TaggedFixedDistributionMatrix> {

	public FixedDistributionMatrixCache(MatrixConfiguration configuration) {
		super(configuration);
	}

	public FixedDistributionMatrix matrixFor(ActivityType activityType) {
		FixedDistributionMatrixId id = configuration().idOf(activityType);
		return matrixFor(id).matrix();
	}

	@Override
	protected TaggedFixedDistributionMatrix loadMatrixBy(FixedDistributionMatrixId id) throws IOException {
		return configuration().fixedDistributionMatrixFor(id);
	}

	@Override
	protected Stream<FixedDistributionMatrixId> split(TaggedFixedDistributionMatrix taggedMatrix) {
		return taggedMatrix.id().split();
	}

}
