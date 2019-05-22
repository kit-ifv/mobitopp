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

public class IntegerMatrixTest {

  private final static int DEFAULT_VALUE = 17;

  private IntegerMatrix matrix;

  private List<ZoneId> ids;

  @BeforeEach
  public void setUp() {
    ids = columns().map(this::newZoneId).collect(toList());
    this.matrix = new IntegerMatrix(ids, DEFAULT_VALUE);
  }

  private Stream<Integer> columns() {
    return Stream.of(0, 1, 2, 3);
  }

  private ZoneId newZoneId(int id) {
    return new ZoneId("" + id, id);
  }

  @Test
  public void constructor() {
    assertNotNull(matrix);
    assertEquals(ids.size(), matrix.ids().size());
  }

  @Test
  public void oids() {
    assertAll(
        columns().map(this::newZoneId).map(id -> () -> assertTrue(matrix.ids().contains(id))));
    assertFalse(matrix.ids().contains(newZoneId(5)));

    assertTrue(matrix.ids().containsAll(ids));
    assertTrue(ids.containsAll(matrix.ids()));
  }

  @Test
  public void set() {
    matrix.set(0, 0, 10);
    matrix.set(0, 1, 11);
    matrix.set(0, 2, 12);
    matrix.set(0, 3, 13);

    matrix.set(1, 0, 21);
    matrix.set(2, 0, 31);
    matrix.set(3, 0, 41);

    assertEquals(10, matrix.get(0, 0));
    assertEquals(11, matrix.get(0, 1));
    assertEquals(12, matrix.get(0, 2));
    assertEquals(13, matrix.get(0, 3));

    assertNotNull(matrix.get(3, 3));
    assertEquals(DEFAULT_VALUE, matrix.get(3, 3));

    assertEquals(21, matrix.get(1, 0));
    assertEquals(31, matrix.get(2, 0));
    assertEquals(41, matrix.get(3, 0));
  }
  
  @Test
  void increment() throws Exception {
    int row = 0;
    int column = 0;
    int before = matrix.get(row, column);
    
    matrix.increment(row, column);
    int current = matrix.get(row, column);
    
    assertEquals(before + 1, current);
  }
  
  @Test
  void addValue() throws Exception {
    ZoneId row = newZoneId(0);
    ZoneId column = newZoneId(0);
    int increment = 1;
    int before = matrix.get(row, column);
    
    matrix.add(row, column, increment);
    int current = matrix.get(row, column);
    
    assertEquals(before + increment, current);
  }
}
