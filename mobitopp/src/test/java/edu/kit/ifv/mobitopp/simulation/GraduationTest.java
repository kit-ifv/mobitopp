package edu.kit.ifv.mobitopp.simulation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class GraduationTest {

  @Test
  void resolveTypeByNumeric() throws Exception {
    assertAll(Arrays.stream(Graduation.values()).map(this::evaluate));
  }

  private Executable evaluate(Graduation graduation) {
    Graduation typeFromNumeric = Graduation.getTypeFromNumeric(graduation.getNumeric());
    return () -> assertThat(typeFromNumeric, is(equalTo(graduation)));
  }
}
