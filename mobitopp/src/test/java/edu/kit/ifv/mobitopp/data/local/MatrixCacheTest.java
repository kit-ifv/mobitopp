package edu.kit.ifv.mobitopp.data.local;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.local.configuration.MatrixConfiguration;

public class MatrixCacheTest {

	private static final MatrixConfiguration notUsed = null;

	private MatrixCache<Object, Object> cache;
	private List<Object> splittedIds;
	private Object id;
	private Object matrix;
	private Object someId;
	private Object anotherId;

	@Before
	public void initialise() {
		cache = newCache();
		id = new Object();
		matrix = new Object();
		someId = new Object();
		anotherId = new Object();
		splittedIds = asList(someId, anotherId);
	}

	@Test
	public void cachesMatricesForSplittedIds() {
		Object complete = cache.matrixFor(id);
		Object some = cache.matrixFor(someId);
		Object another = cache.matrixFor(anotherId);

		assertThat(complete, is(equalTo(matrix)));
		assertThat(some, is(equalTo(matrix)));
		assertThat(another, is(equalTo(matrix)));
	}

	private MatrixCache<Object, Object> newCache() {
		return new MatrixCache<Object, Object>(notUsed) {

			@Override
			protected Stream<Object> split(Object id) {
				return splittedIds.stream();
			}

			@Override
			protected Object loadMatrixBy(Object id) throws IOException {
				if (MatrixCacheTest.this.id == id) {
					return matrix;
				}
				return new RuntimeException("Only main matrix should be loaded.");
			}
		};
	}
}
