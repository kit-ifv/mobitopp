package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StaticTimeMatrices<T> {

	private Map<T, String> matrices;

	public StaticTimeMatrices() {
		this(new HashMap<>());
	}

	public StaticTimeMatrices(Map<T, String> matrices) {
		super();
		this.matrices = matrices;
	}

	public void add(T matrixType, String path) {
		matrices.put(matrixType, path);
	}

	public Map<T, String> asMap() {
		return Collections.unmodifiableMap(matrices);
	}

	public StoredMatrix matrixFor(T matrixType) {
		if (matrices.containsKey(matrixType)) {
			String path = matrices.get(matrixType);
			return new StoredMatrix(path);
		}
		throw new IllegalArgumentException("No matrix found for " + matrixType);
	}
}
