package edu.kit.ifv.mobitopp.data.local.configuration;

import org.junit.Test;

public class StoredMatricesTest {

	@Test(expected = IllegalArgumentException.class)
	public void failsOnMissingTravelTimeType() {
		StoredMatrices matrices = new StoredMatrices();

		matrices.travelTimeMatrixFor(TravelTimeMatrixType.car);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnMissingCostType() {
		StoredMatrices matrices = new StoredMatrices();

		matrices.costMatrixFor(CostMatrixType.car);
	}
}
