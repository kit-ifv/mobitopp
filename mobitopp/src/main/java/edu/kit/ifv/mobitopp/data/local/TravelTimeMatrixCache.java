package edu.kit.ifv.mobitopp.data.local;

import java.io.IOException;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.MatrixConfiguration;
import edu.kit.ifv.mobitopp.data.local.configuration.TaggedTravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixId;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.time.Time;

public class TravelTimeMatrixCache extends MatrixCache<TravelTimeMatrixId, TaggedTravelTimeMatrix> {

	private int cachedHour;
	private TravelTimeMatrix[] cachedMatrices;

	public TravelTimeMatrixCache(MatrixConfiguration configuration) {
		super(configuration);
		createEmptyCache();
	}

	public TravelTimeMatrixId idOf(StandardMode mode, Time date) {
		return configuration().idOf(mode, date);
	}

	public TravelTimeMatrix matrixFor(StandardMode mode, Time date) {
		if (this.cachedHour != date.getHour()) {
			createEmptyCache();
		}
		if (null != cachedMatrices[mode.ordinal()]) {
			return cachedMatrices[mode.ordinal()];
		}
		return updateCache(mode, date);
	}

	private TravelTimeMatrix updateCache(StandardMode mode, Time date) {
		TravelTimeMatrixId id = idOf(mode, date);
		TravelTimeMatrix matrix = matrixFor(id).matrix();
		cachedMatrices[mode.ordinal()] = matrix;
		cachedHour = date.getHour();
		return matrix;
	}

	private void createEmptyCache() {
		cachedMatrices = new TravelTimeMatrix[StandardMode.values().length];
	}

	protected TaggedTravelTimeMatrix loadMatrixBy(TravelTimeMatrixId id) throws IOException {
		return configuration().travelTimeMatrixFor(id);
	}

	@Override
	protected Stream<TravelTimeMatrixId> split(TaggedTravelTimeMatrix taggedMatrix) {
		return taggedMatrix.id().split();
	}

}
