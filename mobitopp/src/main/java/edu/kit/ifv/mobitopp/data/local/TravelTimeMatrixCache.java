package edu.kit.ifv.mobitopp.data.local;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.MatrixConfiguration;
import edu.kit.ifv.mobitopp.data.local.configuration.TaggedTravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixId;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class TravelTimeMatrixCache extends MatrixCache<TravelTimeMatrixId, TaggedTravelTimeMatrix> {

	private Map<Mode, TravelTimeMatrixType> modeToType;

	public TravelTimeMatrixCache(MatrixConfiguration configuration) {
		super(configuration);
		modeToType = travelTimeMatrixTypes();
	}

	private static Map<Mode, TravelTimeMatrixType> travelTimeMatrixTypes() {
		HashMap<Mode, TravelTimeMatrixType> types = new HashMap<>();
		types.put(Mode.BIKE, TravelTimeMatrixType.bike);
		types.put(Mode.CAR, TravelTimeMatrixType.car);
		types.put(Mode.CARSHARING_FREE, TravelTimeMatrixType.car);
		types.put(Mode.CARSHARING_STATION, TravelTimeMatrixType.car);
		types.put(Mode.PASSENGER, TravelTimeMatrixType.car);
		types.put(Mode.PEDELEC, TravelTimeMatrixType.bike);
		types.put(Mode.PEDESTRIAN, TravelTimeMatrixType.pedestrian);
		types.put(Mode.PUBLICTRANSPORT, TravelTimeMatrixType.publictransport);
		types.put(Mode.TRUCK, TravelTimeMatrixType.truck);
		return types;
	}

	public TravelTimeMatrixType typeOf(Mode mode) {
		if (modeToType.containsKey(mode)) {
			return modeToType.get(mode);
		}
		throw new IllegalArgumentException("No travel time matrix type available for " + mode);
	}

	public TravelTimeMatrixId idOf(TravelTimeMatrixType matrixType, Time date) {
		return configuration().idOf(matrixType, date);
	}

	public TravelTimeMatrix matrixFor(Mode mode, Time date) {
		TravelTimeMatrixId id = idOf(typeOf(mode), date);
		return matrixFor(id).matrix();
	}

	protected TaggedTravelTimeMatrix loadMatrixBy(TravelTimeMatrixId id) throws IOException {
		return configuration().travelTimeMatrixFor(id);
	}

	@Override
	protected Stream<TravelTimeMatrixId> split(TaggedTravelTimeMatrix taggedMatrix) {
		return taggedMatrix.id().split();
	}

}
