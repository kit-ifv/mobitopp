package edu.kit.ifv.mobitopp.visum;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import edu.kit.ifv.mobitopp.data.FloatMatrix;
import edu.kit.ifv.mobitopp.data.Matrix;
import edu.kit.ifv.mobitopp.data.ZoneId;

public class VisumMatrix implements Matrix<Float> {

	private static final float defaultValue = 0.0f;
	
	private final FloatMatrix internal;

	private VisumMatrix(List<ZoneId> zones) {
		internal = new FloatMatrix(zones, defaultValue);
	}

	public VisumMatrix(FloatMatrix matrix) {
		super();
		this.internal = matrix;
	}

	public static VisumMatrix loadFrom(String filename) {
		File file = new File(filename);
		return loadFrom(file);
	}

	public static VisumMatrix loadFrom(File file) {
		try {
			FloatMatrix matrix = VisumMatrixParser.load(file).parseMatrix();
			return new VisumMatrix(matrix);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

  public Float get(int row, int col) {
		return internal.get(row, col);
	}

	public Float get(ZoneId origin, ZoneId destination) {
		return internal.get(origin, destination);
	}

  @Override
  public void set(int row, int col, Float val) {
		throw new java.lang.UnsupportedOperationException();
	}

  @Override
 	public  List<ZoneId> ids() {
		return internal.ids();
	}

	public VisumMatrix scaledMatrix(float factor) {
		
		VisumMatrix matrix = new VisumMatrix(internal.ids());

		for (ZoneId source : internal.ids()) {
			for (ZoneId destination : internal.ids()) {

				Float value = internal.get(source, destination);

				matrix.internal.set(source, destination, factor*value);
			}
		}

		return matrix;
	}

	public void scale(float factor) {
		
		for (ZoneId source : internal.ids()) {
			for (ZoneId destination : internal.ids()) {

				Float value = internal.get(source, destination);

				internal.set(source, destination, factor*value);
			}
		}
	}

	public void add(VisumMatrix matrix) {
		
		for (ZoneId source : internal.ids()) {
			for (ZoneId destination : internal.ids()) {

				Float value = internal.get(source, destination);
				Float increment = matrix.get(source, destination);

				internal.set(source, destination, value+increment);
			}
		}
	}

	public void add(int source, int destination, float value) {

		Float current = internal.get(source, destination);

		internal.set(source, destination, current + value);
	}

	public float totalSum() {

		float sum = 0.0f;

		for (ZoneId source : internal.ids()) {
			for (ZoneId destination : internal.ids()) {
				sum+=internal.get(source,destination);
			}
		}

		return sum;
	}

}
