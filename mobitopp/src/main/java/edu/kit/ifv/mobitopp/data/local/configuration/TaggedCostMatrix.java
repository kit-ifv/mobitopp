package edu.kit.ifv.mobitopp.data.local.configuration;

import edu.kit.ifv.mobitopp.data.CostMatrix;

public class TaggedCostMatrix {

	private final CostMatrixId id;
	private final CostMatrix matrix;

	public TaggedCostMatrix(CostMatrixId id, CostMatrix matrix) {
		super();
		this.id = id;
		this.matrix = matrix;
	}

	public CostMatrixId id() {
		return id;
	}

	public CostMatrix matrix() {
		return matrix;
	}

}
