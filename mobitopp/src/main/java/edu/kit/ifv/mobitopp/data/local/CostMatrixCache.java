package edu.kit.ifv.mobitopp.data.local;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixId;
import edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType;
import edu.kit.ifv.mobitopp.data.local.configuration.MatrixConfiguration;
import edu.kit.ifv.mobitopp.data.local.configuration.TaggedCostMatrix;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class CostMatrixCache extends MatrixCache<CostMatrixId, TaggedCostMatrix> {

	private final Map<Mode, CostMatrixType> modeToType;

	public CostMatrixCache(MatrixConfiguration configuration) {
		super(configuration);
		modeToType = modeToTypes();
	}

	private static Map<Mode, CostMatrixType> modeToTypes() {
		HashMap<Mode, CostMatrixType> types = new HashMap<>();
		types.put(Mode.CAR, CostMatrixType.car);
		types.put(Mode.PUBLICTRANSPORT, CostMatrixType.publictransport);
		types.put(Mode.TAXI, CostMatrixType.taxi);
    types.put(Mode.PARK_AND_RIDE, CostMatrixType.park_and_ride);
    types.put(Mode.RIDE_HAILING, CostMatrixType.ride_hailing);
    types.put(Mode.PREMIUM_RIDE_HAILING, CostMatrixType.premium_ride_hailing);
    types.put(Mode.RIDE_POOLING, CostMatrixType.ride_pooling);
    types.put(Mode.TRUCK, CostMatrixType.truck);
		return types;
	}

	public CostMatrixType typeOf(Mode mode) {
		if (modeToType.containsKey(mode)) {
			return modeToType.get(mode);
		}
		throw new IllegalArgumentException("No cost matrix type available for " + mode);
	}

	public CostMatrixId idOf(CostMatrixType matrixType, Time date) {
		return configuration().idOf(matrixType, date);
	}

	public CostMatrix matrixFor(CostMatrixType matrixType, Time date) {
		CostMatrixId id = idOf(matrixType, date);
		return matrixFor(id).matrix();
	}

	protected TaggedCostMatrix loadMatrixBy(CostMatrixId id) throws IOException {
		return configuration().costMatrixFor(id);
	}
	
	@Override
	protected Stream<CostMatrixId> split(TaggedCostMatrix taggedMatrix) {
		return taggedMatrix.id().split();
	}
}
