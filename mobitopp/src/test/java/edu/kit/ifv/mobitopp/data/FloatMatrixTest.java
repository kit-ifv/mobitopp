package edu.kit.ifv.mobitopp.data;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FloatMatrixTest {

	private final static float DEFAULT_VALUE = 17.0f;
	private static final float withMargin = 1e-6f;

	private FloatMatrix matrix;

  private List<ZoneId> ids;

	@BeforeEach
	public void setUp() {
    ids = columns().map(this::newZoneId).collect(toList());
		this.matrix = new FloatMatrix(ids, DEFAULT_VALUE);
	}

  private Stream<Integer> columns() {
    return Stream.of(0, 1, 2, 3);
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
    assertAll(
        columns().map(this::newZoneId).map(id -> () -> assertTrue(matrix.ids().contains(id))));
    assertFalse(matrix.ids().contains(newZoneId(5)));

		assertTrue(matrix.ids().containsAll(ids));
		assertTrue(ids.containsAll(matrix.ids()));
	}

	@Test
	public void testSet() {
		matrix.set(0, 0, 10.0f);
		matrix.set(0, 1, 11.0f);
		matrix.set(0, 2, 12.0f);
		matrix.set(0, 3, 13.0f);

		matrix.set(1, 0, 21.0f);
		matrix.set(2, 0, 31.0f);
		matrix.set(3, 0, 41.0f);

		assertEquals(10.0f, matrix.get(0, 0), withMargin);
		assertEquals(11.0f, matrix.get(0, 1), withMargin);
		assertEquals(12.0f, matrix.get(0, 2), withMargin);
		assertEquals(13.0f, matrix.get(0, 3), withMargin);

		assertNotNull(matrix.get(3, 3));
		assertEquals(DEFAULT_VALUE, matrix.get(3, 3), withMargin);

		assertEquals(21.0f, matrix.get(1, 0), withMargin);
		assertEquals(31.0f, matrix.get(2, 0), withMargin);
		assertEquals(41.0f, matrix.get(3, 0), withMargin);
	}

}
