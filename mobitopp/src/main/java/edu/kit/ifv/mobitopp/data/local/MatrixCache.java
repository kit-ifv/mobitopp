package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.local.configuration.MatrixConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MatrixCache<K, V> {

	private final Map<K, V> cache;
	private MatrixConfiguration configuration;

	public MatrixCache(MatrixConfiguration configuration) {
		super();
		this.configuration = configuration;
		cache = new HashMap<>();
	}

	private boolean contains(K id) {
		return cache.containsKey(id);
	}

	protected V matrixFor(K id) {
		if (contains(id)) {
			return cache.get(id);
		}
		try {
			return loadFrom(id);
		} catch (IOException e) {
			throw warn(new IllegalArgumentException("Can not find matrix for: " + id, e), log);
		}
	}

	protected void add(K id, V matrix) {
		cache.put(id, matrix);
	}
	
	protected MatrixConfiguration configuration() {
		return configuration;
	}

	protected V loadFrom(K id) throws IOException {
		V matrix = loadMatrixBy(id);
		add(id, matrix);
		split(matrix).forEach(singleId -> add(singleId, matrix));
		return matrix;
	}

	protected abstract Stream<K> split(V value);

	protected abstract V loadMatrixBy(K id) throws IOException;

}