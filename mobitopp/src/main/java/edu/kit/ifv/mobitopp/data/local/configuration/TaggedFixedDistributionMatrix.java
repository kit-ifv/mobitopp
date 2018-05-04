package edu.kit.ifv.mobitopp.data.local.configuration;

import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;

public class TaggedFixedDistributionMatrix {

	private FixedDistributionMatrixId id;
	private FixedDistributionMatrix matrix;

	public TaggedFixedDistributionMatrix(
			FixedDistributionMatrixId id, FixedDistributionMatrix matrix) {
		super();
		this.id = id;
		this.matrix = matrix;
	}
	
	public FixedDistributionMatrixId id() {
		return id;
	}

	public FixedDistributionMatrix matrix() {
		return matrix;
	}

}
