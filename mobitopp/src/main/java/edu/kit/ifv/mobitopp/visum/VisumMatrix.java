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
  public List<ZoneId> ids() {
    return internal.ids();
  }

  /**
   * Returns a new matrix by scaling the values of this matrix using the given factor.
   * 
   * @param factor
   *          to scale the matrix
   * @return scaled matrix
   */
  public VisumMatrix scale(float factor) {
    VisumMatrix matrix = new VisumMatrix(internal.ids());
    for (ZoneId source : internal.ids()) {
      for (ZoneId destination : internal.ids()) {
        float value = internal.get(source, destination);
        matrix.internal.set(source, destination, factor * value);
      }
    }
    return matrix;
  }

  /**
   * Returns a new matrix by adding the values of this and the given matrix.
   * 
   * @param other
   *          matrix to add
   * @return added matrix
   */
  public VisumMatrix add(VisumMatrix other) {
    VisumMatrix matrix = new VisumMatrix(internal.ids());
    for (ZoneId source : internal.ids()) {
      for (ZoneId destination : internal.ids()) {
        float value = internal.get(source, destination);
        float increment = other.get(source, destination);
        matrix.internal.set(source, destination, value + increment);
      }
    }
    return matrix;
  }

  /**
   * Returns a new matrix by adding the given value to the values of this matrix.
   * 
   * @param increment
   *          value to add to the values of this matrix
   * @return added matrix
   */
  public VisumMatrix add(float increment) {
    VisumMatrix matrix = new VisumMatrix(internal.ids());
    for (ZoneId source : internal.ids()) {
      for (ZoneId destination : internal.ids()) {
        float value = internal.get(source, destination);
        float newValue = value + increment;
        matrix.internal.set(source, destination, newValue);
      }
    }
    return matrix;
  }

  /**
   * Returns a new matrix by calculating the minimum of the values of this and the given matrix.
   * 
   * @param other
   *          matrix for minimum calculation
   * @return matrix containing the minimum values of both matrices
   */
  public VisumMatrix min(VisumMatrix other) {
    VisumMatrix matrix = new VisumMatrix(internal.ids());
    for (ZoneId source : internal.ids()) {
      for (ZoneId destination : internal.ids()) {
        float value = internal.get(source, destination);
        float increment = other.get(source, destination);
        float newValue = Math.min(value, increment);
        matrix.internal.set(source, destination, newValue);
      }
    }
    return matrix;
  }

  /**
   * Calculates the sum of all matrix elements.
   * 
   * @return sum of all matrix elements
   */
  public float totalSum() {
    float sum = 0.0f;
    for (ZoneId source : internal.ids()) {
      for (ZoneId destination : internal.ids()) {
        sum += internal.get(source, destination);
      }
    }
    return sum;
  }

  public int numberOfElements() {
    return ids().size() * ids().size();
  }

}
