package edu.kit.ifv.mobitopp.data;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class FloatMatrixTest {

	private final static float DEFAULT_VALUE = 17.0f;
	private static final float withMargin = 1e-6f;

	private FloatMatrix matrix;

  private List<ZoneId> ids;

	@Before
	public void setUp() {
    ids = Stream.of(1, 2, 3, 4).map(this::newZoneId).collect(toList());
		this.matrix = new FloatMatrix(ids, DEFAULT_VALUE);
	}

  private ZoneId newZoneId(int id) {
    return new ZoneId("" + id, id);
  }

	@Test
	public void testConstructor() {
		assertNotNull(matrix);
		assertEquals(ids.size(), matrix.ids().size());
	}

	@Test
	public void testOids() {
		assertTrue(matrix.ids().contains(newZoneId(1)));
		assertTrue(matrix.ids().contains(newZoneId(2)));
		assertTrue(matrix.ids().contains(newZoneId(3)));
		assertTrue(matrix.ids().contains(newZoneId(4)));
		assertFalse(matrix.ids().contains(newZoneId(5)));

		assertTrue(matrix.ids().containsAll(ids));
		assertTrue(ids.containsAll(matrix.ids()));
	}

	@Test
	public void testSet() {
		matrix.set(1, 1, 10.0f);
		matrix.set(1, 2, 11.0f);
		matrix.set(1, 3, 12.0f);
		matrix.set(1, 4, 13.0f);

		matrix.set(2, 1, 21.0f);
		matrix.set(3, 1, 31.0f);
		matrix.set(4, 1, 41.0f);

		assertEquals(10.0f, matrix.get(1, 1), withMargin);
		assertEquals(11.0f, matrix.get(1, 2), withMargin);
		assertEquals(12.0f, matrix.get(1, 3), withMargin);
		assertEquals(13.0f, matrix.get(1, 4), withMargin);

		assertNotNull(matrix.get(4, 4));
		assertEquals(DEFAULT_VALUE, matrix.get(4, 4), withMargin);

		assertEquals(21.0f, matrix.get(2, 1), withMargin);
		assertEquals(31.0f, matrix.get(3, 1), withMargin);
		assertEquals(41.0f, matrix.get(4, 1), withMargin);
	}

}
