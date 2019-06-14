package edu.kit.ifv.mobitopp.data.local.configuration;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

class DynamicParametersTest {

  private static final String missingItem = "not existing";
  private static final String valueKey = "value";
  private static final double withinMargin = 1e-6;

  @Test
  void value() {
    String expectedValue = "attribute";
    DynamicParameters parameters = configurationFor(expectedValue);

    String value = parameters.value(valueKey);

    assertThat(value, is(equalTo(expectedValue)));
  }

  @Test
  void valueAsLong() {
    long expectedValue = 1l;
    DynamicParameters parameters = configurationFor(expectedValue);

    long value = parameters.valueAsLong(valueKey);

    assertThat(value, is(equalTo(expectedValue)));
  }

  @Test
  void valueAsInteger() {
    int expectedValue = 1;
    DynamicParameters parameters = configurationFor(expectedValue);

    int value = parameters.valueAsInteger(valueKey);

    assertThat(value, is(equalTo(expectedValue)));
  }

  @Test
  void valueAsDouble() {
    double expectedValue = 1.0d;
    DynamicParameters parameters = configurationFor(expectedValue);

    double value = parameters.valueAsDouble(valueKey);

    assertThat(value, is(closeTo(expectedValue, withinMargin)));
  }

  @Test
  void valueAsFloat() {
    float expectedValue = 1.0f;
    DynamicParameters parameters = configurationFor(expectedValue);

    float value = parameters.valueAsFloat(valueKey);

    assertEquals(expectedValue, value, withinMargin);
  }

  @Test
  void valueAsBoolean() {
    boolean expectedValue = true;
    DynamicParameters parameters = configurationFor(expectedValue);

    boolean value = parameters.valueAsBoolean(valueKey);

    assertThat(value, is(equalTo(expectedValue)));
  }

  @Test
  void valueAsFile() {
    String path = "path/to/file";
    File expected = new File(path);
    DynamicParameters parameters = configurationFor(path);

    File value = parameters.valueAsFile(valueKey);

    assertThat(value, is(equalTo(expected)));
  }

  @Test
  void nonExistingParameter() throws IOException {
    DynamicParameters parameters = new DynamicParameters(Collections.emptyMap());

    assertThrows(IllegalArgumentException.class, () -> parameters.value(missingItem));
  }

  @Test
  void testName() throws Exception {
    String expectedValue = "attribute";
    DynamicParameters parameters = configurationFor(expectedValue);

    assertAll(() -> assertTrue(parameters.hasValue(valueKey)),
        () -> assertFalse(parameters.hasValue(missingItem)));
  }

  private DynamicParameters configurationFor(Object expectedValue) {
    Map<String, String> parameters = Collections
        .singletonMap(valueKey, String.valueOf(expectedValue));
    return new DynamicParameters(parameters);
  }

}
