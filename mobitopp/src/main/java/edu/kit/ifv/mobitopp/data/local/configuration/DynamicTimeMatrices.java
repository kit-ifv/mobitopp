package edu.kit.ifv.mobitopp.data.local.configuration;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DayType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicTimeMatrices<S> {

	private final Map<S, TypeMatrices> matrices;

	public DynamicTimeMatrices() {
		super();
		matrices = new HashMap<>();
	}

	public DynamicTimeMatrices(Map<S, TypeMatrices> matrices) {
		super();
		this.matrices = matrices;
	}

	public void add(S matrixType, DayType dayType, TimeSpan timeSpan, String path) {
		TypeMatrices modeMatrices = matrices.getOrDefault(matrixType, new TypeMatrices());
		modeMatrices.add(dayType, timeSpan, path);
		matrices.put(matrixType, modeMatrices);
	}

	public Map<S, TypeMatrices> asMap() {
		return Collections.unmodifiableMap(matrices);
	}

	public TypeMatrices matrixFor(S matrixType) {
		if (matrices.containsKey(matrixType)) {
			return matrices.get(matrixType);
		}
		throw warn(new IllegalArgumentException("No matrix found for " + matrixType), log);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matrices == null) ? 0 : matrices.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		DynamicTimeMatrices other = (DynamicTimeMatrices) obj;
		if (matrices == null) {
			if (other.matrices != null)
				return false;
		} else if (!matrices.equals(other.matrices))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [matrices=" + matrices + "]";
	}

}
