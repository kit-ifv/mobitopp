package edu.kit.ifv.mobitopp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.function.Function;

public abstract class TestUtil {

  public static <T> void assertValue(Function<T, ?> value, T actual, T expected) {
    assertThat(value.apply(actual)).isEqualTo(value.apply(expected));
  }
  
  public static <T, S, R> void assertValues(
      Function<T, Map<S, R>> value, T actual, T expected) {
    assertThat(value.apply(actual).entrySet()).containsAll(value.apply(expected).entrySet());
  }
}
