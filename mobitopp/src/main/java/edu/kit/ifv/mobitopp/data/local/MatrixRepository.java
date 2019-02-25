package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType.constant;
import static edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType.distance;
import static edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType.parking;
import static edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType.parkingstress;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType;
import edu.kit.ifv.mobitopp.data.local.configuration.MatrixConfiguration;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class MatrixRepository implements Matrices {

	private final TravelTimeMatrixCache travelTimeCache;
	private final CostMatrixCache costCache;
	private final FixedDistributionMatrixCache fixedDistributionCache;

	public MatrixRepository(MatrixConfiguration configuration, TypeMapping modeToType) {
		super();
		costCache = new CostMatrixCache(configuration);
		fixedDistributionCache = new FixedDistributionMatrixCache(configuration);
		travelTimeCache = new TravelTimeMatrixCache(configuration, modeToType);
	}

	@Override
	public TravelTimeMatrix travelTimeFor(Mode mode, Time date) {
		return travelTimeCache.matrixFor(mode, date);
	}
	
	@Override
	public FixedDistributionMatrix fixedDistributionMatrixFor(ActivityType activityType) {
		return fixedDistributionCache.matrixFor(activityType);
	}

	@Override
	public CostMatrix travelCostFor(Mode mode, Time date) {
		return costMatrix(costMatrixTypeOf(mode), date);
	}

	@Override
	public CostMatrix distanceMatrix(Time date) {
		return costMatrix(distance, date);
	}

	@Override
	public CostMatrix parkingCostMatrix(Time date) {
		return costMatrix(parking, date);
	}

	@Override
	public CostMatrix parkingStressMatrix(Time date) {
		return costMatrix(parkingstress, date);
	}

	@Override
	public CostMatrix constantMatrix(Time date) {
		return costMatrix(constant, date);
	}

	private CostMatrix costMatrix(CostMatrixType matrixType, Time date) {
		return costMatrixInternal(matrixType, date);
	}

	private CostMatrix costMatrixInternal(CostMatrixType matrixType, Time date) {
		return costCache.matrixFor(matrixType, date);
	}

	private CostMatrixType costMatrixTypeOf(Mode mode) {
		return costCache.typeOf(mode);
	}

}
