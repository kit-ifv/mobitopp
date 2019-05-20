package edu.kit.ifv.mobitopp.visum;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.FloatMatrix;
import edu.kit.ifv.mobitopp.data.ZoneId;

public class VisumMatrixTest {

  @Test
  void countsNumberOfElements() throws Exception {
    VisumMatrix matrix = new VisumMatrix(new FloatMatrix(oids(), 0.0f));
    
    int dimension = oids().size();
    int numberOfElements = dimension * dimension;
    assertThat(matrix.numberOfElements(), is(numberOfElements));
  }
  
  @Test
  void calculatesSumOfElements() throws Exception {
    float value = 1.0f;
    VisumMatrix matrix = new VisumMatrix(new FloatMatrix(oids(), value));
    
    int dimension = oids().size();
    int numberOfElements = dimension * dimension;
    float sum = value * numberOfElements;
    assertThat(matrix.totalSum(), is(sum));
  }
  
  @Test
  void addsConstant() throws Exception {
    VisumMatrix matrix = new VisumMatrix(new FloatMatrix(oids(), 0.0f));

    float summand = 1.0f;
    VisumMatrix newMatrix = matrix.add(summand);

    float expectedSum = matrix.totalSum() + summand * matrix.numberOfElements();
    assertThat(newMatrix.totalSum(), is(equalTo(expectedSum)));
    assertAll(oids()
        .stream()
        .flatMap(o -> oids().stream().map(d -> newMatrix.get(o, d)))
        .map(value -> () -> assertThat(value, is(equalTo(summand)))));
  }

  @Test
  void addMatrices() throws Exception {
    float someValue = 1.0f;
    float otherValue = 2.0f;
    float sum = someValue + otherValue;
    VisumMatrix someMatrix = new VisumMatrix(new FloatMatrix(oids(), someValue));
    VisumMatrix otherMatrix = new VisumMatrix(new FloatMatrix(oids(), otherValue));

    VisumMatrix newMatrix = someMatrix.add(otherMatrix);

    float expectedSum = someMatrix.totalSum() + otherMatrix.totalSum();
    assertThat(newMatrix.totalSum(), is(equalTo(expectedSum)));
    assertAll(oids()
        .stream()
        .flatMap(o -> oids().stream().map(d -> newMatrix.get(o, d)))
        .map(value -> () -> assertThat(value, is(equalTo(sum)))));
  }

  @Test
  void minMatrices() throws Exception {
    float someValue = 1.0f;
    float otherValue = 2.0f;
    float min = Math.min(someValue, otherValue);
    VisumMatrix someMatrix = new VisumMatrix(new FloatMatrix(oids(), someValue));
    VisumMatrix otherMatrix = new VisumMatrix(new FloatMatrix(oids(), otherValue));

    VisumMatrix minMatrix = someMatrix.min(otherMatrix);

    assertThat(minMatrix.totalSum(), is(equalTo(someMatrix.totalSum())));
    assertAll(oids()
        .stream()
        .flatMap(o -> oids().stream().map(d -> minMatrix.get(o, d)))
        .map(value -> () -> assertThat(value, is(equalTo(min)))));
  }

  private List<ZoneId> oids() {
    return asList(new ZoneId("1", 0), new ZoneId("2", 1));
  }
}
