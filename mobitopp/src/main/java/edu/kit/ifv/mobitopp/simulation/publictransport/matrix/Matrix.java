package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.Duration;
import java.util.List;

import edu.kit.ifv.mobitopp.data.FloatMatrix;
import edu.kit.ifv.mobitopp.data.Zone;

public class Matrix {

	public static final Duration infinite = Duration.of(999999, MINUTES);
	private final FloatMatrix matrix;

	public Matrix(List<Integer> oids) {
		super();
		matrix = new FloatMatrix(oids, inSeconds(infinite));
	}

	public void set(Zone origin, Zone destination, Duration travelTime) {
		matrix.set(origin.getOid(), destination.getOid(), inSeconds(travelTime));
	}

	void set(Zone origin, Zone destination, float seconds) {
		matrix.set(origin.getOid(), destination.getOid(), seconds);
	}

	public Duration get(Zone origin, Zone destination) {
		return Duration.of((long) travelTime(origin, destination), MINUTES);
	}

	private float travelTime(Zone origin, Zone destination) {
		return matrix.get(origin.getOid(), destination.getOid());
	}

	public FloatMatrix toFloatMatrix() {
		return this.matrix;
	}

	private float inSeconds(Duration travelTime) {
		return travelTime.toMinutes();
	}

}
