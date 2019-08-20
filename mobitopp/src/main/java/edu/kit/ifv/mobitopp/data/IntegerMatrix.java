package edu.kit.ifv.mobitopp.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntegerMatrix implements Serializable, Matrix<Integer> {

  private static final long serialVersionUID = 1L;

  private int[][] matrix;
  private final List<ZoneId> ids;
  private final int size;
  private final int defaultValue;

  protected IntegerMatrix(List<ZoneId> ids, int defaultValue) {

    this.ids = Collections.unmodifiableList(new ArrayList<>(ids));
    this.size = ids.size();

    this.defaultValue = defaultValue;

    this.matrix = new int[size][size];

    initMatrix();
  }

  public IntegerMatrix(List<ZoneId> oids) {
    this(oids, 0);
  }

  private void initMatrix() {

    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        this.matrix[i][j] = this.defaultValue;
      }
    }
  }

  @Override
  public List<ZoneId> ids() {
    return this.ids;
  }

  public int get(int row, int column) {
    assert 0 <= row && size > row;
    assert 0 <= column && size > column;

    return this.matrix[row][column];
  }

  @Override
  public Integer get(ZoneId origin, ZoneId destination) {
    return get(origin.getMatrixColumn(), destination.getMatrixColumn());
  }

  @Override
  public void set(int row, int column, Integer value) {
    assert 0 <= row && row < size : ("row=" + row + ", size=" + size);
    assert 0 <= column && column < size : ("column=" + column + ", size=" + size);

    this.matrix[row][column] = value;
  }

  /**
   * Increment the matrix value at row and column by one.
   * 
   * @param row
   * @param column
   */
  public void increment(int row, int column) {
    add(row, column, 1);
  }

  /**
   * Increment the matrix value specified by origin and destination by one.
   * 
   * @param origin
   * @param destination
   * @see #increment(int, int)
   */
  public void increment(ZoneId origin, ZoneId destination) {
    increment(origin.getMatrixColumn(), destination.getMatrixColumn());
  }

  /**
   * Add increment to the matrix value specified by origin and destination
   * 
   * @param origin
   * @param destination
   * @param increment value to add
   */
  public void add(ZoneId origin, ZoneId destination, int increment) {
    add(origin.getMatrixColumn(), destination.getMatrixColumn(), increment);
  }

  private void add(int row, int column, int increment) {
    set(row, column, get(row, column) + increment);
  }

}
